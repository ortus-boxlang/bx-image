package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

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

public class ImageScaleToFitTest {

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

	@DisplayName( "It should resize an image and keep the aspect ratio" )
	@Test
	public void testScaleToFit() throws IOException {
		// @formatter:off
		instance.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			ImageScaleToFit( result, 100, "" );
			width = result.getWidth();
			height = result.getHeight();
		""", context );
		// @formatter:on

		assertThat( variables.get( "width" ) ).isEqualTo( 100 );
		assertThat( variables.get( "height" ) ).isEqualTo( 100 );
	}

	@DisplayName( "It should resize an image when called as a member function" )
	@Test
	public void testScaleToFitMemberInvocation() throws IOException {
		// @formatter:off
		instance.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			result.scaleToFit( 100, "" );
			imageWrite( result,"src/test/resources/testimage.png")
			width = result.getWidth();
			height = result.getHeight();
		""", context );
		// @formatter:on

		assertThat( variables.get( "width" ) ).isEqualTo( 100 );
		assertThat( variables.get( "height" ) ).isEqualTo( 100 );
	}

}
