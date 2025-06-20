package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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

public class ImageScaleToFitTest {

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

	@DisplayName( "It should resize an image and keep the aspect ratio" )
	@Test
	public void testScaleToFit() throws IOException {
		String fileName = "logo-scale-to-fit.png";
		// @formatter:off
		instance.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			ImageScaleToFit( result, 100, "" );
			ImageWrite( result, "src/test/resources/generated/%s" );
			// ImageWrite( result, "src/test/resources/test-images/%s" )
		""".formatted( fileName, fileName ), context );
		// @formatter:on

		assertThat( variables.get( result ) ).isInstanceOf( BoxImage.class );
		BoxImage res = ( BoxImage ) variables.get( result );

		assertThat( res.getWidth() ).isEqualTo( 100 );
		assertThat( res.getHeight() ).isEqualTo( 100 );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should resize an image when called as a member function" )
	@Test
	public void testScaleToFitMemberInvocation() throws IOException {
		String fileName = "logo-scale-to-fit.png";
		// @formatter:off
		instance.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			result.scaleToFIt( 100, "" );
			ImageWrite( result, "src/test/resources/generated/%s" );
			// ImageWrite( result, "src/test/resources/test-images/%s" )
		""".formatted( fileName, fileName ), context );
		// @formatter:on

		assertThat( variables.get( result ) ).isInstanceOf( BoxImage.class );
		BoxImage res = ( BoxImage ) variables.get( result );

		assertThat( res.getWidth() ).isEqualTo( 100 );
		assertThat( res.getHeight() ).isEqualTo( 100 );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
