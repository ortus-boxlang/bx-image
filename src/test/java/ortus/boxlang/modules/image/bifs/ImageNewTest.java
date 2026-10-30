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

public class ImageNewTest extends BaseIntegrationTest {

	@DisplayName( "It can create a new image" )
	@Test
	public void testNewImage() {
		runtime.executeSource( """
		                                                           result = ImageNew( "", 256, 256, "argb", "green" );
		                       ImageWrite( result, "src/test/resources/new-image.png" );
		                                                           """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can create a new image from a url" )
	@Test
	public void testImageFromURL() {
		runtime.executeSource(
		    """
		                                        result = ImageNew( "https://communitycdn.ortussolutions.com/original/2X/1/1459cdd448100319697645d3eb15894396f042df.png" );
		    ImageWrite( result, "src/test/resources/new-image-url.png" );
		                                        """,
		    context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can create a new image from a file" )
	@Test
	public void testImageFromFile() {
		runtime.executeSource(
		    """
		       result = ImageNew( "src/test/resources/logo.png" );
		    ImageWrite( result, "src/test/resources/new-image-file.png" );
		       """,
		    context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can create a new image from an existing BoxImage" )
	@Test
	public void testImageFromBoxImage() {
		runtime.executeSource(
		    """
		            a = ImageNew( "src/test/resources/logo.png" );
		          result = ImageNew( a );
		       a.setDrawingColor( "green" );
		       a.drawRect( 20, 20, 50, 50, true );
		    result.setDrawingColor( "blue" );
		       result.drawRect( 20, 20, 50, 50, true );

		    a.write( "src/test/resources/new-image-copy-orig.png" );
		       result.write( "src/test/resources/new-image-copy-copy.png" );
		            """,
		    context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can create a new image from a relative path" )
	@Test
	public void testRelativePath() {
		runtime.executeSource( """
		                       result = ImageNew( "src/test/resources/logo.png" );
		                       width = result.getWidth();
		                       """, context );
		assertThat( ( ( Number ) variables.get( Key.of( "width" ) ) ).intValue() ).isGreaterThan( 0 );
	}

	@DisplayName( "It can create a new image from an absolute path" )
	@Test
	public void testAbsolutePath() {
		runtime.executeSource( """
		                       result = ImageNew( expandPath( "src/test/resources/logo.png" ) );
		                       width = result.getWidth();
		                       """, context );
		assertThat( ( ( Number ) variables.get( Key.of( "width" ) ) ).intValue() ).isGreaterThan( 0 );
	}

	@DisplayName( "It can create a new image from an absolute path with forward slashes on Windows" )
	@Test
	public void testForwardSlashAbsolutePathOnWindows() {
		assumeTrue( File.separatorChar == '\\' );
		runtime.executeSource( """
		                       result = ImageNew( replace( expandPath( "src/test/resources/logo.png" ), "\\", "/", "all" ) );
		                       width = result.getWidth();
		                       """, context );
		assertThat( ( ( Number ) variables.get( Key.of( "width" ) ) ).intValue() ).isGreaterThan( 0 );
	}

	@DisplayName( "It throws a clear error for a short path" )
	@Test
	public void testShortPath() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "ImageNew( 'a' )", context ) );
		assertThat( e.getMessage() ).contains( "Unable to load image: a" );
	}

	@DisplayName( "It throws a clear error for a Windows style path that does not exist" )
	@Test
	public void testMissingWindowsPath() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class,
		    () -> runtime.executeSource( "ImageNew( 'D:\\a\\x\\card.png' )", context ) );
		assertThat( e.getMessage() ).contains( "Unable to load image: D:" );
	}

}
