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

public class IsImageTest {

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

	@DisplayName( "It should return true for an image" )
	@Test
	public void testTrueIfImage() {
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        result = IsImage( result );
		                                          """, context );

		assertThat( variables.get( result ) ).isEqualTo( true );
	}

	@DisplayName( "It should return false for anything other than an image" )
	@Test
	public void testFalseIfNotImage() {
		instance.executeSource( """
		                        result = IsImage( "test" );
		                                          """, context );

		assertThat( variables.get( result ) ).isEqualTo( false );
	}

}
