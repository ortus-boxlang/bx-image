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

This module provides image manipulation functionality. This module is part of the [BoxLang](https://boxlang.io/) project.

## BIFs

This module contributes the following BIFs:

* [GetReadableImageFormats](https://cfdocs.org/GetReadableImageFormats)
* [GetWriteableImageFormats](https://cfdocs.org/GetWriteableImageFormats)
* [ImageAddBorder](https://cfdocs.org/ImageAddBorder)
* [ImageBlur](https://cfdocs.org/ImageBlur)
* [ImageClearRect](https://cfdocs.org/ImageClearRect)
* [ImageCopy](https://cfdocs.org/ImageCopy)
* [ImageCrop](https://cfdocs.org/ImageCrop)
* [ImageDrawArc](https://cfdocs.org/ImageDrawArc)
* [ImageDrawBeveledRect](https://cfdocs.org/ImageDrawBeveledRect)
* [ImageDrawCubicCurve](https://cfdocs.org/ImageDrawCubicCurve)
* [ImageDrawLine](https://cfdocs.org/ImageDrawLine)
* [ImageDrawLines](https://cfdocs.org/ImageDrawLines)
* [ImageDrawOval](https://cfdocs.org/ImageDrawOval)
* [ImageDrawPoint](https://cfdocs.org/ImageDrawPoint)
* [ImageDrawQuadraticCurve](https://cfdocs.org/ImageDrawQuadraticCurve)
* [ImageDrawRect](https://cfdocs.org/ImageDrawRect)
* [ImageDrawRoundRect](https://cfdocs.org/ImageDrawRoundRect)
* [ImageDrawText](https://cfdocs.org/ImageDrawText)
* [ImageFlip](https://cfdocs.org/ImageFlip)
* [ImageGetBlob](https://cfdocs.org/ImageGetBlob)
* [ImageGetBufferedImage](https://cfdocs.org/ImageGetBufferedImage)
* [ImageGetExifMetaData](https://cfdocs.org/ImageGetExifMetaData)
* [ImageGetExifTag](https://cfdocs.org/ImageGetExifTag)
* [ImageGetHeight](https://cfdocs.org/ImageGetHeight)
* [ImageGetIPTCMetadata](https://cfdocs.org/ImageGetIPTCMetadata)
* [ImageGetIPTCTag](https://cfdocs.org/ImageGetIPTCTag)
* [ImageGetWidth](https://cfdocs.org/ImageGetWidth)
* [ImageGrayScale](https://cfdocs.org/ImageGrayScale) - also aliased as [`ImageGreyScale()`](https://cfdocs.org/ImageGrayScale) for you brits
* [ImageInfo](https://cfdocs.org/ImageInfo)
* [ImageNegative](https://cfdocs.org/ImageNegative)
* [ImageNew](https://cfdocs.org/ImageNew)
* [ImageOverlay](https://cfdocs.org/ImageOverlay)
* [ImagePaste](https://cfdocs.org/ImagePaste) - aliased as [`imagePaste()`](https://cfdocs.org/imagePaste)
* [ImageRead](https://cfdocs.org/ImageRead)
* [ImageReadBase64](https://cfdocs.org/ImageReadBase64)
* [ImageResize](https://cfdocs.org/ImageResize)
* [ImageRotate](https://cfdocs.org/ImageRotate)
* [ImageRotateDrawingAxis](https://cfdocs.org/ImageRotateDrawingAxis)
* [ImageScaleToFit](https://cfdocs.org/ImageScaleToFit)
* [ImageSetAntiAliasing](https://cfdocs.org/ImageSetAntiAliasing)
* [ImageSetBackgroundColor](https://cfdocs.org/ImageSetBackgroundColor)
* [ImageSetDrawingColor](https://cfdocs.org/ImageSetDrawingColor)
* [ImageSetDrawingStroke](https://cfdocs.org/ImageSetDrawingStroke)
* [ImageSetDrawingTransparency](https://cfdocs.org/ImageSetDrawingTransparency)
* [ImageSharpen](https://cfdocs.org/ImageSharpen)
* [ImageShear](https://cfdocs.org/ImageShear)
* [ImageShearDrawingAxis](https://cfdocs.org/ImageShearDrawingAxis)
* [ImageTranslate](https://cfdocs.org/ImageTranslate)
* [ImageTranslateDrawingAxis](https://cfdocs.org/ImageTranslateDrawingAxis)
* [ImageWrite](https://cfdocs.org/ImageWrite)
* [ImageWriteBase64](https://cfdocs.org/ImageWriteBase64)
* [IsImage](https://cfdocs.org/IsImage)
* [IsImageFile](https://cfdocs.org/IsImageFile)

Most of these BIFs are also implemented as member functions on the `BoxImage` type, so `imageGrayScale( myImage )` can also be written as `myImage.grayScale()`.

## Component

This module contains no BoxLang Components.

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
