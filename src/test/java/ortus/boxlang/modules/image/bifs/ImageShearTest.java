package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BaseIntegrationTest;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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

public class ImageShearTest extends BaseIntegrationTest {

	@DisplayName( "It should shear an image" )
	@Test
	public void testShear() throws IOException {
		String fileName = "logo-draw-shear.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageShear( result, 0.5 );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should shear an image horizontally" )
	@Test
	public void testShearHorizontal() throws IOException {
		String fileName = "logo-draw-shear-horizontal.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageShear( result, 0.5, "horizontal" );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should shear an image vertically" )
	@Test
	public void testShearVertical() throws IOException {
		String fileName = "logo-draw-shear-vertical.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageShear( result, 0.5, 'vertical' );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should shear an image as a member function" )
	@Test
	public void testShearMember() throws IOException {
		String fileName = "logo-draw-shear.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageShear( result, 0.5 );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}
}
