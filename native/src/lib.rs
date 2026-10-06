//! Initial MatchBox backend: blank black RGB images and dimension metadata.
//! The JVM implementation remains unchanged in the same bx-image package.
use matchbox_plugin_sdk::*;
use std::ffi::c_void;

const IMAGE: &str = "bx-image.image.v1";
struct Image {
    width: u32,
    height: u32,
    _pixels: Vec<u8>,
}
fn dimension(value: f64) -> Result<u32, String> {
    if !value.is_finite() || value < 1.0 || value.fract() != 0.0 || value > 4096.0 {
        return Err("Blank image dimensions must be integers between 1 and 4096".into());
    }
    Ok(value as u32)
}
unsafe extern "C" fn drop_image(data: *mut c_void) {
    drop(unsafe { Box::from_raw(data.cast::<Image>()) });
}
unsafe extern "C" fn image_new(
    ctx: Context,
    api: *const ValuesV1,
    _data: *mut c_void,
    args: *const Value,
    len: usize,
    out: *mut Value,
) -> i32 {
    unsafe {
        guarded(ctx, api, out, || {
            let call = Call::new(ctx, api, args, len);
            if !call.string(call.argument(0)?)?.is_empty() {
                return Err("This native image pilot only supports blank images".into());
            }
            let width = dimension(call.number(call.argument(1)?)?)?;
            let height = dimension(call.number(call.argument(2)?)?)?;
            if !call.string(call.argument(3)?)?.eq_ignore_ascii_case("rgb")
                || !call
                    .string(call.argument(4)?)?
                    .eq_ignore_ascii_case("black")
            {
                return Err("This native image pilot supports only black RGB images".into());
            }
            let size = (width as usize)
                .checked_mul(height as usize)
                .and_then(|n| n.checked_mul(3))
                .ok_or("Image size overflow")?;
            let mut pixels = Vec::new();
            pixels
                .try_reserve_exact(size)
                .map_err(|_| "Unable to allocate image pixels")?;
            pixels.resize(size, 0);
            let data = Box::into_raw(Box::new(Image {
                width,
                height,
                _pixels: pixels,
            }))
            .cast();
            match call.object_new(IMAGE, data, drop_image, r#"{"info":"imageInfo"}"#) {
                Ok(value) => Ok(value),
                Err(error) => {
                    drop_image(data);
                    Err(error)
                }
            }
        })
    }
}
unsafe extern "C" fn image_info(
    ctx: Context,
    api: *const ValuesV1,
    _data: *mut c_void,
    args: *const Value,
    len: usize,
    out: *mut Value,
) -> i32 {
    unsafe {
        guarded(ctx, api, out, || {
            let call = Call::new(ctx, api, args, len);
            let image = &*call.object_data(call.argument(0)?, IMAGE)?.cast::<Image>();
            let info = call.struct_new()?;
            call.struct_set(info, "width", call.number_new(image.width as f64)?)?;
            call.struct_set(info, "height", call.number_new(image.height as f64)?)?;
            Ok(info)
        })
    }
}

/// Stable, versioned entry point; artifacts require no MatchBox VM crate or Rust ABI.
/// # Safety
/// Called only by a compatible host with valid registration pointers.
#[unsafe(no_mangle)]
pub unsafe extern "C" fn matchbox_plugin_init_v1(host: *const HostV1, ctx: Context) -> i32 {
    if unsafe { require_values(host, ctx) }.is_err() {
        return 1;
    }
    let host = unsafe { &*host };
    let functions = [
        (
            r#"{"name":"imageNew","arguments":[{"name":"source","default":""},{"name":"width","required":true},{"name":"height","required":true},{"name":"imageType","default":"rgb"},{"name":"color","default":"black"}]}"#,
            image_new as Invoke,
        ),
        (
            r#"{"name":"imageInfo","arguments":[{"name":"name","required":true}]}"#,
            image_info as Invoke,
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
    #[test]
    fn validates_dimensions() {
        assert_eq!(dimension(32.0).unwrap(), 32);
        for value in [0.0, -1.0, 1.5, f64::NAN, f64::INFINITY, 4097.0] {
            assert!(dimension(value).is_err());
        }
    }
}
