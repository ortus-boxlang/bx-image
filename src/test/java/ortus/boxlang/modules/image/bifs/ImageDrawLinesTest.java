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

public class ImageDrawLinesTest {

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

	@DisplayName( "It should draw an unclosed polygon" )
	@Test
	public void testDrawLine() throws IOException {
		String fileName = "logo-draw-lines-1.png";
		instance.executeSource( """
		                                                       result = ImageRead( "src/test/resources/logo.png" );
		                         x = [];
		                         y = [];
		                         x[1] = "100";
		                        x[2] = "50";
		                        x[3] = "200";
		                        x[4] = "100";
		                        y[1] = "50";
		                        y[2] = "100";
		                        y[3] = "100";
		                        y[4] = "200";
		                                     ImageDrawLines( result, x, y );
		                                     ImageWrite( result, "src/test/resources/%s" );
		                                     //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                       """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw a closed polygon" )
	@Test
	public void testDrawClosedPolygon() throws IOException {
		String fileName = "logo-draw-lines-closed.png";
		instance.executeSource( """
		                                                       result = ImageRead( "src/test/resources/logo.png" );
		                         x = [];
		                         y = [];
		                         x[1] = "100";
		                        x[2] = "50";
		                        x[3] = "200";
		                        x[4] = "100";
		                        y[1] = "50";
		                        y[2] = "100";
		                        y[3] = "100";
		                        y[4] = "200";
		                                     ImageDrawLines( result, x, y, true );
		                                     ImageWrite( result, "src/test/resources/%s" );
		                                     //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                       """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw a closed filled polygon" )
	@Test
	public void testDrawClosedFilledPolygon() throws IOException {
		String fileName = "logo-draw-lines-closed-filled.png";
		instance.executeSource( """
		                                                       result = ImageRead( "src/test/resources/logo.png" );
		                         x = [];
		                         y = [];
		                         x[1] = "100";
		                        x[2] = "50";
		                        x[3] = "200";
		                        x[4] = "100";
		                        y[1] = "50";
		                        y[2] = "100";
		                        y[3] = "100";
		                        y[4] = "200";
		                                     ImageDrawLines( result, x, y, true, true );
		                                     ImageWrite( result, "src/test/resources/%s" );
		                                     //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                       """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should be callable as a member function" )
	@Test
	public void testMemberFunction() throws IOException {
		String fileName = "logo-draw-lines-closed-filled.png";
		instance.executeSource( """
		                                                       result = ImageRead( "src/test/resources/logo.png" );
		                         x = [];
		                         y = [];
		                         x[1] = "100";
		                        x[2] = "50";
		                        x[3] = "200";
		                        x[4] = "100";
		                        y[1] = "50";
		                        y[2] = "100";
		                        y[3] = "100";
		                        y[4] = "200";
		                                     result.drawLines( x, y, true, true );
		                                     ImageWrite( result, "src/test/resources/%s" );
		                                     //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                                       """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}
}
