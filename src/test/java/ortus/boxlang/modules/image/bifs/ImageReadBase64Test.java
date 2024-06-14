package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.context.ScriptingRequestBoxContext;
import ortus.boxlang.runtime.scopes.IScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.scopes.VariablesScope;

public class ImageReadBase64Test {

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

	@DisplayName( "It should return an image from a base64 string" )
	@Test
	public void testReadFromBase64() throws IOException {
		instance.executeSource(
		    """
		    result = ImageReadBase64( "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" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( BoxImage.class );
	}
}
