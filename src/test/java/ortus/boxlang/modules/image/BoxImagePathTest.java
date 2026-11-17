package ortus.boxlang.modules.image;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.net.URI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class BoxImagePathTest extends BaseIntegrationTest {

	private static final boolean IS_WINDOWS = File.separatorChar == '\\';

	@DisplayName( "It detects Windows drive paths" )
	@Test
	public void testIsWindowsDrivePath() {
		assertThat( BoxImage.isWindowsDrivePath( "D:\\a\\x\\card.png" ) ).isTrue();
		assertThat( BoxImage.isWindowsDrivePath( "C:/x/card.png" ) ).isTrue();
		assertThat( BoxImage.isWindowsDrivePath( "c:\\x" ) ).isTrue();
		assertThat( BoxImage.isWindowsDrivePath( "https://x.com/a.png" ) ).isFalse();
		assertThat( BoxImage.isWindowsDrivePath( "src/test/resources/logo.png" ) ).isFalse();
		assertThat( BoxImage.isWindowsDrivePath( "a" ) ).isFalse();
		assertThat( BoxImage.isWindowsDrivePath( "" ) ).isFalse();
	}

	@DisplayName( "It detects http, https and file URI sources, including short strings" )
	@Test
	public void testIsURISource() {
		assertThat( BoxImage.isURISource( "http://x.com/a.png" ) ).isTrue();
		assertThat( BoxImage.isURISource( "HTTPS://x.com/a.png" ) ).isTrue();
		assertThat( BoxImage.isURISource( "file:///tmp/a.png" ) ).isTrue();
		assertThat( BoxImage.isURISource( "C:/x/a.png" ) ).isFalse();
		assertThat( BoxImage.isURISource( "a" ) ).isFalse();
		assertThat( BoxImage.isURISource( "ab" ) ).isFalse();
		assertThat( BoxImage.isURISource( "" ) ).isFalse();
	}

	@DisplayName( "It converts a backslash Windows path to a file URI" )
	@Test
	public void testBackslashWindowsPath() {
		URI uri = BoxImage.stringToURI( "D:\\a\\x\\card.png" );
		assertThat( uri.getScheme() ).isEqualTo( "file" );
		assertThat( uri.getPath() ).isEqualTo( "/D:/a/x/card.png" );
	}

	@DisplayName( "It converts a forward slash drive path to a file URI, not a URI with scheme C" )
	@Test
	public void testForwardSlashDrivePath() {
		URI uri = BoxImage.stringToURI( "C:/x/card.png" );
		assertThat( uri.getScheme() ).isEqualTo( "file" );
		assertThat( uri.getPath() ).isEqualTo( "/C:/x/card.png" );
	}

	@DisplayName( "It leaves http URLs as they are" )
	@Test
	public void testHttpUrl() {
		URI uri = BoxImage.stringToURI( "https://example.com/a.png" );
		assertThat( uri.getScheme() ).isEqualTo( "https" );
	}

	@DisplayName( "It loads an image from an absolute path with backslashes on Windows" )
	@Test
	public void testLoadBackslashPathOnWindows() throws Exception {
		assumeTrue( IS_WINDOWS );
		String path = new File( "src/test/resources/logo.png" ).getAbsolutePath();
		assertThat( new BoxImage( path ).getWidth() ).isGreaterThan( 0 );
	}

	@DisplayName( "It loads an image from an absolute path with forward slashes on Windows" )
	@Test
	public void testLoadForwardSlashPathOnWindows() throws Exception {
		assumeTrue( IS_WINDOWS );
		String path = new File( "src/test/resources/logo.png" ).getAbsolutePath().replace( '\\', '/' );
		assertThat( new BoxImage( path ).getWidth() ).isGreaterThan( 0 );
	}

	@DisplayName( "It throws a clear error for a missing file" )
	@Test
	public void testMissingFile() {
		assertThrows( Exception.class, () -> new BoxImage( "src/test/resources/does-not-exist.png" ) );
	}

	@DisplayName( "imageNew reports the source in its error message" )
	@Test
	public void testImageNewMessage() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class,
		    () -> runtime.executeSource( "imageNew( 'src/test/resources/does-not-exist.png' )", context ) );
		assertThat( e.getMessage() ).contains( "Unable to load image: src/test/resources/does-not-exist.png" );
	}
}
