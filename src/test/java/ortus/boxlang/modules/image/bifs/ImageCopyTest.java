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

public class ImageCopyTest {

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

	@DisplayName( "It should copy the image" )
	@Test
	public void testCopy() throws IOException {
		instance.executeSource( """
		                                          theSource = ImageRead( "src/test/resources/logo.png" );
		                        result = ImageCopy( theSource, 0, 0, 100, 100 );
		                        ImageWrite( result, "src/test/resources/logo-copy-100.png" );
		                                          """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/logo-copy-100.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-copy-100.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should copy the image and offset it" )
	@Test
	public void testCopyOffset() throws IOException {
		instance.executeSource( """
		                                          theSource = ImageRead( "src/test/resources/logo.png" );
		                        result = ImageCopy( theSource, 0, 0, 100, 100, 15, 15 );
		                        ImageWrite( result, "src/test/resources/logo-copy-100-15.png" );
		                                          """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/logo-copy-100-15.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-copy-100-15.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should be invocable member function" )
	@Test
	public void testCopyMemberFunction() throws IOException {
		instance.executeSource( """
		                                          theSource = ImageRead( "src/test/resources/logo.png" );
		                        result = theSource.copy( 0, 0, 100, 100, 15, 15 );
		                        ImageWrite( result, "src/test/resources/logo-copy-100-15.png" );
		                                          """, context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/logo-copy-100-15.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-copy-100-15.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
