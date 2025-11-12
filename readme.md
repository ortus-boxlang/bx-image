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

The BoxLang Image module provides comprehensive image manipulation functionality with a fluent, chainable API. Read, create, transform, filter, and save images with ease. This module brings CFML-compatible image functions to BoxLang while adding modern enhancements and conveniences.

**Key Features:**

- 📸 Read/write images from files, URLs, or Base64
- ✂️ Crop, resize, rotate, flip, and scale images
- 🎨 Draw shapes, text, lines, and curves
- 🎭 Apply filters: blur, sharpen, grayscale, negative
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
img = imageRead("photo.jpg");
img.scaleToFit(800, 600)
   .blur(2)
   .sharpen(1)
	.write("photo-optimized.jpg");

// Create a new image with drawing
canvas = imageNew("", 400, 300, "rgb", "white");
canvas.setDrawingColor("blue")
      .drawRect(50, 50, 300, 200, true)
      .setDrawingColor("red")
      .drawText("Hello BoxLang!", 150, 150)
	  .write("greeting.png");

// Chain operations
imageRead("logo.png")
    .crop(10, 10, 200, 200)
    .grayScale()
    .rotate(45)
    .write("logo-transformed.png");
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

img.write("output.png")                      // Write to file
img.write()                                  // Write back to source
base64 = imageWriteBase64(img)               // Export as Base64
blob = imageGetBlob(img)                     // Export as binary
```

#### Transformations

```javascript
img.resize(width, height)                    // Exact dimensions
img.resize(width, height, interpolation)     // With interpolation method
img.scaleToFit(size)                         // Fit to width, maintain aspect
img.scaleToFit(maxWidth, maxHeight)          // Fit within bounds
img.crop(x, y, width, height)                // Extract region
img.rotate(angle)                            // Rotate degrees
img.flip("horizontal")                       // Flip horizontal
img.flip("vertical")                         // Flip vertical
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
    width: 2,
    endCaps: "round",
    lineJoins: "miter"
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
- `writeToBrowser` - Stream image to HTTP response
- `resize` - Change image dimensions
- `rotate` - Rotate image by angle
- `border` - Add border around image
- `info` - Get image metadata
- `convert` - Convert image format

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
