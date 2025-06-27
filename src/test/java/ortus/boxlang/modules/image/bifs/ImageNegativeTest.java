package ortus.boxlang.modules.image.bifs;

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

public class ImageNegativeTest {

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

	@DisplayName( "It can make a negative image" )
	@Test
	public void testNegative() {
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          ImageNegative( result );
		                        ImageWrite( result, "src/test/resources/generated/logo-negative.png" );
		                                                            """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can be called as a member method" )
	@Test
	public void testDrawRectFilledMemberInvocation() {
		instance.executeSource( """
		                                                                           result = ImageRead( "src/test/resources/logo.png" );
		                        result.setDrawingColor( "magenta" );
		                                                         result.drawRect( 20, 20, 50, 50, true );
		                                       ImageWrite( result, "src/test/resources/generated/logo-test-member.png" );
		                                                                           """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

}
