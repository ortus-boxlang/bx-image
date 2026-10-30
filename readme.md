# ⚡︎ BoxLang Module: BoxLang Image Library

```
|:------------------------------------------------------:|
| ⚡︎ B o x L a n g ⚡︎
| Dynamic : Modular : Productive
|:------------------------------------------------------:|
```

<blockquote>
	Copyright Since 2023 by Ortus Solutions, Corp
	<br>
	<a href="https://www.boxlang.io">www.boxlang.io</a> |
	<a href="https://www.ortussolutions.com">www.ortussolutions.com</a>
</blockquote>

<p>&nbsp;</p>

## Introduction

The **BoxLang** Image module provides comprehensive image manipulation functionality with a fluent, chainable API. Read, create, transform, filter, and save images with ease. This module brings CFML-compatible image functions to BoxLang while adding modern enhancements and conveniences.

**Key Features:**

- 📸 Read/write images from files, URLs, or Base64
- 🖼️ Broad format support: PNG, JPEG, WebP, GIF, BMP, TIFF
- ✂️ Crop, resize, rotate, flip, scale, split, and shear images
- 🎨 Draw shapes, text, lines, bezier curves, and points
- 🎭 Apply filters: blur, sharpen, grayscale, negative
- 🤖 Generate CAPTCHA images with configurable difficulty
- 🔳 Generate and read QR codes, with custom colors and an optional logo
- 📊 Extract EXIF and IPTC metadata
- ⚡ Fluent API with method chaining
- 🔧 50+ built-in functions and member methods

📖 **[Full Documentation](https://boxlang.ortusbooks.com/boxlang-framework/modularity/image-manipulation)**

## Why BoxLang Image?

BoxLang Image brings a **fluent, modern API** to image manipulation. Unlike traditional tag-based or function-based approaches, BoxLang Image emphasizes **method chaining** for readable, maintainable code:

```javascript
// ✨ The Fluent Way (Recommended)
imageRead("photo.jpg")
    .scaleToFit(800, 600)
    .blur(2)
    .sharpen(1)
    .grayScale()
    .write();  // Saves back to original file

// 📦 Traditional BIF Approach
img = imageRead("photo.jpg");
imageResize(img, 800, 600);
imageBlur(img, 2);
imageSharpen(img, 1);
imageGrayScale(img);
imageWrite(img, "photo.jpg");

// 🏷️ Component Approach
<bx:image action="read" source="photo.jpg" name="img" />
<bx:image action="resize" source="#img#" width="800" height="600" />
<bx:image action="write" source="#img#" destination="photo.jpg" />
```

**The fluent API provides:**

- 🔗 **Method chaining** - Write transformations in a natural, sequential flow
- 🎯 **Less boilerplate** - No need to repeat variable names
- 📖 **Better readability** - Code reads like a pipeline of transformations
- ⚡ **Immediate feedback** - Each method returns the image for further manipulation

## Quick Start

### Installation

Install via CommandBox:

```bash
# Install the module
install bx-image

# Or add to box.json
box install bx-image --save
```

### Basic Usage

```javascript
// Read and manipulate an image
img = imageRead( "photo.jpg" )
	.scaleToFit(800, 600)
	.blur(2)
	.sharpen(1)
	.write("photo-optimized.jpg")

// Create a new image with drawing
canvas = imageNew("", 400, 300, "rgb", "white")
canvas.setDrawingColor("blue")
      .drawRect(50, 50, 300, 200, true)
      .setDrawingColor("red")
      .drawText("Hello BoxLang!", 150, 150)
	  .write("greeting.png")

// Chain operations
imageRead("logo.png")
    .crop(10, 10, 200, 200)
    .grayScale()
    .rotate(45)
    .write("logo-transformed.png")
```

## Quick Reference

### Fluent BoxImage API

The `BoxImage` class provides a fluent interface where most methods return `this` for chaining:

#### Image Creation & I/O

```javascript
img = imageRead("path/to/file.jpg")          // Load from file
img = imageRead("https://example.com/img")   // Load from URL
img = imageReadBase64(base64String)          // Load from Base64
img = imageNew("", 800, 600, "rgb", "white") // Create blank canvas

img.write("output.png")                      // Write to specified file (format from extension)
img.write()                                  // Write back to original source file
base64 = imageWriteBase64(img)               // Export as Base64
base64 = imageWriteBase64(img, "jpg")        // Export as Base64 with format
blob = imageGetBlob(img)                     // Export as binary
imageWriteToBrowser(img)                     // Stream directly to HTTP response
formats = GetReadableImageFormats()          // List readable formats
formats = GetWriteableImageFormats()         // List writable formats
```

**Note:** The `write()` and `ImageWrite()` methods auto-detect the output format from the file extension (e.g., `.png`, `.jpg`, `.webp`, `.gif`, `.bmp`, `.tiff`). For formats that don't support transparency (JPEG, BMP), images with alpha channels are automatically composited onto a white background.

#### Transformations

```javascript
img.resize(width, height)                    // Exact dimensions
img.resize(width, height, interpolation)     // With interpolation method
img.scaleToFit(size)                         // Fit to width, maintain aspect ratio
img.scaleToFit(width, height)                // Fit within rectangular bounds
img.scaleToFit(width, height, interpolation) // With custom interpolation
img.crop(x, y, width, height)                // Extract region
img.rotate(angle)                            // Rotate degrees
img.flip("horizontal")                       // Flip horizontal
img.flip("vertical")                         // Flip vertical
img.flip("diagonal")                         // Flip along main diagonal (transpose)
img.flip("antidiagonal")                     // Flip along anti-diagonal
img.flip("90")                               // Rotate 90° clockwise
img.flip("180")                              // Rotate 180°
img.flip("270")                              // Rotate 270° clockwise (90° CCW)
img.shear(shearX, shearY)                    // Shear transform
img.rotateDrawingAxis(angle)                 // Rotate drawing axis
img.translateDrawingAxis(x, y)               // Translate drawing axis
img.shearDrawingAxis(shearX, shearY)         // Shear drawing axis
```

#### Filters & Effects

```javascript
img.blur(radius)                             // Apply Gaussian blur
img.sharpen(gain)                            // Sharpen image
img.grayScale()                              // Convert to grayscale
img.negative()                               // Invert colors
img.addBorder(thickness, color)              // Add colored border
```

#### Drawing Setup

```javascript
img.setDrawingColor("red")                   // Set foreground color
img.setDrawingColor("#FF0000")               // Use hex colors
img.setBackgroundColor("white")              // Set background
img.setAntiAliasing(true)                    // Enable anti-aliasing
img.setDrawingTransparency(50)               // Set transparency (0-100)
img.setDrawingStroke({                       // Set stroke properties
    width: 2.0,                              // Stroke width in pixels
    endCaps: "round",                        // "butt", "round", or "square"
    lineJoins: "miter",                      // "miter", "round", or "bevel"
    miterLimit: 10.0,                        // Miter limit for mitered joins
    dashArray: [10, 5],                      // Dash pattern (on, off, on, off...)
    dashPhase: 0                             // Offset to start dash pattern
})
```

#### Drawing Shapes

```javascript
img.drawRect(x, y, width, height, filled)    // Rectangle
img.drawRoundRect(x, y, w, h, aw, ah, fill)  // Rounded rectangle
img.drawBeveledRect(x, y, w, h, raised, fill)// Beveled rectangle
img.drawOval(x, y, width, height, filled)    // Oval/circle
img.drawArc(x, y, w, h, start, arc, filled)  // Arc segment
img.drawLine(x1, y1, x2, y2)                 // Straight line
img.drawLines(pointArray, isPolygon, filled) // Multiple lines/polygon
img.drawPoint(x, y)                          // Single pixel
img.drawCubicCurve(x1, y1, cx1, cy1, ...)    // Bezier curve
img.drawQuadraticCurve(x1, y1, cx, cy, ...)  // Quadratic curve
img.clearRect(x, y, width, height)           // Clear region
```

#### Drawing Text

```javascript
img.drawText(text, x, y)                     // Draw text at position
img.drawText(text, x, y, attributes)         // With font attributes
// attributes: { font, size, style, alpha, underline, strikethrough }
```

#### Image Composition

```javascript
img.overlay(topImage)                        // Overlay another image
img.paste(source, x, y)                      // Paste at position
img.copy(x, y, width, height)                // Copy region to new image
```

#### Image Splitting

```javascript
// Split into a grid of tiles (returns array of arrays)
tiles = img.splitGrid(columns, rows)          // 2D array: tiles[row][col]
tiles = ImageSplitGrid(img, columns, rows)    // BIF equivalent

// Example: split a 600×400 image into 3×2 grid (6 tiles, each 200×200)
grid = ImageRead("panorama.jpg").splitGrid(3, 2);
// grid[1][1] = top-left, grid[1][2] = top-center, etc.
eachTile = grid[2][1];  // second row, first column
```

#### Image Information

```javascript
width = img.getWidth()                       // Get width in pixels
height = img.getHeight()                     // Get height in pixels
info = imageInfo(img)                        // Full image info struct
exif = imageGetExifMetadata(img)             // EXIF metadata
tag = imageGetExifTag(img, tagName)          // Specific EXIF tag
iptc = imageGetIPTCMetadata(img)             // IPTC metadata
tag = imageGetIPTCTag(img, tagName)          // Specific IPTC tag
buffered = imageGetBufferedImage(img)        // Java BufferedImage
```

### Creating Images

```javascript
// From file
img = imageRead("path/to/image.jpg");

// From URL
img = imageRead("https://example.com/image.png");

// From Base64
img = imageReadBase64(base64String);

// New blank canvas
img = imageNew("", 800, 600, "rgb", "white");
```

### CAPTCHA Generation

```javascript
// Basic CAPTCHA (args: height, width, text)
captcha = ImageGenerateCaptcha( 75, 200, "A3X9K2" );
ImageWrite( captcha, "/path/to/captcha.png" );

// With difficulty and fonts (ColdFusion-compatible arg order)
captcha = ImageGenerateCaptcha( 35, 400, "loner" );
captcha = ImageGenerateCaptcha( 35, 400, "loner", "high" );
captcha = ImageGenerateCaptcha( 35, 400, "loner", "high", "serif,sansserif", 24 );

// Via component — auto-streams to browser when no name/destination is given
<bx:image action="captcha" text="A3X9K2" width="200" height="75" difficulty="medium" />

// Store in variable for further processing
<bx:image action="captcha" text="A3X9K2" width="200" height="75" name="captchaImg" />

// Write directly to file
<bx:image action="captcha" text="A3X9K2" destination="/path/to/captcha.png" />
```

**Difficulty levels:**

- `low` — minimal character rotation, clean background
- `medium` — moderate rotation, noise dots, crossing lines
- `high` — heavy rotation, dense noise, wavy lines, random character colors

### QR Codes

Generate a QR code from text. The result is a regular image, so `ImageWrite()`, `ImageResize()`, `ImageWriteBase64()` and the rest of the image functions all work on it.

```javascript
// Basic QR code, 125px PNG
qr = ImageQRCodeGenerate( "https://example.com/verify" );
ImageWrite( qr, "/path/to/qr.png" );

// text, size, format, errorCorrection, quietZone
qr = ImageQRCodeGenerate( "ME:12345678", 250, "gif", "H" );

// Colors and a logo
qr = ImageQRCodeGenerate(
    text       = "https://example.com",
    size       = 400,
    foreground = "#1a237e",
    background = "white",
    logo       = "/path/to/logo.png"
);

// Member function on a string
qr = "https://example.com".toQRCode( 250 );

// Read a QR code back out of an image (empty string if none is found)
text = ImageQRCodeRead( ImageRead( "/path/to/qr.png" ) );
text = qr.readQRCode();

// Via component
<bx:image action="qrcode" text="https://example.com" size="250" name="qr" />
<bx:image action="qrcode" text="https://example.com" destination="/path/to/qr.png" />
<bx:image action="qrread" source="/path/to/qr.png" name="decoded" />
```

**Arguments of `ImageQRCodeGenerate()`:**

| Argument | Default | Description |
| --- | --- | --- |
| `text` | required | The text to encode. |
| `size` | `125` | Edge length in pixels, clamped to 16 to 4096. |
| `format` | `png` | Any format returned by `GetWriteableImageFormats()`. |
| `errorCorrection` | `M` | `L`, `M`, `Q` or `H`. Always `H` when a logo is used. |
| `quietZone` | `4` | Light margin in modules, clamped to 0 to 32. |
| `foreground` | `black` | Color of the dark modules. A color name or hex code. |
| `background` | `white` | Color of the light modules. A color name or hex code. |
| `logo` | none | An image, file path or URL drawn in the center. |
| `logoScale` | `0.2` | Fraction of the code's edge the logo covers, up to 0.3. |

A logo hides part of the code. If a logo would make the code unreadable, for example a large logo on a very short text, `ImageQRCodeGenerate()` throws instead of returning a code that will not scan. Lower `logoScale`, raise `size`, or encode more text.

### Member Functions vs BIFs

Most functions work both ways:

```javascript
// As BIF (Built-In Function)
imageBlur(img, 5);
imageCrop(img, 10, 10, 200, 200);

// As member function (chainable!)
img.blur(5)
   .crop(10, 10, 200, 200)
   .write();
```

## BIFs

This module contributes the following BIFs:

- [GetReadableImageFormats](https://cfdocs.org/GetReadableImageFormats)
- [GetWriteableImageFormats](https://cfdocs.org/GetWriteableImageFormats)
- [ImageAddBorder](https://cfdocs.org/ImageAddBorder)
- [ImageBlur](https://cfdocs.org/ImageBlur)
- [ImageClearRect](https://cfdocs.org/ImageClearRect)
- [ImageCopy](https://cfdocs.org/ImageCopy)
- [ImageCrop](https://cfdocs.org/ImageCrop)
- [ImageDrawArc](https://cfdocs.org/ImageDrawArc)
- [ImageDrawBeveledRect](https://cfdocs.org/ImageDrawBeveledRect)
- [ImageDrawCubicCurve](https://cfdocs.org/ImageDrawCubicCurve)
- [ImageDrawLine](https://cfdocs.org/ImageDrawLine)
- [ImageDrawLines](https://cfdocs.org/ImageDrawLines)
- [ImageDrawOval](https://cfdocs.org/ImageDrawOval)
- [ImageDrawPoint](https://cfdocs.org/ImageDrawPoint)
- [ImageDrawQuadraticCurve](https://cfdocs.org/ImageDrawQuadraticCurve)
- [ImageDrawRect](https://cfdocs.org/ImageDrawRect)
- [ImageDrawRoundRect](https://cfdocs.org/ImageDrawRoundRect)
- [ImageDrawText](https://cfdocs.org/ImageDrawText)
- [ImageFlip](https://cfdocs.org/ImageFlip)
- [ImageGenerateCaptcha](https://cfdocs.org/ImageCreateCaptcha) - Generate a CAPTCHA image with distorted text. Args: `height, width, text [, difficulty [, fonts [, fontSize]]]`
- [ImageGetBlob](https://cfdocs.org/ImageGetBlob)
- [ImageGetBufferedImage](https://cfdocs.org/ImageGetBufferedImage)
- [ImageGetExifMetaData](https://cfdocs.org/ImageGetExifMetaData)
- [ImageGetExifTag](https://cfdocs.org/ImageGetExifTag)
- [ImageGetHeight](https://cfdocs.org/ImageGetHeight)
- [ImageGetIPTCMetadata](https://cfdocs.org/ImageGetIPTCMetadata)
- [ImageGetIPTCTag](https://cfdocs.org/ImageGetIPTCTag)
- [ImageGetWidth](https://cfdocs.org/ImageGetWidth)
- [ImageGrayScale](https://cfdocs.org/ImageGrayScale) - also aliased as [`ImageGreyScale()`](https://cfdocs.org/ImageGrayScale) for you brits
- [ImageInfo](https://cfdocs.org/ImageInfo)
- [ImageNegative](https://cfdocs.org/ImageNegative)
- [ImageNew](https://cfdocs.org/ImageNew)
- [ImageOverlay](https://cfdocs.org/ImageOverlay)
- [ImagePaste](https://cfdocs.org/ImagePaste) - aliased as [`imagePaste()`](https://cfdocs.org/imagePaste)
- `ImageQRCodeGenerate` - Generate a QR code image. Args: `text [, size [, format [, errorCorrection [, quietZone [, foreground [, background [, logo [, logoScale]]]]]]]]`. Also available as the string member function `toQRCode()`
- `ImageQRCodeRead` - Read the text from a QR code in an image, or an empty string if there is none. Args: `name [, tryHarder]`. Also available as the member function `readQRCode()`
- [ImageRead](https://cfdocs.org/ImageRead)
- [ImageReadBase64](https://cfdocs.org/ImageReadBase64)
- [ImageResize](https://cfdocs.org/ImageResize)
- [ImageRotate](https://cfdocs.org/ImageRotate)
- [ImageRotateDrawingAxis](https://cfdocs.org/ImageRotateDrawingAxis)
- [ImageScaleToFit](https://cfdocs.org/ImageScaleToFit)
- [ImageSetAntiAliasing](https://cfdocs.org/ImageSetAntiAliasing)
- [ImageSetBackgroundColor](https://cfdocs.org/ImageSetBackgroundColor)
- [ImageSetDrawingColor](https://cfdocs.org/ImageSetDrawingColor)
- [ImageSetDrawingStroke](https://cfdocs.org/ImageSetDrawingStroke)
- [ImageSetDrawingTransparency](https://cfdocs.org/ImageSetDrawingTransparency)
- [ImageSharpen](https://cfdocs.org/ImageSharpen)
- [ImageShear](https://cfdocs.org/ImageShear)
- [ImageShearDrawingAxis](https://cfdocs.org/ImageShearDrawingAxis)
- [ImageSplitGrid](https://cfdocs.org/ImageSplitGrid) - Split an image into a grid of tiles. Returns a 2D array: `tiles[row][col]`
- [ImageTranslate](https://cfdocs.org/ImageTranslate)
- [ImageTranslateDrawingAxis](https://cfdocs.org/ImageTranslateDrawingAxis)
- [ImageWrite](https://cfdocs.org/ImageWrite)
- [ImageWriteBase64](https://cfdocs.org/ImageWriteBase64)
- [IsImage](https://cfdocs.org/IsImage)
- [IsImageFile](https://cfdocs.org/IsImageFile)

Most of these BIFs are also implemented as member functions on the `BoxImage` type, so `imageGrayScale( myImage )` can also be written as `myImage.grayScale()`.

## Components

This module provides the **`<bx:image>`** component for tag-based image manipulation.

### Quick Reference

The `<bx:image>` component supports the following actions:

```javascript
// Read an image
<bx:image action="read" source="photo.jpg" name="myImage" />

// Resize
<bx:image action="resize" source="#myImage#" width="800" height="600" />

// Rotate
<bx:image action="rotate" source="#myImage#" angle="45" destination="rotated.jpg" />

// Add border
<bx:image action="border" source="#myImage#" color="black" thickness="5" />

// Write to file
<bx:image action="write" source="#myImage#" destination="output.jpg" />

// Get image info
<bx:image action="info" source="#myImage#" structName="imageInfo" />

// Write to browser
<bx:image action="writeToBrowser" source="#myImage#" />
```

**Supported Actions:**

- `read` - Load an image from file or URL
- `write` - Save image to file
- `writeToBrowser` - Stream image to HTTP response (supports `format` attribute)
- `resize` - Change image dimensions
- `rotate` - Rotate image by angle
- `border` - Add border around image
- `info` - Get image metadata
- `convert` - Convert image format
- `captcha` - Generate a CAPTCHA image (v1.6.0+)
- `qrcode` - Generate a QR code image
- `qrread` - Read the text from a QR code into the variable named by `name`

**Common Attributes:**

- `source` - Path to image file, URL, or BoxImage variable
- `name` - Variable name to store the image
- `destination` - Output file path for write operations
- `width`, `height` - Dimensions for resize
- `angle` - Rotation angle in degrees
- `color` - Color name or hex code
- `thickness` - Border thickness in pixels
- `overwrite` - Boolean, allow overwriting existing files (default: false)
- `isBase64` - Boolean, indicates if source is Base64-encoded

💡 **Tip:** For complex image manipulation workflows, consider using the fluent API instead of components for better readability and maintainability.

## Settings

Set these under the `bx-image` module settings in `boxlang.json`.

| Setting | Default | Description |
| --- | --- | --- |
| `maxPixels` | `50000000` | Maximum width x height an image may declare. Checked from the header before decoding. `0` disables. |
| `maxBytes` | `52428800` | Maximum source size in bytes for files, URLs and Base64 data. `0` disables. |

## Image Sources

`imageNew()` and `imageRead()` accept the same sources:

| Source | Example |
| ------ | ------- |
| `http:` or `https:` URL | `imageRead( "https://example.com/logo.png" )` |
| `file:` URI | `imageRead( "file:///tmp/logo.png" )` |
| Relative path, resolved from the current template and mappings | `imageRead( "images/logo.png" )` |
| Absolute path, including Windows drive paths with `\` or `/` | `imageRead( "D:\\a\\x\\card.png" )`, `imageRead( "C:/x/card.png" )` |

Anything that is not an `http:`, `https:` or `file:` URI is treated as a local path and resolved with `expandPath()`. A source that cannot be loaded throws `Unable to load image: <source>`.

## Important Notes

### File Handling

The module properly manages file handles when reading images. After loading an image with `imageRead()` or `imageNew()`, the underlying file stream is automatically closed, allowing you to safely delete or move the source file:

```javascript
// This works correctly - file can be deleted after reading
img = imageRead("photo.jpg");
img.resize(800, 600).write("photo-resized.jpg");
fileDelete("photo.jpg");  // ✅ Works - no file lock
```

This is especially important on Windows, where file locks can prevent file operations.

### Directory Creation

When writing images, parent directories are automatically created if they don't exist:

```javascript
// Creates 'output/thumbs/' directory if needed
img.write("output/thumbs/photo.jpg");
```

### Format Detection

The `write()` and `ImageWrite()` methods now auto-detect the output format from the file extension rather than defaulting to PNG. For example:

```javascript
// Correctly writes JPEG, WebP, GIF, BMP, or TIFF based on extension
img.write("photo.jpg");   // → JPEG
img.write("photo.webp");  // → WebP
img.write("photo.gif");   // → GIF
img.write("photo.bmp");   // → BMP
img.write("photo.tiff");  // → TIFF
```

### Alpha Channel Handling

When writing to formats that do not support transparency (JPEG, BMP), images with alpha channels are automatically composited onto a white background. This prevents write failures and produces visually correct output.

## Examples

Blur, crop, and grayscale a png image before saving it back to disk:

```js
var updatedLogo = ImageRead( "src/test/resources/logo.png" )
    .blur( 5 )
    .crop( x = 50, y = 50, width = 150, height = 100 )
    .grayScale();
imageWrite( updatedLogo, "src/test/resources/logoNew.png" );
```

## Ortus Sponsors

BoxLang is a professional open-source project and it is completely funded by the [community](https://patreon.com/ortussolutions) and [Ortus Solutions, Corp](https://www.ortussolutions.com).  Ortus Patreons get many benefits like a cfcasts account, a FORGEBOX Pro account and so much more.  If you are interested in becoming a sponsor, please visit our patronage page: [https://patreon.com/ortussolutions](https://patreon.com/ortussolutions)

### THE DAILY BREAD

 > "I am the way, and the truth, and the life; no one comes to the Father, but by me (JESUS)" Jn 14:1-12
