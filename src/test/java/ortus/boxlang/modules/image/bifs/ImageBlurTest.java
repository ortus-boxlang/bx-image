package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

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

public class ImageBlurTest {

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

	@DisplayName( "It can blur the image" )
	@Test
	public void testBlurImage() throws IOException {
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          ImageBlur( result, 50 );
		                        ImageWrite( result, "src/test/resources/logo-blurred.png" );
		                                                            """, context );

		assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/logo-blurred.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/blurred-50.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It can be called as a member function" )
	@Test
	public void testMemberInvocationBlurImage() throws IOException {
		instance.executeSource( """
		                                                            result = ImageRead( "src/test/resources/logo.png" );
		                                          result.blur(25);
		                        ImageWrite( result, "src/test/resources/logo-blurred.png" );
		                                                            """, context );

		assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/logo-blurred.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/blurred-25.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It can be called without an argument" )
	@Test
	public void testMemberInvocationBlurImageNoArg() throws IOException {
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        result.blur();
		                                          """, context );

		assertThat( true ).isTrue();
	}

}
