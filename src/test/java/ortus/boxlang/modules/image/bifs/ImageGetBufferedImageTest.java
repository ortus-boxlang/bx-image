package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.awt.image.BufferedImage;
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

public class ImageGetBufferedImageTest {

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

	@DisplayName( "It should an instance of BufferedImage" )
	@Test
	public void testReturnsAnArray() throws IOException {
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        result = ImageGetBufferedImage( result );
		                                          """, context );

		assertThat( variables.get( result ) ).isInstanceOf( BufferedImage.class );
	}

	@DisplayName( "It should be callable as a member function" )
	@Test
	public void testMemberInvocation() throws IOException {
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        result = result.getBufferedImage();
		                                          """, context );

		assertThat( variables.get( result ) ).isInstanceOf( BufferedImage.class );
	}

}
