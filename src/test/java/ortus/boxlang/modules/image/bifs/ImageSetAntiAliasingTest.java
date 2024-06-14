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

public class ImageSetAntiAliasingTest {

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

	@DisplayName( "It should turn antialiasing off" )
	@Test
	public void testAAOff() throws IOException {
		String fileName = "logo-draw-aa-off.png";
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          ImageSetAntiAliasing( result, false );
		                        result.drawLine( 0, 0, 250, 250 );
		                                          ImageWrite( result, "src/test/resources/%s" );
		                                          //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                            """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should turn antialiasing on" )
	@Test
	public void testAAOn() throws IOException {
		String fileName = "logo-draw-aa-on.png";
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          ImageSetAntiAliasing( result, true );
		                        result.drawLine( 0, 0, 250, 250 );
		                                          ImageWrite( result, "src/test/resources/%s" );
		                                          //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                            """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should turn antialiasing on as a member function" )
	@Test
	public void testAAOnMember() throws IOException {
		String fileName = "logo-draw-aa-on.png";
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          result.setAntiAliasing( true );
		                        result.drawLine( 0, 0, 250, 250 );
		                                          ImageWrite( result, "src/test/resources/%s" );
		                                          //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                            """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
