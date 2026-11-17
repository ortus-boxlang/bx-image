//! MatchBox backend for bx-image: reads PNG, JPEG, GIF, WebP and SVG into RGBA pixels and
//! implements the portable subset of the bx-image API. The JVM implementation ships in the
//! same bx-image package; this plugin is selected only when the module runs on MatchBox.
use matchbox_plugin_sdk::*;
use resvg::{tiny_skia, usvg};
use std::{
    ffi::c_void,
    path::Path,
    sync::{Arc, OnceLock},
};

const IMAGE: &str = "bx-image.image.v1";
const METHODS: &str = r#"{"info":"imageInfo","resize":"imageResize","grayScale":"imageGrayScale","greyScale":"imageGrayScale","getPixels":"imageGetPixels"}"#;
/// Largest side accepted for blank images and SVG rendering.
const MAX_SIDE: u32 = 16384;
/// Size of both sides of an SVG that declares neither a viewBox nor a width and height.
const SVG_DEFAULT_SIZE: u32 = 512;
/// Largest pixel count accepted for any image (256 megapixels, 1 GiB of RGBA).
const MAX_PIXELS: u64 = 1 << 28;
/// Formats this backend reads, as reported by `getReadableImageFormats()`.
const READABLE_FORMATS: &[&str] = &["PNG", "JPEG", "JPG", "GIF", "WEBP", "SVG"];

/// An image held by the VM. Pixels are straight (not premultiplied) RGBA, row-major.
#[derive(Clone)]
struct Image {
    width: u32,
    height: u32,
    pixels: Vec<u8>,
    /// Set when the image was read from SVG, so a resize redraws it sharply.
    svg: Option<Arc<usvg::Tree>>,
    /// Grayscale is reapplied after an SVG redraw.
    gray: bool,
    source: Option<String>,
}
impl std::fmt::Debug for Image {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "Image({}x{}, svg={}, gray={})",
            self.width,
            self.height,
            self.svg.is_some(),
            self.gray
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Pure image operations (unit-tested without a host)
// ---------------------------------------------------------------------------------------------

fn check_size(width: u32, height: u32) -> Result<(), String> {
    if width == 0 || height == 0 || width > MAX_SIDE || height > MAX_SIDE {
        return Err(format!(
            "Image dimensions must be between 1 and {MAX_SIDE} pixels"
        ));
    }
    if width as u64 * height as u64 > MAX_PIXELS {
        return Err("Image is too large".into());
    }
    Ok(())
}

fn alloc_pixels(width: u32, height: u32, rgba: [u8; 4]) -> Result<Vec<u8>, String> {
    check_size(width, height)?;
    let len = width as usize * height as usize * 4;
    let mut pixels = Vec::new();
    pixels
        .try_reserve_exact(len)
        .map_err(|_| "Unable to allocate image pixels")?;
    for _ in 0..width as usize * height as usize {
        pixels.extend_from_slice(&rgba);
    }
    Ok(pixels)
}

/// Same integer weights as Java2D's RGB to `TYPE_BYTE_GRAY` conversion, so both backends agree.
fn luma(r: u8, g: u8, b: u8) -> u8 {
    ((77 * r as u32 + 150 * g as u32 + 29 * b as u32 + 128) >> 8) as u8
}

/// Like the JVM's `TYPE_BYTE_GRAY` result: composited onto black and fully opaque.
fn gray_in_place(pixels: &mut [u8]) {
    for px in pixels.chunks_exact_mut(4) {
        let a = px[3] as u32;
        let over_black = |c: u8| ((c as u32 * a + 127) / 255) as u8;
        let v = luma(over_black(px[0]), over_black(px[1]), over_black(px[2]));
        px.copy_from_slice(&[v, v, v, 255]);
    }
}

fn parse_color(color: &str) -> Result<[u8; 4], String> {
    let c = color.trim().to_ascii_lowercase();
    let named = match c.as_str() {
        "black" => Some([0, 0, 0]),
        "white" => Some([255, 255, 255]),
        "red" => Some([255, 0, 0]),
        "green" => Some([0, 255, 0]),
        "blue" => Some([0, 0, 255]),
        "gray" | "grey" => Some([128, 128, 128]),
        "yellow" => Some([255, 255, 0]),
        "cyan" => Some([0, 255, 255]),
        "magenta" => Some([255, 0, 255]),
        _ => None,
    };
    if let Some([r, g, b]) = named {
        return Ok([r, g, b, 255]);
    }
    let hex = c.strip_prefix('#').unwrap_or(&c);
    if hex.len() == 6 && hex.bytes().all(|b| b.is_ascii_hexdigit()) {
        let v = u32::from_str_radix(hex, 16).map_err(|e| e.to_string())?;
        return Ok([(v >> 16) as u8, (v >> 8) as u8, v as u8, 255]);
    }
    Err(format!(
        "Unsupported color '{color}': use a hex value (#RRGGBB) or black, white, red, green, blue, gray, yellow, cyan or magenta"
    ))
}

fn blank(width: u32, height: u32, image_type: &str, color: &str) -> Result<Image, String> {
    let rgba = parse_color(color)?;
    let image_type = image_type.to_ascii_lowercase();
    if !matches!(image_type.as_str(), "rgb" | "argb" | "grayscale") {
        return Err(format!(
            "Unsupported imageType '{image_type}': use rgb, argb or grayscale"
        ));
    }
    let mut pixels = alloc_pixels(width, height, rgba)?;
    let gray = image_type == "grayscale";
    if gray {
        gray_in_place(&mut pixels);
    }
    Ok(Image {
        width,
        height,
        pixels,
        svg: None,
        gray,
        source: None,
    })
}

fn looks_like_svg(path: &str, data: &[u8]) -> bool {
    let ext = Path::new(path)
        .extension()
        .and_then(|e| e.to_str())
        .map(|e| e.to_ascii_lowercase());
    if matches!(ext.as_deref(), Some("svg" | "svgz")) {
        return true;
    }
    if data.starts_with(&[0x1f, 0x8b]) {
        return false; // gzip without an .svgz name: leave to the raster decoders
    }
    let head = &data[..data.len().min(1024)];
    let text = String::from_utf8_lossy(head);
    let text = text.trim_start_matches('\u{feff}').trim_start();
    text.starts_with("<svg") || (text.starts_with('<') && text.contains("<svg"))
}

fn font_database() -> Arc<usvg::fontdb::Database> {
    static FONTS: OnceLock<Arc<usvg::fontdb::Database>> = OnceLock::new();
    FONTS
        .get_or_init(|| {
            let mut db = usvg::fontdb::Database::new();
            db.load_system_fonts();
            Arc::new(db)
        })
        .clone()
}

/// Size an SVG renders at when no size is asked for, the same rule as the JVM backend: its own size
/// when the root declares a viewBox or an absolute width and height, otherwise 512 x 512.
fn svg_intrinsic_size(data: &[u8], tree: &usvg::Tree) -> (u32, u32) {
    let declared = std::str::from_utf8(data)
        .ok()
        .and_then(|text| usvg::roxmltree::Document::parse(text.trim_start_matches('\u{feff}')).ok())
        .map(|doc| {
            let root = doc.root_element();
            let absolute = |name| {
                root.attribute(name)
                    .is_some_and(|v: &str| !v.is_empty() && !v.contains('%'))
            };
            root.attribute("viewBox").is_some() || (absolute("width") && absolute("height"))
        })
        // Compressed or unusual documents: trust the parsed size
        .unwrap_or(true);
    if !declared {
        return (SVG_DEFAULT_SIZE, SVG_DEFAULT_SIZE);
    }
    let size = tree.size();
    (
        (size.width().ceil() as u32).max(1),
        (size.height().ceil() as u32).max(1),
    )
}

/// Draws the SVG at `width` x `height`. `fit` keeps its aspect ratio, centred in the box;
/// otherwise it is stretched to fill it.
fn render_svg(tree: &usvg::Tree, width: u32, height: u32, fit: bool) -> Result<Vec<u8>, String> {
    check_size(width, height)?;
    let mut pixmap =
        tiny_skia::Pixmap::new(width, height).ok_or("Unable to allocate image pixels")?;
    let size = tree.size();
    let (sx, sy) = (width as f32 / size.width(), height as f32 / size.height());
    let transform = if fit {
        let scale = sx.min(sy);
        tiny_skia::Transform::from_scale(scale, scale).post_translate(
            (width as f32 - size.width() * scale) / 2.0,
            (height as f32 - size.height() * scale) / 2.0,
        )
    } else {
        tiny_skia::Transform::from_scale(sx, sy)
    };
    resvg::render(tree, transform, &mut pixmap.as_mut());
    let mut pixels = Vec::new();
    pixels
        .try_reserve_exact(width as usize * height as usize * 4)
        .map_err(|_| "Unable to allocate image pixels")?;
    for px in pixmap.pixels() {
        let c = px.demultiply();
        pixels.extend_from_slice(&[c.red(), c.green(), c.blue(), c.alpha()]);
    }
    Ok(pixels)
}

fn decode(path: &str, data: &[u8]) -> Result<Image, String> {
    if looks_like_svg(path, data) {
        let options = usvg::Options {
            fontdb: font_database(),
            ..usvg::Options::default()
        };
        let tree =
            usvg::Tree::from_data(data, &options).map_err(|e| format!("invalid SVG: {e}"))?;
        let (width, height) = svg_intrinsic_size(data, &tree);
        let pixels = render_svg(&tree, width, height, false)?;
        return Ok(Image {
            width,
            height,
            pixels,
            svg: Some(Arc::new(tree)),
            gray: false,
            source: Some(path.into()),
        });
    }
    let format = image::guess_format(data)
        .map_err(|_| "unrecognized or unsupported image format".to_string())?;
    let mut reader = image::ImageReader::with_format(std::io::Cursor::new(data), format);
    let mut limits = image::Limits::default();
    limits.max_image_width = Some(MAX_SIDE);
    limits.max_image_height = Some(MAX_SIDE);
    reader.limits(limits);
    let rgba = reader.decode().map_err(|e| e.to_string())?.to_rgba8();
    let (width, height) = rgba.dimensions();
    check_size(width, height)?;
    Ok(Image {
        width,
        height,
        pixels: rgba.into_raw(),
        svg: None,
        gray: false,
        source: Some(path.into()),
    })
}

fn read(path: &str) -> Result<Image, String> {
    let fail = |reason: String| format!("Unable to load image: {path}: {reason}");
    if path.trim().is_empty() {
        return Err("Unable to load image: the path is empty".into());
    }
    let lower = path.to_ascii_lowercase();
    if lower.starts_with("http://") || lower.starts_with("https://") {
        return Err(fail(
            "URLs are not supported by the MatchBox backend; read a local file".into(),
        ));
    }
    let file = path.strip_prefix("file://").unwrap_or(path);
    let data = std::fs::read(file).map_err(|e| fail(e.to_string()))?;
    decode(path, &data).map_err(fail)
}

/// `imageRead( path, [width], [height] )`: an SVG is drawn straight at the asked size (fitted and
/// centred when both are given), other formats are decoded and then resized.
fn read_sized(path: &str, width: Option<u32>, height: Option<u32>) -> Result<Image, String> {
    let mut image = read(path)?;
    if width.is_none() && height.is_none() {
        return Ok(image);
    }
    let fail = |reason: String| format!("Unable to load image: {path}: {reason}");
    match (&image.svg, width, height) {
        (Some(tree), Some(w), Some(h)) => {
            let pixels = render_svg(tree, w, h, true).map_err(fail)?;
            image.width = w;
            image.height = h;
            image.pixels = pixels;
        }
        _ => resize(&mut image, width, height, "bilinear").map_err(fail)?,
    }
    Ok(image)
}

/// Resolves the target size; `None` keeps the aspect ratio from the other side.
fn target_size(
    image: &Image,
    width: Option<u32>,
    height: Option<u32>,
) -> Result<(u32, u32), String> {
    let scale = |known: u32, from: u32, to: u32| {
        ((known as f64 * to as f64 / from as f64).round() as u32).max(1)
    };
    match (width, height) {
        (Some(w), Some(h)) => Ok((w, h)),
        (Some(w), None) => Ok((w, scale(w, image.width, image.height))),
        (None, Some(h)) => Ok((scale(h, image.height, image.width), h)),
        (None, None) => Err("imageResize needs a width or a height".into()),
    }
}

fn resize(
    image: &mut Image,
    width: Option<u32>,
    height: Option<u32>,
    interpolation: &str,
) -> Result<(), String> {
    let (width, height) = target_size(image, width, height)?;
    check_size(width, height)?;
    let pixels = if let Some(tree) = &image.svg {
        let mut pixels = render_svg(tree, width, height, false)?;
        if image.gray {
            gray_in_place(&mut pixels);
        }
        pixels
    } else {
        use image::imageops::FilterType;
        let filter = match interpolation.to_ascii_lowercase().as_str() {
            "nearest" | "nearestneighbor" => FilterType::Nearest,
            "bicubic" => FilterType::CatmullRom,
            "bilinear" | "" => FilterType::Triangle,
            "lanczos" | "highestquality" | "highquality" => FilterType::Lanczos3,
            other => {
                return Err(format!(
                    "Unsupported interpolation '{other}': use bilinear, bicubic, nearest or lanczos"
                ));
            }
        };
        let source = image::RgbaImage::from_raw(
            image.width,
            image.height,
            std::mem::take(&mut image.pixels),
        )
        .ok_or("Corrupt image pixels")?;
        image::imageops::resize(&source, width, height, filter).into_raw()
    };
    image.width = width;
    image.height = height;
    image.pixels = pixels;
    Ok(())
}

fn grayscale(image: &mut Image) {
    gray_in_place(&mut image.pixels);
    image.gray = true;
}

#[derive(Clone, Copy, PartialEq, Debug)]
enum PixelMode {
    Luma,
    Rgba,
}
fn pixel_mode(mode: &str) -> Result<PixelMode, String> {
    match mode.to_ascii_lowercase().as_str() {
        "" | "luma" => Ok(PixelMode::Luma),
        "rgba" => Ok(PixelMode::Rgba),
        other => Err(format!(
            "Unsupported pixel mode '{other}': use luma or rgba"
        )),
    }
}

// ---------------------------------------------------------------------------------------------
// Host bindings
// ---------------------------------------------------------------------------------------------

static TYPES: OnceLock<&'static TypesV1> = OnceLock::new();
static COLLECTIONS: OnceLock<&'static CollectionsV1> = OnceLock::new();
fn types() -> &'static TypesV1 {
    TYPES.get().expect("types capability initialised")
}
fn collections() -> &'static CollectionsV1 {
    COLLECTIONS
        .get()
        .expect("collections capability initialised")
}

/// The image passed as `name`. The JVM also accepts a variable name; MatchBox needs the image.
unsafe fn image_arg<'a>(call: &Call<'_>, index: usize) -> Result<&'a mut Image, String> {
    let value = call.argument(index)?;
    // Check the kind first: object_data on a non-object raises a host error.
    match call.kind(types(), value)? {
        Kind::Other => {
            let data = unsafe { call.object_data(value, IMAGE) }?;
            Ok(unsafe { data.cast::<Image>().as_mut() })
        }
        Kind::String => Err(
            "Pass the image itself: the MatchBox backend does not look images up by variable name"
                .into(),
        ),
        _ => Err("Expected an image".into()),
    }
}

/// A dimension that may be blank or 0, meaning "keep the aspect ratio".
fn optional_dimension(call: &Call<'_>, value: Value) -> Result<Option<u32>, String> {
    let number = match call.kind(types(), value)? {
        Kind::Null => return Ok(None),
        Kind::Number => call.number(value)?,
        Kind::String => {
            let text = call.string(value)?;
            let text = text.trim();
            if text.is_empty() {
                return Ok(None);
            }
            text.parse::<f64>()
                .map_err(|_| format!("Invalid dimension '{text}'"))?
        }
        _ => return Err("Image dimensions must be numbers".into()),
    };
    if !number.is_finite() || number < 0.0 || number > MAX_SIDE as f64 {
        return Err(format!("Image dimensions must be between 0 and {MAX_SIDE}"));
    }
    let rounded = number.round() as u32;
    Ok((rounded > 0).then_some(rounded))
}

fn string_or_default(call: &Call<'_>, value: Value, default: &str) -> Result<String, String> {
    match call.kind(types(), value)? {
        Kind::Null => Ok(default.into()),
        _ => call.string(value),
    }
}

unsafe fn new_object(call: &Call<'_>, image: Image) -> Result<Value, String> {
    let data = Box::into_raw(Box::new(image)).cast();
    match unsafe { call.object_new(IMAGE, data, drop_image, METHODS) } {
        Ok(value) => Ok(value),
        Err(error) => {
            unsafe { drop_image(data) };
            Err(error)
        }
    }
}

unsafe extern "C" fn drop_image(data: *mut c_void) {
    drop(unsafe { Box::from_raw(data.cast::<Image>()) });
}

/// Declares a host callback whose body returns `Result<Value, String>`.
macro_rules! bif {
    ($name:ident, |$call:ident| $body:block) => {
        unsafe extern "C" fn $name(
            ctx: Context,
            api: *const ValuesV1,
            _data: *mut c_void,
            args: *const Value,
            len: usize,
            out: *mut Value,
        ) -> i32 {
            unsafe {
                guarded(ctx, api, out, || {
                    let $call = Call::new(ctx, api, args, len);
                    $body
                })
            }
        }
    };
}

bif!(image_read, |call| {
    let path = call.string(call.argument(0)?)?;
    let width = optional_dimension(&call, call.argument(1)?)?;
    let height = optional_dimension(&call, call.argument(2)?)?;
    new_object(&call, read_sized(&path, width, height)?)
});

bif!(image_new, |call| {
    let source = call.argument(0)?;
    if call.kind(types(), source)? == Kind::Other {
        let copy = image_arg(&call, 0)?.clone();
        return new_object(&call, copy);
    }
    let path = string_or_default(&call, source, "")?;
    if !path.is_empty() {
        return new_object(&call, read(&path)?);
    }
    let width = optional_dimension(&call, call.argument(1)?)?;
    let height = optional_dimension(&call, call.argument(2)?)?;
    let (Some(width), Some(height)) = (width, height) else {
        return Err("A blank image needs a width and a height of at least 1".into());
    };
    let image_type = string_or_default(&call, call.argument(3)?, "rgb")?;
    let color = string_or_default(&call, call.argument(4)?, "black")?;
    new_object(&call, blank(width, height, &image_type, &color)?)
});

bif!(image_resize, |call| {
    let image = image_arg(&call, 0)?;
    let width = optional_dimension(&call, call.argument(1)?)?;
    let height = optional_dimension(&call, call.argument(2)?)?;
    let interpolation = string_or_default(&call, call.argument(3)?, "bilinear")?;
    resize(image, width, height, &interpolation)?;
    call.argument(0)
});

bif!(image_gray_scale, |call| {
    grayscale(image_arg(&call, 0)?);
    call.argument(0)
});

bif!(image_info, |call| {
    let image = image_arg(&call, 0)?;
    let info = call.struct_new()?;
    call.struct_set(info, "width", call.number_new(image.width as f64)?)?;
    call.struct_set(info, "height", call.number_new(image.height as f64)?)?;
    let source = match &image.source {
        Some(source) => call.string_new(source)?,
        None => call.null_new()?,
    };
    call.struct_set(info, "source", source)?;
    Ok(info)
});

bif!(image_get_pixels, |call| {
    let image = image_arg(&call, 0)?;
    let mode = pixel_mode(&string_or_default(&call, call.argument(1)?, "luma")?)?;
    let api = collections();
    let rows = call.array_new(api)?;
    for row in image.pixels.chunks_exact(image.width as usize * 4) {
        let values = call.array_new(api)?;
        for px in row.chunks_exact(4) {
            let value = match mode {
                PixelMode::Luma => call.number_new(luma(px[0], px[1], px[2]) as f64)?,
                PixelMode::Rgba => {
                    let channels = call.array_new(api)?;
                    for &channel in px {
                        call.array_push(api, channels, call.number_new(channel as f64)?)?;
                    }
                    channels
                }
            };
            call.array_push(api, values, value)?;
        }
        call.array_push(api, rows, values)?;
    }
    Ok(rows)
});

bif!(get_readable_image_formats, |call| {
    let api = collections();
    let formats = call.array_new(api)?;
    for format in READABLE_FORMATS {
        call.array_push(api, formats, call.string_new(format)?)?;
    }
    Ok(formats)
});

/// Stable, versioned entry point; artifacts require no MatchBox VM crate or Rust ABI.
/// # Safety
/// Called only by a compatible host with valid registration pointers.
#[unsafe(no_mangle)]
pub unsafe extern "C" fn matchbox_plugin_init_v1(host: *const HostV1, ctx: Context) -> i32 {
    let capabilities = unsafe {
        require_values(host, ctx)
            .and_then(|_| require_types(host, ctx))
            .and_then(|types| Ok((types, require_collections(host, ctx)?)))
    };
    let Ok((types, collections)) = capabilities else {
        return 1;
    };
    let _ = TYPES.set(types);
    let _ = COLLECTIONS.set(collections);
    let host = unsafe { &*host };
    let functions = [
        (
            r#"{"name":"imageRead","arguments":[{"name":"path","required":true},{"name":"width","default":null},{"name":"height","default":null}]}"#,
            image_read as Invoke,
        ),
        (
            r#"{"name":"imageNew","arguments":[{"name":"source","default":""},{"name":"width","default":null},{"name":"height","default":null},{"name":"imageType","default":"rgb"},{"name":"color","default":"black"}]}"#,
            image_new as Invoke,
        ),
        (
            r#"{"name":"imageResize","arguments":[{"name":"name","required":true},{"name":"width","default":null},{"name":"height","default":null},{"name":"interpolation","default":"bilinear"},{"name":"blurFactor","default":1}]}"#,
            image_resize as Invoke,
        ),
        (
            r#"{"name":"imageGrayScale","arguments":[{"name":"name","required":true}]}"#,
            image_gray_scale as Invoke,
        ),
        (
            r#"{"name":"imageGreyScale","arguments":[{"name":"name","required":true}]}"#,
            image_gray_scale as Invoke,
        ),
        (
            r#"{"name":"imageInfo","arguments":[{"name":"name","required":true}]}"#,
            image_info as Invoke,
        ),
        (
            r#"{"name":"imageGetPixels","arguments":[{"name":"name","required":true},{"name":"mode","default":"luma"}]}"#,
            image_get_pixels as Invoke,
        ),
        (
            r#"{"name":"getReadableImageFormats","arguments":[]}"#,
            get_readable_image_formats as Invoke,
        ),
    ];
    for (descriptor, function) in functions {
        if unsafe { (host.register)(ctx, Text::new(descriptor), function) } != 0 {
            return 1;
        }
    }
    0
}

#[cfg(test)]
mod tests {
    use super::*;

    const SVG: &[u8] = br##"<svg xmlns="http://www.w3.org/2000/svg" width="4" height="2" viewBox="0 0 4 2"><rect width="2" height="2" fill="#ff0000"/><rect x="2" width="2" height="2" fill="#0000ff"/></svg>"##;

    fn png(width: u32, height: u32, f: impl Fn(u32, u32) -> [u8; 4]) -> Vec<u8> {
        let img = image::RgbaImage::from_fn(width, height, |x, y| image::Rgba(f(x, y)));
        let mut out = std::io::Cursor::new(Vec::new());
        img.write_to(&mut out, image::ImageFormat::Png).unwrap();
        out.into_inner()
    }
    fn px(image: &Image, x: u32, y: u32) -> [u8; 4] {
        let i = ((y * image.width + x) * 4) as usize;
        image.pixels[i..i + 4].try_into().unwrap()
    }

    #[test]
    fn validates_sizes() {
        assert!(check_size(32, 16).is_ok());
        assert!(check_size(MAX_SIDE, MAX_SIDE).is_ok());
        for (w, h) in [(0, 1), (1, 0), (MAX_SIDE + 1, 1), (1, MAX_SIDE + 1)] {
            assert!(check_size(w, h).is_err(), "{w}x{h}");
        }
    }

    #[test]
    fn luma_matches_java2d_weights() {
        assert_eq!(luma(0, 0, 0), 0);
        assert_eq!(luma(255, 255, 255), 255);
        assert_eq!(luma(255, 0, 0), 77);
        assert_eq!(luma(0, 255, 0), 149);
        assert_eq!(luma(0, 0, 255), 29);
    }

    #[test]
    fn blank_images_fill_and_validate() {
        let img = blank(3, 2, "rgb", "#102030").unwrap();
        assert_eq!(px(&img, 2, 1), [0x10, 0x20, 0x30, 255]);
        assert!(blank(3, 2, "cmyk", "black").is_err());
        assert!(blank(3, 2, "rgb", "chartreuse-ish").is_err());
        assert_eq!(
            px(&blank(1, 1, "grayscale", "white").unwrap(), 0, 0),
            [255; 4]
        );
    }

    #[test]
    fn decodes_png_and_detects_svg_by_content() {
        let img = decode(
            "a.png",
            &png(2, 1, |x, _| {
                if x == 0 {
                    [255, 0, 0, 255]
                } else {
                    [0, 0, 255, 128]
                }
            }),
        )
        .unwrap();
        assert_eq!((img.width, img.height), (2, 1));
        assert_eq!(px(&img, 1, 0), [0, 0, 255, 128]);
        let svg = decode("no-extension", SVG).unwrap();
        assert!(svg.svg.is_some());
        assert_eq!((svg.width, svg.height), (4, 2));
        assert_eq!(px(&svg, 0, 0), [255, 0, 0, 255]);
        assert_eq!(px(&svg, 3, 1), [0, 0, 255, 255]);
    }

    #[test]
    fn errors_are_clear() {
        let missing = read("/definitely/missing.png").unwrap_err();
        assert!(
            missing.starts_with("Unable to load image: /definitely/missing.png"),
            "{missing}"
        );
        let bad_svg = decode("bad.svg", b"<svg><rect").unwrap_err();
        assert!(bad_svg.contains("invalid SVG"), "{bad_svg}");
        assert!(decode("x.bin", b"not an image").is_err());
        assert!(
            read("https://example.com/x.png")
                .unwrap_err()
                .contains("URLs")
        );
    }

    #[test]
    fn resize_keeps_aspect_ratio_and_redraws_svg() {
        let mut img = decode(
            "a.png",
            &png(8, 4, |x, _| {
                if x < 4 {
                    [255, 0, 0, 255]
                } else {
                    [0, 255, 0, 255]
                }
            }),
        )
        .unwrap();
        resize(&mut img, Some(4), None, "bilinear").unwrap();
        assert_eq!((img.width, img.height), (4, 2));
        assert_eq!(px(&img, 0, 0), [255, 0, 0, 255]);
        assert_eq!(px(&img, 3, 1), [0, 255, 0, 255]);
        resize(&mut img, None, Some(8), "nearest").unwrap();
        assert_eq!((img.width, img.height), (16, 8));
        assert!(resize(&mut img, None, None, "bilinear").is_err());
        assert!(resize(&mut img, Some(2), Some(2), "sideways").is_err());

        let mut svg = decode("s.svg", SVG).unwrap();
        grayscale(&mut svg);
        resize(&mut svg, Some(400), None, "bilinear").unwrap();
        assert_eq!((svg.width, svg.height), (400, 200));
        // Redrawn, not upscaled: the edge between the halves stays sharp.
        assert_eq!(px(&svg, 199, 100), [77, 77, 77, 255]);
        assert_eq!(px(&svg, 200, 100), [29, 29, 29, 255]);
    }

    #[test]
    fn svg_sizes_follow_the_jvm_rules() {
        let no_size = decode(
            "n.svg",
            br#"<svg xmlns="http://www.w3.org/2000/svg"><rect width="5" height="5"/></svg>"#,
        )
        .unwrap();
        assert_eq!((no_size.width, no_size.height), (512, 512));
        let percent = decode(
            "p.svg",
            br#"<svg xmlns="http://www.w3.org/2000/svg" width="100%" height="50"/>"#,
        )
        .unwrap();
        assert_eq!((percent.width, percent.height), (512, 512));
        let view_box = decode(
            "v.svg",
            br#"<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 30 10"/>"#,
        )
        .unwrap();
        assert_eq!((view_box.width, view_box.height), (30, 10));
        let fractional = decode(
            "f.svg",
            br#"<svg xmlns="http://www.w3.org/2000/svg" width="10.2" height="4.5"/>"#,
        )
        .unwrap();
        assert_eq!((fractional.width, fractional.height), (11, 5));
    }

    #[test]
    fn read_sized_draws_svg_at_the_asked_size() {
        let dir = std::env::temp_dir().join(format!("bx-image-native-{}", std::process::id()));
        std::fs::create_dir_all(&dir).unwrap();
        let path = dir.join("halves.svg");
        std::fs::write(&path, SVG).unwrap();
        let path = path.to_str().unwrap();
        let wide = read_sized(path, Some(400), None).unwrap();
        assert_eq!((wide.width, wide.height), (400, 200));
        assert_eq!(px(&wide, 199, 100), [255, 0, 0, 255]);
        assert_eq!(px(&wide, 200, 100), [0, 0, 255, 255]);
        // Both sides: fitted and centred, transparent around it
        let boxed = read_sized(path, Some(40), Some(40)).unwrap();
        assert_eq!((boxed.width, boxed.height), (40, 40));
        assert_eq!(px(&boxed, 0, 0)[3], 0);
        assert_eq!(px(&boxed, 0, 20), [255, 0, 0, 255]);
        assert_eq!(px(&boxed, 39, 20), [0, 0, 255, 255]);
        std::fs::remove_dir_all(dir).unwrap();
    }

    #[test]
    fn grayscale_composites_on_black() {
        let mut img = decode(
            "a.png",
            &png(2, 1, |x, _| {
                if x == 0 {
                    [255, 255, 255, 255]
                } else {
                    [255, 255, 255, 0]
                }
            }),
        )
        .unwrap();
        grayscale(&mut img);
        assert_eq!(px(&img, 0, 0), [255, 255, 255, 255]);
        assert_eq!(px(&img, 1, 0), [0, 0, 0, 255]);
    }

    #[test]
    fn pixel_modes() {
        assert_eq!(pixel_mode("LUMA").unwrap(), PixelMode::Luma);
        assert_eq!(pixel_mode("rgba").unwrap(), PixelMode::Rgba);
        assert!(pixel_mode("cmyk").is_err());
    }
}
