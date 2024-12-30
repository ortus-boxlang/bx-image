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

import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.context.ScriptingRequestBoxContext;
import ortus.boxlang.runtime.scopes.IScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.scopes.VariablesScope;

public class ImageGrayScaleTest {

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

	@DisplayName( "It should grayscale the image" )
	@Test
	public void testDrawOval() throws IOException {
		String fileName = "logo-draw-grayscale.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageGrayScale( result );
		                        ImageWrite( result, "src/test/resources/generated/%s" );
		                        // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should be invocable as a member function" )
	@Test
	public void testMemberInvocation() throws IOException {
		String fileName = "logo-draw-grayscale.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        result.grayScale( result );
		                        ImageWrite( result, "src/test/resources/generated/%s" );
		                        // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should be aliased as greyscale " )
	@Test
	public void testGreyAlias() throws IOException {
		String fileName = "logo-draw-grayscale.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageGreyScale( result );
		                        ImageWrite( result, "src/test/resources/generated/%s" );
		                        // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should be invocable as a member function" )
	@Test
	public void testGreyMemberInvocation() throws IOException {
		String fileName = "logo-draw-grayscale.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        result.greyScale();
		                        ImageWrite( result, "src/test/resources/generated/%s" );
		                        // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}
}
