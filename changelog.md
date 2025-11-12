# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

* * *

## [Unreleased]

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

## [1.0.1] - 2024-12-30

## [1.0.1] - 2024-12-30

## [1.0.1] - 2024-12-30

## [1.0.1] - 2024-06-27

## [1.0.0] - 2024-06-27

## [1.0.0] => 2024-APR-05

- First iteration of this module

[unreleased]: https://github.com/ortus-boxlang/bx-image/compare/v1.4.0...HEAD
[1.4.0]: https://github.com/ortus-boxlang/bx-image/compare/v1.3.2...v1.4.0
[1.3.2]: https://github.com/ortus-boxlang/bx-image/compare/v1.3.1...v1.3.2
[1.3.1]: https://github.com/ortus-boxlang/bx-image/compare/v1.3.0...v1.3.1
[1.3.0]: https://github.com/ortus-boxlang/bx-image/compare/v1.1.0...v1.3.0
[1.1.0]: https://github.com/ortus-boxlang/bx-image/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/ortus-boxlang/bx-image/compare/v1.0.1...v1.0.1
[1.0.0]: https://github.com/ortus-boxlang/bx-image/compare/c673f34388fa8707a7811ce7789da0686e2f0bd5...v1.0.0
