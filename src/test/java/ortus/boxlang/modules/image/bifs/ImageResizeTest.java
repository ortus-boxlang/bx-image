package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageResizeTest extends BaseIntegrationTest {

	@DisplayName( "It should resize an image" )
	@Test
	public void testResize() throws IOException {
		String fileName = "logo-draw-resize.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       imageResize( result, 512, 512 );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should resize an image via member function" )
	@Test
	public void testResizeMemberFunc() throws IOException {
		String fileName = "logo-draw-resize.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       result.resize( 512, 512 );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "A blank or 0 dimension keeps the aspect ratio" )
	@Test
	public void testResizeKeepsAspectRatio() {
		runtime.executeSource( """
		                       wide = imageNew( "", 40, 20, "rgb", "black" );
		                       imageResize( wide, 10, "" );
		                       tall = imageNew( "", 40, 20, "rgb", "black" ).resize( 0, 5 );
		                       result = [ wide.getWidth(), wide.getHeight(), tall.getWidth(), tall.getHeight() ];
		                       """, context );

		assertThat( variables.getAsArray( result ).toArray() ).asList().containsExactly( 10, 5, 10, 5 ).inOrder();
	}

	@DisplayName( "Resizing with neither a width nor a height fails" )
	@Test
	public void testResizeNeedsADimension() {
		org.junit.jupiter.api.Assertions.assertThrows( Exception.class, () -> runtime.executeSource( """
		                                                                                             img = imageNew( "", 4, 4, "rgb", "black" );
		                                                                                             imageResize( img, "", 0 );
		                                                                                             """, context ) );
	}

}
