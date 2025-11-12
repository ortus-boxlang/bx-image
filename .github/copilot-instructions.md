# BoxLang Image Module - AI Coding Instructions

## Project Overview

This is a BoxLang module that provides image manipulation functionality via Built-In Functions (BIFs), member functions, and components. It wraps Java AWT/ImageIO operations in a CFML-compatible API, targeting compatibility with Adobe ColdFusion and Lucee image functions.

**Architecture**: Java implementation (BIFs/Components) + BoxLang glue code (ModuleConfig.bx) + Gradle-based build system

## Core Components

### 1. BoxImage Class (`src/main/java/ortus/boxlang/modules/image/BoxImage.java`)
- Central wrapper around `javaxt.io.Image` and Java's `BufferedImage`
- Maintains image state: `Graphics2D` graphics context, drawing colors, metadata (EXIF/IPTC)
- All BIFs delegate to methods on this class
- Supports creation from: file paths, URLs, URIs, Base64, BufferedImage, or blank canvas

### 2. BIF Implementation Pattern
All BIFs follow this structure:
```java
@BoxBIF
@BoxMember(type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "methodName")
public class ImageMethodName extends BIF {
    // Constructor declares arguments with validators
    public ImageMethodName() {
        declaredArguments = new Argument[] {
            new Argument(true, "any", ImageKeys.name, Set.of(Validator.REQUIRED)),
            // ... more arguments
        };
    }
    
    // Implementation returns BoxImage for chaining
    public BoxImage _invoke(IBoxContext context, ArgumentsScope arguments) {
        BoxImage theImage = /* extract from arguments */;
        theImage.someMethod(/* ... */);
        return theImage;
    }
}
```

**Key Patterns**:
- `@BoxBIF` registers as global function (e.g., `ImageCrop()`)
- `@BoxMember` registers as member function (e.g., `image.crop()`)
- Use `@BoxBIF(alias = "AlternateName")` for aliasing (see `ImagePaste.java`)
- First argument typically `ImageKeys.name` of type `any` - can be BoxImage or variable name string
- Return `BoxImage` to enable method chaining
- Use `ImageKeys.*` constants for argument names to ensure consistency

### 3. Module Configuration (`src/main/bx/ModuleConfig.bx`)
- BoxLang entry point defining module metadata and interceptors
- Registers the `ImageEvents` interceptor for lifecycle hooks
- Version must match `gradle.properties` via token replacement (`@build.version@`)

### 4. Service Loader Registration
BIFs/Components are auto-discovered via Gradle plugin:
```gradle
serviceLoader {
    serviceInterface 'ortus.boxlang.runtime.bifs.BIF'
    serviceInterface 'ortus.boxlang.runtime.components.Component'
}
```
This generates `META-INF/services/` files for Java ServiceLoader mechanism.

## Development Workflow

### Building the Module
```bash
# Full build (compiles, tests, creates module structure)
./gradlew build

# Skip tests for faster iteration
./gradlew build -x test

# Build outputs:
# - build/libs/bx-image-{version}.jar (shadow jar with dependencies)
# - build/module/ (complete module structure ready for deployment)
# - build/distributions/bx-image-{version}.zip (distributable package)
```

### Testing
- Base class: `BaseIntegrationTest` sets up BoxRuntime and loads module from `build/module/`
- **Critical**: Must run `./gradlew build -x test` before tests to create module structure
- Tests use BoxLang script execution: `instance.executeSource("...", context)`
- Test images in `src/test/resources/test-images/`
- Tests often write output images to `src/test/resources/` for visual verification

Example test structure:
```java
public class ImageSomeTest extends BaseIntegrationTest {
    @Test
    public void testSomething() {
        instance.executeSource("""
            result = ImageNew("src/test/resources/logo.png")
                .crop(50, 50, 150, 100)
                .grayScale();
            ImageWrite(result, "src/test/resources/output.png");
        """, context);
    }
}
```

### Adding New BIFs
1. Create BIF class in `src/main/java/ortus/boxlang/modules/image/bifs/`
2. Add `@BoxBIF` and optionally `@BoxMember` annotations
3. Implement `_invoke()` method delegating to `BoxImage` method
4. Add corresponding method to `BoxImage.java` if needed
5. Add constant to `ImageKeys.java` for new argument names
6. Create test class in `src/test/java/ortus/boxlang/modules/image/bifs/`
7. ServiceLoader auto-registers during build (no manual registration needed)

### Version Management
- Versions in 3 places: `gradle.properties`, `box.json`, `ModuleConfig.bx`
- `ModuleConfig.bx` uses token replacement: `this.version = "@build.version@";`
- Development branch adds `-snapshot` suffix automatically (see `build.gradle` line 26-31)
- BoxLang runtime dependency stored in `src/test/resources/libs/boxlang-{version}.jar`

## Module-Specific Conventions

### Argument Handling
- Use `IntegerCaster.cast()`, `StringCaster.cast()`, etc. for type conversion
- Check `CastAttempt.wasSuccessful()` for nullable conversions
- First argument pattern: accept either BoxImage instance OR variable name string
```java
BoxImage theImage = arguments.get(ImageKeys.name) instanceof BoxImage
    ? (BoxImage) arguments.get(ImageKeys.name)
    : (BoxImage) context.getDefaultAssignmentScope().get(arguments.getAsString(ImageKeys.name));
```

### Color Handling
- Accept string color names (see `BoxImage.COLORS` map) or hex codes
- Use `BoxImage.parseColor(String)` for conversion to `java.awt.Color`

### Metadata Extraction
- EXIF/IPTC metadata extracted on image load via `ImageMetadataUtil`
- Stored in `BoxImage.exifData` and `BoxImage.iptcData` as `IStruct`
- Uses `com.drew.metadata` library for parsing

### Build System Quirks
- Shadow jar plugin merges dependencies (Apache Commons Imaging, javaxt-core, metadata-extractor)
- `createModuleStructure` task copies jar + BoxLang files to `build/module/` with token replacement
- Must use `shadowJar` task for distributable builds (standard `jar` lacks dependencies)

## Common Pitfalls

1. **Forgetting to build module before testing**: Tests load from `build/module/`, not `src/`
2. **Not returning BoxImage from BIFs**: Breaks method chaining (e.g., `image.crop().blur()`)
3. **Hardcoding argument names**: Always use `ImageKeys.*` constants
4. **Missing @BoxMember**: BIF works but member function doesn't (e.g., `image.method()` fails)
5. **Version mismatches**: Ensure all 3 version locations are updated together
6. **BoxLang runtime dependency**: Must match version in `gradle.properties` and be present in `src/test/resources/libs/`

## Key External Dependencies

- `javaxt:javaxt-core:2.1.9` - Core image manipulation (rotation, scaling, EXIF)
- `org.apache.commons:commons-imaging` - Additional format support
- `com.drewnoakes:metadata-extractor` - EXIF/IPTC metadata reading
- BoxLang runtime (locally cached in test resources, not from Maven Central yet)

## Release Process

1. Update versions in `gradle.properties`, `box.json`, `ModuleConfig.bx`
2. Update `changelog.md` with release notes
3. Merge `development` → `master` via `box run src/build/release.boxr` recipe
4. GitHub Actions workflow (`release.yml`) auto-publishes on master push
5. Artifacts published to Ortus downloads server and ForgeBox

## References

- [CFDocs Image Functions](https://cfdocs.org/) - Target compatibility reference
- [BoxLang Runtime Source](https://github.com/ortus-boxlang/BoxLang) - For BIF/Component patterns
- Module follows conventions from BoxLang module template
