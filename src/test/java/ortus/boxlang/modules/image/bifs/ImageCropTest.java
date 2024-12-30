package ortus.boxlang.modules.image.bifs;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

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

public class ImageCropTest {

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

	@DisplayName( "It can crop the image" )
	@Test
	public void testDrawRect() {
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          ImageCrop( result, 20, 20, 120, 120 );
		                        ImageWrite( result, "src/test/resources/generated/logo-cropped.png" );
		                                                            """, context );

		assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can be called as a member method" )
	@Test
	public void testDrawRectFilledMemberInvocation() {
		instance.executeSource( """
		                                                                              result = ImageRead( "src/test/resources/logo.png" );
		                        result.crop( 20, 20, 120, 120 );
		                                          ImageWrite( result, "src/test/resources/generated/logo-cropped-member.png" );
		                                                                              """, context );

		assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

}
