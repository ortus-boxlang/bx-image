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

public class ImageShearDrawingAxisTest {

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

	@DisplayName( "It should shear drawing calls" )
	@Test
	public void testShearHorizontally() throws IOException {
		String fileName = "logo-draw-shear-drawing-axis.png";
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          ImageShearDrawingAxis( result, 0.5, 0 );
		                                          ImageDrawRect( result, 50, 50, 100, 100, true );
		                        ImageWrite( result, "src/test/resources/%s" );
		                                        //   ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                            """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should shear drawing calls vertically" )
	@Test
	public void testShearVertically() throws IOException {
		String fileName = "logo-draw-shear-drawing-axis-vertically.png";
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          ImageShearDrawingAxis( result, 0, 0.5 );
		                                          ImageDrawRect( result, 50, 50, 100, 100, true );
		                        ImageWrite( result, "src/test/resources/%s" );
		                                        //   ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                            """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should shear drawing calls as a member function" )
	@Test
	public void testShearMember() throws IOException {
		String fileName = "logo-draw-shear-drawing-axis-vertically.png";
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          result.shearDrawingAxis( 0, 0.5 );
		                                          ImageDrawRect( result, 50, 50, 100, 100, true );
		                        ImageWrite( result, "src/test/resources/%s" );
		                                          //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                            """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}
}
