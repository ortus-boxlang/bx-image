package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageBlurTest extends BaseIntegrationTest {

	@DisplayName( "It can blur the image" )
	@Test
	public void testBlurImage() throws IOException {
		runtime.executeSource( """
		                                                           result = ImageRead( "src/test/resources/logo.png" );
		                                         ImageBlur( result, 50 );
		                       ImageWrite( result, "src/test/resources/generated/logo-blurred.png" );
		                                                           """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-blurred.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/blurred-50.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It can be called as a member function" )
	@Test
	public void testMemberInvocationBlurImage() throws IOException {
		runtime.executeSource( """
		                                                           result = ImageRead( "src/test/resources/logo.png" );
		                                         result.blur(25);
		                       ImageWrite( result, "src/test/resources/generated/logo-blurred.png" );
		                                                           """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-blurred.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/blurred-25.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It can be called without an argument" )
	@Test
	public void testMemberInvocationBlurImageNoArg() throws IOException {
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       result.blur();
		                                         """, context );

		assertThat( true ).isTrue();
	}

}
