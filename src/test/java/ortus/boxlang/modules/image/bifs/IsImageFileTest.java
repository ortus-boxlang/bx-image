package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class IsImageFileTest extends BaseIntegrationTest {

	@DisplayName( "It should true if the file is an image" )
	@Test
	public void testTrueIfImage() {
		runtime.executeSource( """
		                       result = isImageFile( "src/test/resources/logo.png" );
		                       """, context );

		assertThat( variables.get( result ) ).isEqualTo( true );
	}

	@DisplayName( "It should true if the file is an image and the path has backslashes" )
	@Test
	public void testTrueIfImageWithBackslashes() {
		runtime.executeSource( """
		                       result = isImageFile( "src\\test\\resources\\logo.png" );
		                       """, context );

		assertThat( variables.get( result ) ).isEqualTo( true );
	}

	@DisplayName( "It should return false for anything other than an image file" )
	@Test
	public void testFalseIfNotImage() {
		runtime.executeSource( """
		                       result = isImageFile( "test" );
		                                         """, context );

		assertThat( variables.get( result ) ).isEqualTo( false );
	}

}
