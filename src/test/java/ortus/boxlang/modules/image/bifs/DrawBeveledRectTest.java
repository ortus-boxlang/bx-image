package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class DrawBeveledRectTest extends BaseIntegrationTest {

	@DisplayName( "It should draw a beveled rect" )
	@Test
	public void testDrawBeveledRect() throws IOException {
		runtime.executeSource( """
		                                                     result = ImageRead( "src/test/resources/logo.png" );
		                       result.setDrawingColor( "orange") ;
		                                   ImageDrawBeveledRect( result, 50, 50, 100, 100, true );
		                                   ImageWrite( result, "src/test/resources/generated/logo-beveled-rect.png" );
		                                                     """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-beveled-rect.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-beveled-rect.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw a depressed beveled rect" )
	@Test
	public void testDrawDepressedBeveledRect() throws IOException {
		runtime.executeSource( """
		                                                     result = ImageRead( "src/test/resources/logo.png" );
		                       result.setDrawingColor( "orange") ;
		                                   ImageDrawBeveledRect( result, 50, 50, 100, 100, false );
		                                   ImageWrite( result, "src/test/resources/generated/logo-beveled-rect-depressed.png" );
		                                                     """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-beveled-rect-depressed.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-beveled-rect-depressed.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw a filled rect" )
	@Test
	public void testDrawFilledBeveledRect() throws IOException {
		runtime.executeSource( """
		                                                     result = ImageRead( "src/test/resources/logo.png" );
		                       result.setDrawingColor( "orange") ;
		                                   ImageDrawBeveledRect( result, 50, 50, 100, 100, true, true  );
		                                   ImageWrite( result, "src/test/resources/generated/logo-beveled-rect-filled.png" );
		                                                     """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-beveled-rect-filled.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-beveled-rect-filled.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should be able to be called as a member function" )
	@Test
	public void testDrawBeveledRectMemberInvocation() throws IOException {
		runtime.executeSource( """
		                                                     result = ImageRead( "src/test/resources/logo.png" );
		                       result.setDrawingColor( "orange") ;
		                                   result.drawBeveledRect( 50, 50, 100, 100, true, true  );
		                                   ImageWrite( result, "src/test/resources/generated/logo-beveled-rect-filled.png" );
		                                                     """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-beveled-rect-filled.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-beveled-rect-filled.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
