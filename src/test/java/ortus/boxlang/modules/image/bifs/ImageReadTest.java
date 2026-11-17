package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BaseIntegrationTest;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;

import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
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

public class ImageReadTest extends BaseIntegrationTest {

	@DisplayName( "It can read a BoxImage" )
	@Test
	public void testExampleBIF() {
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       width = result.getWidth();
		                                         """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can create a new image from a url" )
	@Test
	public void testImageFromURL() {
		runtime.executeSource(
		    """
		    result = ImageRead( "https://communitycdn.ortussolutions.com/original/2X/1/1459cdd448100319697645d3eb15894396f042df.png" );
		    """,
		    context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can create a new image from a url which will return a relocation header" )
	@Test
	public void testImageFromRelocation() {
		runtime.executeSource(
		    """
		    result = ImageRead( "http://cfdocs.org/apple-touch-icon.png" );
		    """,
		    context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It throws a clear error for a path shorter than 4 characters" )
	@Test
	public void testShortPath() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "ImageRead( 'a' )", context ) );
		assertThat( e.getMessage() ).contains( "Unable to load image: a" );
	}

	@DisplayName( "It throws a clear error for a missing file" )
	@Test
	public void testMissingFile() {
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "ImageRead( 'src/test/resources/nope.png' )", context ) );
	}

	@DisplayName( "It can read an absolute path with forward slashes on Windows" )
	@Test
	public void testForwardSlashAbsolutePathOnWindows() {
		assumeTrue( File.separatorChar == '\\' );
		runtime.executeSource( """
		                       result = ImageRead( replace( expandPath( "src/test/resources/logo.png" ), "\\", "/", "all" ) );
		                       width = result.getWidth();
		                       """, context );
		assertThat( ( ( Number ) variables.get( Key.of( "width" ) ) ).intValue() ).isGreaterThan( 0 );
	}

}
