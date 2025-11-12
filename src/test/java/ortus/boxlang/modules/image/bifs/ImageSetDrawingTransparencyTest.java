package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BaseIntegrationTest;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.context.ScriptingRequestBoxContext;
import ortus.boxlang.runtime.scopes.IScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.scopes.VariablesScope;

public class ImageSetDrawingTransparencyTest extends BaseIntegrationTest {

	@DisplayName( "It should draw with transparency" )
	@Test
	public void testDrawTransparency() throws IOException {
		String fileName = "logo-draw-transparency.png";
		runtime.executeSource( """
		                                                                             result = ImageRead( "src/test/resources/logo.png" );
		                                               ImageSetDrawingTransparency( result, 100 );
		                                                           ImageDrawOval( result, 50, 50, 200, 100, true );

		                                ImageSetDrawingTransparency( result, 50 );
		                                                           ImageDrawOval( result, 50, 150, 200, 100, true );

		                       ImageSetDrawingTransparency( result, 25 );
		                                                           ImageDrawOval( result, 50, 225, 200, 100, true );
		                                                           ImageWrite( result, "src/test/resources/generated/%s" );
		                                                           //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                                             """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should be usable as a member function" )
	@Test
	public void testDrawTransparencyMember() throws IOException {
		String fileName = "logo-draw-transparency.png";
		runtime.executeSource( """
		                                                                             result = ImageRead( "src/test/resources/logo.png" );
		                                               result.setDrawingTransparency( 100 );
		                                                           ImageDrawOval( result, 50, 50, 200, 100, true );

		                                result.setDrawingTransparency( 50 );
		                                                           ImageDrawOval( result, 50, 150, 200, 100, true );

		                       result.setDrawingTransparency( 25 );
		                                                           ImageDrawOval( result, 50, 225, 200, 100, true );
		                                                           ImageWrite( result, "src/test/resources/generated/%s" );
		                                                           //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                                             """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
