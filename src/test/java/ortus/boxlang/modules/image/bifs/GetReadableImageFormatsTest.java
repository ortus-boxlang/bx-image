package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BaseIntegrationTest;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

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
import ortus.boxlang.runtime.types.Array;

public class GetReadableImageFormatsTest extends BaseIntegrationTest {

	@DisplayName( "It can return an array of image formats" )
	@Test
	public void testGetReadableImageFormats() throws IOException {
		runtime.executeSource( """
		                       result = getReadableImageFormats();
		                       """, context );

		assertInstanceOf( ortus.boxlang.runtime.types.Array.class, variables.get( result ) );

		Array res = variables.getAsArray( result );

		assertThat( res.size() ).isGreaterThan( 0 );
		assertThat( res.get( 0 ) instanceof String ).isTrue();
	}

}
