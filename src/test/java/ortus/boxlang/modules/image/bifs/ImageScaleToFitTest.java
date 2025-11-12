package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;

public class ImageScaleToFitTest extends BaseIntegrationTest {

	@DisplayName( "It should resize an image and keep the aspect ratio" )
	@Test
	public void testScaleToFit() throws IOException {
		String fileName = "logo-scale-to-fit.png";
		// @formatter:off
		runtime.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			ImageScaleToFit( result, 100, "" );
			width = result.getWidth();
			height = result.getHeight();
			ImageWrite( result, "src/test/resources/generated/%s" );
		""".formatted( fileName, fileName ), context );
		// @formatter:on

		assertThat( variables.get( "width" ) ).isEqualTo( 100 );
		assertThat( variables.get( "height" ) ).isEqualTo( 100 );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should resize an image when called as a member function" )
	@Test
	public void testScaleToFitMemberInvocation() throws IOException {
		String fileName = "logo-scale-to-fit.png";
		// @formatter:off
		runtime.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			result.scaleToFIt( 100, "" );
			width = result.getWidth();
			height = result.getHeight();
			ImageWrite( result, "src/test/resources/generated/%s" );
			// ImageWrite( result, "src/test/resources/test-images/%s" )
		""".formatted( fileName, fileName ), context );
		// @formatter:on

		int	width	= ( int ) variables.get( Key.of( "width" ) );
		int	height	= ( int ) variables.get( Key.of( "height" ) );

		assertThat( width ).isEqualTo( 100 );
		assertThat( height ).isEqualTo( 100 );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
