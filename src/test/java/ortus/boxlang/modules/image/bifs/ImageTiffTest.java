package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Array;

public class ImageTiffTest extends BaseIntegrationTest {

	@DisplayName( "It should write a PNG image as TIFF to the specified path" )
	@Test
	public void testWriteTiff() {
		String outputFile = "src/test/resources/generated/test-write.tiff";

		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "src/test/resources/logo.png" );
			img.write( "%s" );
			result = FileExists( "%s" );
		""".formatted( outputFile, outputFile ), context );
		// @formatter:on

		assertThat( ( Boolean ) variables.get( Key.of( "result" ) ) ).isTrue();
	}

	@DisplayName( "It should write and read back a TIFF image with matching dimensions" )
	@Test
	public void testWriteAndReadTiff() {
		String outputFile = "src/test/resources/generated/test-roundtrip.tiff";

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

	@DisplayName( "It should return a non-empty Base64 string when writing TIFF via ImageWriteBase64" )
	@Test
	public void testWriteBase64Tiff() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "src/test/resources/logo.png" );
			result = ImageWriteBase64( img, "tiff" );
		""", context );
		// @formatter:on

		String result = ( String ) variables.get( Key.of( "result" ) );
		assertThat( result ).isNotEmpty();
	}

	@DisplayName( "It should include tiff in the readable image formats" )
	@Test
	public void testGetReadableFormatsIncludesTiff() {
		// @formatter:off
		runtime.executeSource( """
			result = GetReadableImageFormats();
		""", context );
		// @formatter:on

		Array result = ( Array ) variables.get( Key.of( "result" ) );
		assertThat( result.stream().map( Object::toString ).map( String::toLowerCase ).toList() ).contains( "tiff" );
	}

	@DisplayName( "It should include tiff in the writeable image formats" )
	@Test
	public void testGetWriteableFormatsIncludesTiff() {
		// @formatter:off
		runtime.executeSource( """
			result = GetWriteableImageFormats();
		""", context );
		// @formatter:on

		Array result = ( Array ) variables.get( Key.of( "result" ) );
		assertThat( result.stream().map( Object::toString ).map( String::toLowerCase ).toList() ).contains( "tiff" );
	}
}
