package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BaseIntegrationTest;

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

public class ImageNegativeTest extends BaseIntegrationTest {

	@DisplayName( "It can make a negative image" )
	@Test
	public void testNegative() {
		runtime.executeSource( """
		                                                           result = ImageRead( "src/test/resources/logo.png" );
		                                         ImageNegative( result );
		                       ImageWrite( result, "src/test/resources/generated/logo-negative.png" );
		                                                           """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can be called as a member method" )
	@Test
	public void testDrawRectFilledMemberInvocation() {
		runtime.executeSource( """
		                                                                          result = ImageRead( "src/test/resources/logo.png" );
		                       result.setDrawingColor( "magenta" );
		                                                        result.drawRect( 20, 20, 50, 50, true );
		                                      ImageWrite( result, "src/test/resources/generated/logo-test-member.png" );
		                                                                          """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

}
