package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class ImageWriteTest extends BaseIntegrationTest {

	@DisplayName( "It should write an image back to its original source path" )
	@Test
	public void testWriteToOriginalPath() throws IOException {
		// Copy logo.png to generated folder so we can modify it
		String testFile = "src/test/resources/generated/test-write-original.png";
		Files.copy(
		    Paths.get( "src/test/resources/logo.png" ),
		    Paths.get( testFile ),
		    java.nio.file.StandardCopyOption.REPLACE_EXISTING
		);

		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "%s" );
			img.scaleToFit( 128 );
			img.write();
			// Verify the file was written
			reloaded = ImageRead( "%s" );
			width = reloaded.getWidth();
		""".formatted( testFile, testFile ), context );
		// @formatter:on

		int width = ( int ) variables.get( Key.of( "width" ) );
		// Image should have been scaled to 128x128
		assertThat( width ).isEqualTo( 128 );
	}

	@DisplayName( "It should write an image to a specified path" )
	@Test
	public void testWriteToSpecifiedPath() throws IOException {
		String outputFile = "src/test/resources/generated/test-write-specified.png";

		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "src/test/resources/logo.png" );
			img.scaleToFit( 64 );
			img.write( "%s" );
			// Verify the file was written
			reloaded = ImageRead( "%s" );
			width = reloaded.getWidth();
		""".formatted( outputFile, outputFile ), context );
		// @formatter:on

		int width = ( int ) variables.get( Key.of( "width" ) );
		assertThat( width ).isEqualTo( 64 );
	}

	@DisplayName( "It should throw an exception when writing an image with no source path" )
	@Test
	public void testWriteWithoutSourcePath() {
		assertThrows( BoxRuntimeException.class, () -> {
			runtime.executeSource( """
			                       	img = ImageNew( "", 100, 100, "rgb", "white" );
			                       	img.write();
			                       """, context );
		} );
	}

	@DisplayName( "It should write an image using the ImageWrite BIF with no path" )
	@Test
	public void testImageWriteBIFNoPath() throws IOException {
		// Copy logo.png to generated folder so we can modify it
		String testFile = "src/test/resources/generated/test-write-bif.png";
		Files.copy(
		    Paths.get( "src/test/resources/logo.png" ),
		    Paths.get( testFile ),
		    java.nio.file.StandardCopyOption.REPLACE_EXISTING
		);

		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "%s" );
			ImageScaleToFit( img, 200 );
			ImageWrite( img );
			// Verify the file was written
			reloaded = ImageRead( "%s" );
			width = reloaded.getWidth();
		""".formatted( testFile, testFile ), context );
		// @formatter:on

		int width = ( int ) variables.get( Key.of( "width" ) );
		assertThat( width ).isEqualTo( 200 );
	}
}
