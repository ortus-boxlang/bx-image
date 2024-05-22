package ortus.boxlang.modules.image.bifs;

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

public class ImageDrawCubicCurveTest {

	static BoxRuntime	instance;
	IBoxContext			context;
	IScope				variables;
	static Key			result	= new Key( "result" );

	@BeforeAll
	public static void setUp() {
		instance = BoxRuntime.getInstance( true );
	}

	@BeforeEach
	public void setupEach() {
		context		= new ScriptingRequestBoxContext( instance.getRuntimeContext() );
		variables	= context.getScopeNearby( VariablesScope.name );
	}

	@DisplayName( "It should draw a cubic curve" )
	@Test
	public void testDrawCubicCurve() throws IOException {
		instance.executeSource( """
		                                                      result = ImageRead( "src/test/resources/logo.png" );
		                        imageSetDrawingColor(result,"magenta")
		                        imageDrawCubicCurve(result,0,100,256,100,0,0,256,256)
		                                    ImageWrite( result, "src/test/resources/logo-cubic-curve.png" );
		                                                      """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/logo-cubic-curve.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-cubic-curve.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should let you set the drawing color as a member function" )
	@Test
	public void testSetDrawingColorMember() throws IOException {
		instance.executeSource( """
		                                                      result = ImageRead( "src/test/resources/logo.png" );
		                        imageSetDrawingColor(result,"magenta")
		                        result.drawCubicCurve(0,100,256,100,0,0,256,256)
		                                    ImageWrite( result, "src/test/resources/logo-cubic-curve.png" );
		                                                      """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/logo-cubic-curve.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-cubic-curve.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
