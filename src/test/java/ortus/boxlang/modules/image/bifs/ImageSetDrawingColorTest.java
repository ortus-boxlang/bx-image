package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

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

public class ImageSetDrawingColorTest {

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
	public void testDefaultDrawingColor() {
		// @formatter:off
		instance.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" ).getDrawingColor();
		""", context );
		// @formatter:on

		assertThat( variables.get( result ) ).isEqualTo( "white" );
	}

	@DisplayName( "It should let you set the drawing color" )
	@Test
	public void testSetDrawingColor() {
		// @formatter:off
		instance.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			ImageSetDrawingColor( result, "green" );
			result = result.getDrawingColor();
		""", context );
		// @formatter:on

		assertThat( variables.get( result ) ).isEqualTo( "green" );
	}

	@DisplayName( "It should let you set the drawing color of an image by name" )
	@Test
	public void testSetDrawingColorByName() {
		// @formatter:off
		instance.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			ImageSetDrawingColor( "result", "green" );
			result = result.getDrawingColor();
		""", context );
		// @formatter:on

		assertThat( variables.get( result ) ).isEqualTo( "green" );
	}

	// TODO test member functionality
	@DisplayName( "It should let you set the drawing color as a member function" )
	@Test
	public void testSetDrawingColorMember() {
		// @formatter:off
		instance.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			result.setDrawingColor( "green" );
			result = result.getDrawingColor();
		""", context );
		// @formatter:on

		assertThat( variables.get( result ) ).isEqualTo( "green" );
	}

}
