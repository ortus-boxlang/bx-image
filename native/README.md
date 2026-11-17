# bx-image MatchBox backend

The published package is still **bx-image**: one module, one zip, one version. The JVM jar and `ModuleConfig.bx` are unchanged. This folder holds a Rust plugin that MatchBox loads instead when the module runs without a JVM. It is listed under `matchbox.artifacts` in the packaged `box.json`, and the JVM module loader ignores that metadata.

## What it supports

Same names and arguments as the JVM backend:

| Function | Member | Notes |
| --- | --- | --- |
| `imageRead( path, [width], [height] )` | | PNG, JPEG, GIF, WebP and SVG, detected by extension or by content. Local files only. With a size, an SVG is drawn straight at it (fitted and centred when both are given) and other formats are resized after decoding. An SVG with neither a viewBox nor an absolute width and height reads as 512 x 512, as on the JVM. |
| `imageNew( source, [width], [height], [imageType], [color] )` | | A path reads the file. `""` makes a blank image: `imageType` is `rgb`, `argb` or `grayscale`; `color` is `#RRGGBB` or black, white, red, green, blue, gray, yellow, cyan, magenta. An image source is copied. |
| `imageResize( name, width, height, [interpolation] )` | `resize()` | A blank or `0` width or height keeps the aspect ratio. `interpolation` is `bilinear` (default), `bicubic`, `nearest` or `lanczos`. An image read from SVG is redrawn at the new size, so it stays sharp. `blurFactor` is accepted and ignored, as on the JVM. |
| `imageGrayScale( name )`, `imageGreyScale( name )` | `grayScale()`, `greyScale()` | Like the JVM's `TYPE_BYTE_GRAY` image: composited onto black and opaque. |
| `imageInfo( name )` | `info()` | `width`, `height` and `source` (the path, or null). No `colormodel`. |
| `imageGetPixels( name, [mode="luma"] )` | `getPixels()` | An array of rows, top to bottom. `luma`: a number from 0 to 255 per pixel, `(77r + 150g + 29b + 128) >> 8`, alpha ignored. `rgba`: `[r, g, b, a]` per pixel. |
| `getReadableImageFormats()` | | `PNG`, `JPEG`, `JPG`, `GIF`, `WEBP`, `SVG`. |

Differences from the JVM:

- `resize()` on an image read from SVG redraws it on MatchBox; the JVM scales the raster. Read at the size you want (`imageRead( path, width )`) for the same sharp result on both.
- `name` must be the image itself; looking an image up by variable name is JVM-only.
- URLs, writing, drawing, metadata and the other bx-image functions are not implemented, and fail with a clear error.
- SVG text uses the system fonts found when the first SVG is read.
- Images are capped at 16384 pixels per side and 256 megapixels.

Pixels are kept as straight (not premultiplied) RGBA. The luma weights are the integers Java2D uses for grayscale images, so `imageGetPixels` returns the same numbers on both backends.

## Build

`native/Cargo.toml` takes `matchbox-plugin-sdk` straight from GitHub, pinned to a commit, because the SDK is not on crates.io:

```toml
matchbox-plugin-sdk = { git = "https://github.com/ortus-boxlang/matchbox", rev = "fe1503d8672a" }
```

That commit adds the `matchbox.types.v1` and `matchbox.collections.v1` capabilities the plugin requires, so it needs a MatchBox at or after it. This is an author-side dependency only: consumers install the prebuilt zip and do not compile anything.

Build the JVM package with the native plugin added (use `build`, not just `shadowJar`, so the jar gets its BIF service file):

```bash
./gradlew build -x test -PmatchboxTargets=x86_64-unknown-linux-gnu
# Or add a target to an already-built package:
python3 native/package.py build/module x86_64-unknown-linux-gnu
```

`package.py` runs `cargo build --release`, copies the library into `build/module/native/<target>/` and records its path and SHA-256 in `build/module/box.json`. Several targets can be passed comma-separated; you supply the cross-compilation toolchains. CI builds `x86_64-unknown-linux-gnu` for releases.

## Tests

One script, `native/image-contract.bxs`, runs on every runtime. It reads the fixtures in `src/test/resources/native` (or the folder in `BX_IMAGE_FIXTURES`), resizes, converts to grayscale, checks sizes and pixel values, and checks that a missing file and a malformed SVG fail with `Unable to load image`. The SVG checks run where `getReadableImageFormats()` lists SVG.

```bash
cargo test --manifest-path native/Cargo.toml

# MatchBox, interpreted. Options go before the script.
matchbox --module build/module native/image-contract.bxs

# MatchBox, native binary: the plugin is embedded, so it runs anywhere
matchbox --target native --module build/module --output image-app native/image-contract.bxs
./image-app

# JVM: use a complete BoxLang runtime jar, such as the one from ./gradlew downloadBoxLang.
# The harness runs the script as source, so it needs BX_IMAGE_FIXTURES.
BX_IMAGE_FIXTURES=$PWD/src/test/resources/native java -cp src/test/resources/libs/boxlang-1.13.0-snapshot.jar native/ImageContract.java build/module native/image-contract.bxs
```

The JVM harness uses an isolated runtime home and loads this package before running the script.
