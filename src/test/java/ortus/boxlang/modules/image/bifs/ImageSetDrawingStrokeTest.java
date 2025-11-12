package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageSetDrawingStrokeTest extends BaseIntegrationTest {
	// test the rest of the configs

	@DisplayName( "It should a thick line" )
	@Test
	public void testDrawThickLine() throws IOException {
		String fileName = "logo-draw-drawing-stroke-width.png";
		runtime.executeSource( """
		                                                                    result = ImageRead( "src/test/resources/logo.png" );
		                       result.setAntiAliasing( true );
		                                                  ImageSetDrawingStroke( result, {width: 5} );
		                                result.drawLine( 0, 0, 250, 250 );
		                                                  ImageWrite( result, "src/test/resources/generated/%s" );
		                                                  //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                                    """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
