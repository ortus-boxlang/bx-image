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

public class ImageOverlayTest extends BaseIntegrationTest {
	// TODO tests for SRC
	// TODO tests for DST_IN
	// TODO tests for DST_OUT
	// TODO tests for DST_OVER
	// TODO tests for SRC_IN
	// TODO tests for SRC_OVER
	// TODO tests for SRC_OUT

	@DisplayName( "It should draw an overlay" )
	@Test
	public void testDrawOverlay() throws IOException {
		String fileName = "logo-draw-overlay.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                                         overlay = ImageRead( "src/test/resources/test-images/overlay.png" );
		                       ImageOverlay( result, overlay );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should be callable as a member function" )
	@Test
	public void testDrawOverlayMember() throws IOException {
		String fileName = "logo-draw-overlay.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                                         overlay = ImageRead( "src/test/resources/test-images/overlay.png" );
		                       result.overlay( overlay );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}
}
