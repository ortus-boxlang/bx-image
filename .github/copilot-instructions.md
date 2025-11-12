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

#### Test Structure and Base Classes
All module tests **MUST** extend `BaseIntegrationTest` to properly load the module:

```java
public class ImageSomeTest extends BaseIntegrationTest {
    @Test
    public void testSomething() {
        runtime.executeSource("""
            result = ImageNew("src/test/resources/logo.png")
                .crop(50, 50, 150, 100)
                .grayScale();
            ImageWrite(result, "src/test/resources/generated/output.png");
        """, context);
    }
}
```

**Critical Requirements**:
- **ALL tests must extend `BaseIntegrationTest`** - this loads the module from `build/module/`
- Use `runtime.executeSource()` (provided by BaseIntegrationTest), NOT `instance.executeSource()`
- Do NOT re-declare fields like `instance`, `context`, `variables`, or `result` - they're inherited from BaseIntegrationTest
- Must run `./gradlew build -x test` before tests to create module structure

#### BaseIntegrationTest Provides:
- `protected static BoxRuntime runtime` - The BoxLang runtime instance
- `protected static ModuleService moduleService` - Module management service
- `protected static ModuleRecord moduleRecord` - This module's record
- `protected static Key result` - Standard key for test results
- `protected ScriptingRequestBoxContext context` - Execution context (recreated per test via @BeforeEach)
- `protected IScope variables` - Variables scope (recreated per test via @BeforeEach)

#### Classloader Isolation Pattern
BoxLang modules run in a separate classloader from test code. This creates casting restrictions:

**❌ WRONG - ClassCastException:**
```java
runtime.executeSource("""
    result = ImageNew("test.png");
""", context);
BoxImage img = (BoxImage) variables.get(result); // FAILS - different classloaders
int width = img.getWidth();
```

**✅ CORRECT - Extract values within BoxLang:**
```java
runtime.executeSource("""
    result = ImageNew("test.png");
    width = result.getWidth();
    height = result.getHeight();
""", context);
int width = (int) variables.get(Key.of("width"));   // Simple types work
int height = (int) variables.get(Key.of("height"));
```

**Golden Rule**: Execute ALL BoxLang operations inside `runtime.executeSource()`. Only extract primitive types, Strings, or BoxLang core types (Array, Struct) from `variables.get()`. Never cast to module classes like `BoxImage`.

#### Test Resource Directories
- `src/test/resources/test-images/` - Reference images for comparison
- `src/test/resources/generated/` - Output directory for test-generated images (must exist)
- `src/test/resources/libs/` - BoxLang runtime JAR
- Test images often compared byte-for-byte using `Files.readAllBytes()` and `Arrays.equals()`

#### Common Test Patterns

**Testing BIF Functionality:**
```java
@Test
public void testImageCrop() {
    runtime.executeSource("""
        img = ImageNew("src/test/resources/logo.png");
        result = ImageCrop(img, 50, 50, 100, 100);
        width = result.getWidth();
        ImageWrite(result, "src/test/resources/generated/cropped.png");
    """, context);

    int width = (int) variables.get(Key.of("width"));
    assertThat(width).isEqualTo(100);
}
```

**Testing Member Functions:**
```java
@Test
public void testMemberFunction() {
    runtime.executeSource("""
        result = ImageNew("src/test/resources/logo.png")
            .crop(50, 50, 100, 100)
            .blur()
            .grayScale();
        exists = isDefined("result");
    """, context);

    boolean exists = (boolean) variables.get(Key.of("exists"));
    assertThat(exists).isTrue();
}
```

**Testing Exception Handling:**
```java
@Test
public void testInvalidArgument() {
    assertThrows(BoxRuntimeException.class, () -> {
        runtime.executeSource("""
            ImageNew("nonexistent.png");
        """, context);
    });
}
```

**Testing Component Usage:**
```java
@Test
public void testImageComponent() {
    runtime.executeSource("""
        <bx:image action="read" source="src/test/resources/logo.png" name="img" />
        <bx:image action="crop" source="#img#" x="50" y="50" width="100" height="100" />
        <bx:image action="write" source="#img#" destination="src/test/resources/generated/out.png" />
    """, context, BoxSourceType.BOXTEMPLATE);
    // Verify file exists or other assertions
}
```

#### Platform-Specific Test Considerations
Some tests may fail due to platform differences (especially font rendering):
- Use `@DisabledOnOs(OS.LINUX)` or similar annotations for platform-specific tests
- `ImageDrawTextTest` has known font rendering variations across platforms
- Byte-perfect image comparisons are fragile across different environments/JDK versions

#### Test Failure Debugging
1. Check `build/reports/tests/test/index.html` for detailed test reports
2. Verify `build/module/` exists and contains the module
3. Check `src/test/resources/generated/` for actual output images
4. Compare generated images visually with reference images in `test-images/`
5. For ClassCastException: Ensure not casting module classes in test code

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

1. **Tests not extending BaseIntegrationTest**: All tests MUST extend `BaseIntegrationTest` to load the module. Without it, tests fail with `BoxRuntimeException` because BIFs aren't registered.
2. **Re-declaring inherited fields in tests**: Don't redeclare `instance`, `context`, `variables`, or `result` - they're inherited from `BaseIntegrationTest`. This causes NullPointerException or uses wrong instances.
3. **Using `instance` instead of `runtime`**: Tests should use `runtime.executeSource()`, not `instance.executeSource()`. The inherited field is named `runtime`.
4. **Casting module classes in tests**: Never cast `variables.get()` to `BoxImage` or other module classes due to classloader isolation. Extract values as BoxLang primitives instead.
5. **Missing generated directory**: Tests write to `src/test/resources/generated/` which must exist. Create it with `mkdir -p src/test/resources/generated/`.
6. **Forgetting to build module before testing**: Tests load from `build/module/`, not `src/`. Run `./gradlew build -x test` first.
7. **Not returning BoxImage from BIFs**: Breaks method chaining (e.g., `image.crop().blur()`)
8. **Hardcoding argument names**: Always use `ImageKeys.*` constants
9. **Missing @BoxMember**: BIF works but member function doesn't (e.g., `image.method()` fails)
10. **Version mismatches**: Ensure all 3 version locations are updated together
11. **BoxLang runtime dependency**: Must match version in `gradle.properties` and be present in `src/test/resources/libs/`

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
