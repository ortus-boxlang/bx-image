package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;

public class ImageReadAndDeleteTest extends BaseIntegrationTest {

	private Path	tempImagePath;
	private Path	sourceImagePath	= Paths.get( "src/test/resources/logo.png" );

	@BeforeEach
	public void setupTempImage() throws IOException {
		// Create a temporary copy of the test image that we can safely delete
		tempImagePath = Paths.get( "src/test/resources/generated/temp_test_image.png" );
		Files.copy( sourceImagePath, tempImagePath, StandardCopyOption.REPLACE_EXISTING );
	}

	@AfterEach
	public void cleanupTempImage() throws IOException {
		// Clean up in case the test fails and doesn't delete the file
		if ( Files.exists( tempImagePath ) ) {
			Files.delete( tempImagePath );
		}
	}

	@Test
	@DisplayName( "Can read an image and then delete the file" )
	public void testReadAndDelete() {
		runtime.executeSource(
		    """
		    img = ImageRead("src/test/resources/generated/temp_test_image.png");
		    width = img.getWidth();
		    height = img.getHeight();
		    		       // Now attempt to delete the file - this should not fail
		    fileDelete("src/test/resources/generated/temp_test_image.png");
		    		       fileStillExists = fileExists("src/test/resources/generated/temp_test_image.png");
		    """,
		    context
		);

		int		width			= ( int ) variables.get( Key.of( "width" ) );
		int		height			= ( int ) variables.get( Key.of( "height" ) );
		boolean	fileStillExists	= ( boolean ) variables.get( Key.of( "fileStillExists" ) );

		// Verify the image was read correctly
		assertThat( width ).isGreaterThan( 0 );
		assertThat( height ).isGreaterThan( 0 );

		// Verify the file was successfully deleted
		assertThat( fileStillExists ).isFalse();
	}

	@Test
	@DisplayName( "Can read an image with ImageNew and then delete the file" )
	public void testImageNewAndDelete() {
		runtime.executeSource(
		    """
		    img = ImageNew("src/test/resources/generated/temp_test_image.png");
		    info = img.info();
		    		       // Now attempt to delete the file
		    fileDelete("src/test/resources/generated/temp_test_image.png");
		    		       fileStillExists = fileExists("src/test/resources/generated/temp_test_image.png");
		    """,
		    context
		);

		boolean fileStillExists = ( boolean ) variables.get( Key.of( "fileStillExists" ) );

		// Verify the file was successfully deleted
		assertThat( fileStillExists ).isFalse();
	}

	@Test
	@DisplayName( "Can read, manipulate, and then delete the file" )
	public void testReadManipulateAndDelete() {
		runtime.executeSource(
		    """
		    img = ImageRead("src/test/resources/generated/temp_test_image.png");
		    		       // Perform some operations on the image
		    img.resize(100, 100);
		    img.grayScale();
		    		       // Get dimensions after manipulation
		    width = img.getWidth();
		    		       // Now attempt to delete the original file
		    fileDelete("src/test/resources/generated/temp_test_image.png");
		    		       fileStillExists = fileExists("src/test/resources/generated/temp_test_image.png");
		    """,
		    context
		);

		int		width			= ( int ) variables.get( Key.of( "width" ) );
		boolean	fileStillExists	= ( boolean ) variables.get( Key.of( "fileStillExists" ) );

		assertThat( width ).isEqualTo( 100 );
		assertThat( fileStillExists ).isFalse();
	}

}
