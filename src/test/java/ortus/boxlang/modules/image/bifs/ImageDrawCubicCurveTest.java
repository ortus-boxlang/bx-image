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

public class ImageDrawCubicCurveTest extends BaseIntegrationTest {

	@DisplayName( "It should draw a cubic curve" )
	@Test
	public void testDrawCubicCurve() throws IOException {
		runtime.executeSource( """
		                                                     result = ImageRead( "src/test/resources/logo.png" );
		                       imageSetDrawingColor(result,"magenta")
		                       imageDrawCubicCurve(result,0,100,256,100,0,0,256,256)
		                                   ImageWrite( result, "src/test/resources/generated/logo-cubic-curve.png" );
		                                                     """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-cubic-curve.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-cubic-curve.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should let you set the drawing color as a member function" )
	@Test
	public void testSetDrawingColorMember() throws IOException {
		runtime.executeSource( """
		                                                     result = ImageRead( "src/test/resources/logo.png" );
		                       imageSetDrawingColor(result,"magenta")
		                       result.drawCubicCurve(0,100,256,100,0,0,256,256)
		                                   ImageWrite( result, "src/test/resources/generated/logo-cubic-curve.png" );
		                                                     """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-cubic-curve.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-cubic-curve.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
