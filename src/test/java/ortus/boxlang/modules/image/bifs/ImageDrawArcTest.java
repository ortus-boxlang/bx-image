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

public class ImageDrawArcTest {

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

	@DisplayName( "It should draw an arc" )
	@Test
	public void testDrawArc() throws IOException {
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          result.setDrawingColor( "yellow" );
		                        ImageDrawArc( result, 150, 50, 100, 100, 45, 70 );
		                                          ImageWrite( result, "src/test/resources/logo-draw-arc.png" );
		                                                            """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/logo-draw-arc.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-draw-arc.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw a filled arc" )
	@Test
	public void testDrawFilledArc() throws IOException {
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          result.setDrawingColor( "yellow" );
		                        ImageDrawArc( result, 150, 50, 100, 100, 45, 70, true );
		                                          ImageWrite( result, "src/test/resources/logo-draw-arc-filled.png" );
		                                                            """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/logo-draw-arc-filled.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-draw-arc-filled.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should allow member function invocation" )
	@Test
	public void testDrawArcMemberInvocation() throws IOException {
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          result.setDrawingColor( "yellow" );
		                        result.drawArc( 150, 50, 100, 100, 45, 70, true );
		                                          ImageWrite( result, "src/test/resources/logo-draw-arc-filled.png" );
		                                                            """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/logo-draw-arc-filled.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-draw-arc-filled.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
