package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Array;

public class ImageWebPTest extends BaseIntegrationTest {

	private static final String	WEBP_FIXTURE	= "UklGRlQAAABXRUJQVlA4WAoAAAAQAAAAAQAAAQAAQUxQSAUAAAAAb3OviQBWUDggKAAAALABAJ0BKgIAAgACADQljAJ0AQ4kAxAA8kGkZwGgrWTvS9JdLi0AAAA=";

	@TempDir
	Path						tempDir;

	@DisplayName( "It should write a PNG image as WebP to the specified path" )
	@Test
	public void testWriteWebP() {
		String outputFile = "src/test/resources/generated/test-write.webp";

		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "src/test/resources/logo.png" );
			img.write( "%s" );
			result = FileExists( "%s" );
		""".formatted( outputFile, outputFile ), context );
		// @formatter:on

		assertThat( ( Boolean ) variables.get( Key.of( "result" ) ) ).isTrue();
	}

	@DisplayName( "It should write and read back a WebP image with matching dimensions" )
	@Test
	public void testWriteAndReadWebP() {
		String outputFile = "src/test/resources/generated/test-roundtrip.webp";

		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "src/test/resources/logo.png" );
			img.scaleToFit( 64 );
			img.write( "%s" );
			reloaded = ImageRead( "%s" );
			width = reloaded.getWidth();
		""".formatted( outputFile, outputFile ), context );
		// @formatter:on

		int width = ( int ) variables.get( Key.of( "width" ) );
		assertThat( width ).isEqualTo( 64 );
	}

	@DisplayName( "It should read an existing WebP image without requiring WebP write support" )
	@Test
	public void testReadExistingWebP() throws IOException {
		Path inputFile = tempDir.resolve( "fixture.webp" );
		Files.write( inputFile, Base64.getDecoder().decode( WEBP_FIXTURE ) );

		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "%s" );
			result = [ img.getWidth(), img.getHeight() ];
			""".formatted( inputFile ), context );
		// @formatter:on

		Array dimensions = ( Array ) variables.get( Key.of( "result" ) );
		assertThat( dimensions ).containsExactly( 2, 2 ).inOrder();
	}

	@DisplayName( "It should return a non-empty Base64 string when writing WebP via ImageWriteBase64" )
	@Test
	public void testWriteBase64WebP() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "src/test/resources/logo.png" );
			result = ImageWriteBase64( img, "webp" );
		""", context );
		// @formatter:on

		String result = ( String ) variables.get( Key.of( "result" ) );
		assertThat( result ).isNotEmpty();
	}

	@DisplayName( "It should include webp in the readable image formats" )
	@Test
	public void testGetReadableFormatsIncludesWebP() {
		// @formatter:off
		runtime.executeSource( """
			result = GetReadableImageFormats();
		""", context );
		// @formatter:on

		Array result = ( Array ) variables.get( Key.of( "result" ) );
		assertThat( result.stream().map( Object::toString ).map( String::toLowerCase ).toList() ).contains( "webp" );
	}

	@DisplayName( "It should include webp in the writeable image formats" )
	@Test
	public void testGetWriteableFormatsIncludesWebP() {
		// @formatter:off
		runtime.executeSource( """
			result = GetWriteableImageFormats();
		""", context );
		// @formatter:on

		Array result = ( Array ) variables.get( Key.of( "result" ) );
		assertThat( result.stream().map( Object::toString ).map( String::toLowerCase ).toList() ).contains( "webp" );
	}
}
