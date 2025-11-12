package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageGetHeightTest extends BaseIntegrationTest {

	@DisplayName( "It should return the Height of an image" )
	@Test
	public void testGetHeight() throws IOException {
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       result = ImageGetHeight( result );
		                                         """, context );

		assertThat( variables.get( result ) ).isEqualTo( 256 );
	}

	@DisplayName( "It should be invocable as a member function" )
	@Test
	public void testGetHeightMember() throws IOException {
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       result = result.getHeight()
		                                         """, context );

		assertThat( variables.get( result ) ).isEqualTo( 256 );
	}

}
