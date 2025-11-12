package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageDrawArcTest extends BaseIntegrationTest {

	@DisplayName( "It should draw an arc" )
	@Test
	public void testDrawArc() throws IOException {
		runtime.executeSource( """
		                                                           result = ImageRead( "src/test/resources/logo.png" );
		                                         result.setDrawingColor( "yellow" );
		                       ImageDrawArc( result, 150, 50, 100, 100, 45, 70 );
		                                         ImageWrite( result, "src/test/resources/generated/logo-draw-arc.png" );
		                                                           """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-draw-arc.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-draw-arc.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw a filled arc" )
	@Test
	public void testDrawFilledArc() throws IOException {
		runtime.executeSource( """
		                                                           result = ImageRead( "src/test/resources/logo.png" );
		                                         result.setDrawingColor( "yellow" );
		                       ImageDrawArc( result, 150, 50, 100, 100, 45, 70, true );
		                                         ImageWrite( result, "src/test/resources/generated/logo-draw-arc-filled.png" );
		                                                           """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-draw-arc-filled.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-draw-arc-filled.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should allow member function invocation" )
	@Test
	public void testDrawArcMemberInvocation() throws IOException {
		runtime.executeSource( """
		                                                           result = ImageRead( "src/test/resources/logo.png" );
		                                         result.setDrawingColor( "yellow" );
		                       result.drawArc( 150, 50, 100, 100, 45, 70, true );
		                                         ImageWrite( result, "src/test/resources/generated/logo-draw-arc-filled.png" );
		                                                           """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-draw-arc-filled.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-draw-arc-filled.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
