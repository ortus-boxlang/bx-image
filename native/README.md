# MatchBox image pilot

The published package remains **bx-image**. Its existing JVM JAR and ModuleConfig are unchanged. This directory adds a Rust provider for blank black RGB images, `imageInfo(image)`, and `image.info()` with width/height metadata. File/URL loading, drawing, additional image types/colors, and Java-specific APIs are not implemented in this initial native slice; unsupported input fails explicitly.

## Author-side build

The SDK path in Cargo.toml currently points at the adjacent `matchbox-native-modules` worktree; update it to the merged SDK location or published crate when integrating these branches. It does not depend on the VM or require a matching Rust compiler in the consumer runtime.

Generate the normal JVM package first, then add the native target:

```bash
./gradlew shadowJar -PmatchboxTargets=x86_64-unknown-linux-gnu
# Or add it to an already-built package:
python3 native/package.py build/module x86_64-unknown-linux-gnu
```

`package.py` builds on the author's machine/CI, writes the artifact and its SHA-256 metadata into `build/module/box.json`. The Gradle ZIP task runs this before archiving when `matchboxTargets` is supplied. Comma-separated target triples are supported; authors supply their cross-compilation toolchains. Consumers install this same ZIP and do not compile native code.

## Shared API check

```bash
matchbox native/image-contract.bxs --module build/module
matchbox --target native native/image-contract.bxs --module build/module --output image-app
./image-app

# Use a complete BoxLang runtime/distribution JAR, not its thin build/libs JAR.
java -cp /path/to/boxlang-distribution.jar native/ImageContract.java build/module native/image-contract.bxs
cargo test --manifest-path native/Cargo.toml
```

The JVM harness uses an isolated runtime home and explicitly loads **this package** before executing the same script. Extra MatchBox artifact metadata is ignored by the existing JVM module loader.
