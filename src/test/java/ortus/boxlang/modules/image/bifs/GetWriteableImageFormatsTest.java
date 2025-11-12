package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.types.Array;

public class GetWriteableImageFormatsTest extends BaseIntegrationTest {

	@DisplayName( "It can return an array of image formats that it can write to" )
	@Test
	public void testGetWriteableImageFormats() throws IOException {
		runtime.executeSource( """
		                       result = getWriteableImageFormats();
		                       """, context );

		assertInstanceOf( ortus.boxlang.runtime.types.Array.class, variables.get( result ) );

		Array res = variables.getAsArray( result );

		assertThat( res.size() ).isGreaterThan( 0 );
		assertThat( res.get( 0 ) instanceof String ).isTrue();
	}

}
