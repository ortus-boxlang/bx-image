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

	@DisplayName( "It should resize an image with a single size parameter to fit in a square" )
	@Test
	public void testScaleToFitSingleParameter() throws IOException {
		String fileName = "logo-scale-to-fit-250.png";
		// @formatter:off
		runtime.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			result.scaleToFit( 250 );
			width = result.getWidth();
			height = result.getHeight();
			ImageWrite( result, "src/test/resources/generated/%s" );
		""".formatted( fileName ), context );
		// @formatter:on

		int	width	= ( int ) variables.get( Key.of( "width" ) );
		int	height	= ( int ) variables.get( Key.of( "height" ) );

		// The logo.png is 256x256, so scaling to fit 250 should give 250x250
		assertThat( width ).isEqualTo( 250 );
		assertThat( height ).isEqualTo( 250 );
	}

	@DisplayName( "It should resize a non-square image with single parameter maintaining aspect ratio" )
	@Test
	public void testScaleToFitSingleParameterNonSquare() throws IOException {
		// @formatter:off
		runtime.executeSource( """
			// Create a non-square image (400x200)
			result = ImageNew( "", 400, 200, "rgb", "white" );
			result.scaleToFit( 100 );
			width = result.getWidth();
			height = result.getHeight();
		""", context );
		// @formatter:on

		int	width	= ( int ) variables.get( Key.of( "width" ) );
		int	height	= ( int ) variables.get( Key.of( "height" ) );

		// Image is 400x200 (2:1 ratio), scaling to fit width 100 should give 100x50
		assertThat( width ).isEqualTo( 100 );
		assertThat( height ).isEqualTo( 50 );
	}

	@DisplayName( "It should resize an image with two size parameters to fit within rectangular bounds" )
	@Test
	public void testScaleToFitTwoParameters() throws IOException {
		// @formatter:off
		runtime.executeSource( """
			// Create a non-square image (400x200)
			result = ImageNew( "", 400, 200, "rgb", "blue" );
			result.scaleToFit( 100, 100 );
			width = result.getWidth();
			height = result.getHeight();
		""", context );
		// @formatter:on

		int	width	= ( int ) variables.get( Key.of( "width" ) );
		int	height	= ( int ) variables.get( Key.of( "height" ) );

		// Image is 400x200, fitting within 100x100 maintains aspect ratio: 100x50
		assertThat( width ).isEqualTo( 100 );
		assertThat( height ).isEqualTo( 50 );
	}

}
