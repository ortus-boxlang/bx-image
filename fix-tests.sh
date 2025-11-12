#!/bin/bash

# Script to fix all BIF tests to extend BaseIntegrationTest

cd /Users/lmajano/Sites/projects/boxlang-modules/bx-image

for file in src/test/java/ortus/boxlang/modules/image/bifs/*Test.java; do
    echo "Processing $file..."

    # Check if file already extends BaseIntegrationTest
    if grep -q "extends BaseIntegrationTest" "$file"; then
        echo "  Already extends BaseIntegrationTest, skipping"
        continue
    fi

    # Add import for BaseIntegrationTest if not present
    if ! grep -q "import ortus.boxlang.modules.image.BaseIntegrationTest" "$file"; then
        sed -i '' '/^package ortus.boxlang.modules.image.bifs;$/a\
\
import ortus.boxlang.modules.image.BaseIntegrationTest;\
' "$file"
    fi

    # Change class declaration to extend BaseIntegrationTest
    sed -i '' 's/^public class \(.*\) {$/public class \1 extends BaseIntegrationTest {/' "$file"

    # Remove static BoxRuntime instance field if present
    sed -i '' '/static BoxRuntime[[:space:]]*instance;/d' "$file"

    # Remove IBoxContext context field if present
    sed -i '' '/IBoxContext[[:space:]]*context;/d' "$file"

    # Remove IScope variables field if present
    sed -i '' '/IScope[[:space:]]*variables;/d' "$file"

    # Remove static Key result field if present
    sed -i '' '/static Key[[:space:]]*result[[:space:]]*=/d' "$file"

    # Remove @BeforeAll setUp method
    sed -i '' '/@BeforeAll/,/^[[:space:]]*}$/d' "$file"

    # Remove @BeforeEach setupEach method
    sed -i '' '/@BeforeEach/,/^[[:space:]]*}$/d' "$file"

    # Change instance.executeSource to runtime.executeSource
    sed -i '' 's/instance\.executeSource/runtime.executeSource/g' "$file"

    echo "  Fixed!"
done

echo "Done processing all files"
