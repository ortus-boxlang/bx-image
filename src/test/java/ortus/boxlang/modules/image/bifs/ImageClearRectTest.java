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

public class ImageClearRectTest {

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

	// TODO tests for color by name green/white/black/red etc
	// TODO tests for hexadecimal
	// TODO tests for rgb values

	@DisplayName( "It should default to white" )
	@Test
	public void testDefaultDrawingColor() throws IOException {
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageClearRect( result, 0, 0, 50, 50 );
		                        ImageWrite( result, "src/test/resources/generated/logo-clear-rect.png" );
		                                          """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-clear-rect.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-clear-rect.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should let you set the drawing color" )
	@Test
	public void testSetDrawingColor() throws IOException {
		instance.executeSource( """
		                                                      result = ImageRead( "src/test/resources/logo.png" );
		                        ImageSetBackgroundColor( result, "green" );
		                                    ImageClearRect( result, 0, 0, 50, 50 );
		                                    ImageWrite( result, "src/test/resources/generated/logo-clear-rect-green.png" );
		                                                      """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-clear-rect-green.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-clear-rect-green.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();

	}

	@DisplayName( "It should let you set the drawing color of an image by name" )
	@Test
	public void testSetDrawingColorByName() throws IOException {
		instance.executeSource( """
		                                                                                    result = ImageRead( "src/test/resources/logo.png" );
		                                       ImageSetBackgroundColor( "result", "green" );
		                                                ImageClearRect( result, 0, 0, 50, 50 );
		                        ImageWrite( result, "src/test/resources/generated/logo-clear-rect-green.png" );
		                                                                                    """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-clear-rect-green.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-clear-rect-green.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should let you set the drawing color as a member function" )
	@Test
	public void testSetDrawingColorMember() throws IOException {
		instance.executeSource( """
		                                                                                    result = ImageRead( "src/test/resources/logo.png" );
		                                       ImageSetBackgroundColor( "result", "green" );
		                                                result.clearRect( 0, 0, 50, 50 );
		                        ImageWrite( result, "src/test/resources/generated/logo-clear-rect-green.png" );
		                                                                                    """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-clear-rect-green.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-clear-rect-green.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
