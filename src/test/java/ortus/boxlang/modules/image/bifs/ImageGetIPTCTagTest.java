package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

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

public class ImageGetIPTCTagTest {

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

	@DisplayName( "It should read the exif data from a path and return the tag value" )
	@Test
	public void testReadFromPath() throws IOException {
		instance.executeSource(
		    """
		    result = ImageGetIPTCTag( "src/test/resources/test-images/exif-test.jpg", "city" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isEqualTo( "test city" );
	}

	@DisplayName( "It should read the exif data from an image and return the tag value" )
	@Test
	public void testReadFromImage() throws IOException {
		instance.executeSource(
		    """
		    result = ImageGetIPTCTag( imageRead( "src/test/resources/test-images/exif-test.jpg" ), "city" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isEqualTo( "test city" );
	}

	@DisplayName( "It be callable as a member function" )
	@Test
	public void testMemberInvocation() throws IOException {
		instance.executeSource(
		    """
		    result = imageRead( "src/test/resources/test-images/exif-test.jpg" ).GetIPTCTag( "city" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isEqualTo( "test city" );
	}

}
