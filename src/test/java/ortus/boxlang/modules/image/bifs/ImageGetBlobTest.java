package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageGetBlobTest extends BaseIntegrationTest {

	@DisplayName( "It should return a byte array" )
	@Test
	public void testReturnsAnArray() throws IOException {
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       result = ImageGetBlob( result );
		                                         """, context );

		assertThat( variables.get( result ).getClass().isArray() ).isTrue();
		assertThat( ( ( byte[] ) variables.get( result ) ).length ).isGreaterThan( 100 );
	}

	@DisplayName( "It should be callable as a member function" )
	@Test
	public void testMemberInvocation() throws IOException {
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       result = result.getBLob();
		                                         """, context );

		assertThat( variables.get( result ).getClass().isArray() ).isTrue();
		assertThat( ( ( byte[] ) variables.get( result ) ).length ).isGreaterThan( 100 );
	}

}
