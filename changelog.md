# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

* * *

## [Unreleased]

### Added

- WebP image format support for reading and writing via the `org.sejda.imageio:webp-imageio` ImageIO plugin. All BIFs that read or write images (`ImageRead`, `ImageWrite`, `ImageWriteBase64`, `ImageReadBase64`, `IsImageFile`) now handle WebP natively.
- GIF image format support for reading and writing. Java's built-in ImageIO GIF codec is now fully exposed through `ImageRead`, `ImageWrite`, and `ImageWriteBase64`.
- `GetReadableImageFormats()` and `GetWriteableImageFormats()` now include `webp` and `gif` in their results.

### Fixed

- `ImageWrite` and `img.write(path)` were hardcoded to always produce PNG data regardless of the destination file extension. Writing to `.jpg`, `.webp`, or any non-PNG path now correctly encodes in the target format.
- Writing a PNG image (which has an alpha channel) to a JPEG destination no longer fails — the image is automatically composited onto a white background before encoding, since JPEG does not support transparency.

### Updated

- Replaced `org.apache.commons.imaging.Imaging.writeImage()` with `javax.imageio.ImageIO.write()` as the underlying write mechanism, enabling format detection from the file extension and support for any registered ImageIO plugin (JPG, PNG, WebP, GIF, BMP, TIFF).

## [1.6.0] - 2026-05-25

### Added

- `ImageGenerateCaptcha( height, width, text [, difficulty [, fonts [, fontSize]]] )` BIF for generating CAPTCHA images with configurable dimensions, font size, difficulty level (`low`/`medium`/`high`), and font list. Argument order is ColdFusion-compatible.
- `<bx:image action="captcha">` component support with `text`, `width`, `height`, `fontSize`, `difficulty`, `fonts`, `destination`, `overwrite`, and `name` attributes. When neither `name` nor `destination` is specified, the image is automatically streamed to the browser.

### Fixed

- javaxt.com has been down for weeks, moving to single compiled jar and looking for alternatives.
- `ModuleConfig.bx` version was not dynamic.

## [1.5.0] - 2026-02-18

- BLMODULES-139 Update writeToBrowser to accept format attribute
- BLMODULES-138 Improve base64 generation and auto detect format

## [1.4.0] - 2025-11-12

### Added

- Updated all GitHub actions to latest according to templates
- Updated templates to latest module template
- Bump javaxt:javaxt-core from 2.1.9 to 2.1.11
- Generate AI Instructions
- Dependabot updates
- Updated Gradle wrapper to 8.14.1
- Updated gradle build to latest module template
- Added documentation to classes

### Changed

- All tests to inherit from BaseIntegrationTest for consistency
- Refactored internal classes into functional packaging
- Rewrote the ImageDrawTextTest to use more reliable image size assertions and work on all Operating Systems

### Fixed

- Resource leak when reading images into the input stream and not closing it.
- Added jaxt library to gradle dependencies
- Fixed writing of images to directories that don't exist. Now creates parent directories as needed.
- image.scaleToFit() now works with a single value and more.
- write() now works with no provided path, uses internally read source path.

## [1.3.2] - 2025-07-25

### Changed

- Removed logging from ImageService startup/shutdown

## [1.3.1] - 2025-07-24

### Changed

- Version bump maintenance release

## [1.3.0] - 2025-07-23

### Fixed

- BL-1216 Fix ImageScaleToFit BIF
- BL-1217 Fix invoking resize as a member function

## [1.1.0] - 2025-02-13

## [1.0.1] - 2024-06-27

## [1.0.0] - 2024-06-27

## [1.0.0] => 2024-APR-05

- First iteration of this module

[unreleased]: https://github.com/ortus-boxlang/bx-image/compare/v1.6.0...HEAD
[1.6.0]: https://github.com/ortus-boxlang/bx-image/compare/v1.5.0...v1.6.0
[1.5.0]: https://github.com/ortus-boxlang/bx-image/compare/v1.4.0...v1.5.0
[1.4.0]: https://github.com/ortus-boxlang/bx-image/compare/v1.3.2...v1.4.0
[1.3.2]: https://github.com/ortus-boxlang/bx-image/compare/v1.3.1...v1.3.2
[1.3.1]: https://github.com/ortus-boxlang/bx-image/compare/v1.3.0...v1.3.1
[1.3.0]: https://github.com/ortus-boxlang/bx-image/compare/v1.1.0...v1.3.0
[1.1.0]: https://github.com/ortus-boxlang/bx-image/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/ortus-boxlang/bx-image/compare/v1.0.1...v1.0.1
[1.0.0]: https://github.com/ortus-boxlang/bx-image/compare/c673f34388fa8707a7811ce7789da0686e2f0bd5...v1.0.0
