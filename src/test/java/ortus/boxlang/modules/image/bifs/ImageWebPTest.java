package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Array;

public class ImageWebPTest extends BaseIntegrationTest {

	private static boolean WEBP_WRITE_SUPPORTED;

	@BeforeAll
	static void detectWebPWriteSupport() {
		try {
			ByteArrayOutputStream	baos	= new ByteArrayOutputStream();
			BufferedImage			img		= new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
			WEBP_WRITE_SUPPORTED = ImageIO.write( img, "webp", baos );
		} catch ( Throwable t ) {
			WEBP_WRITE_SUPPORTED = false;
		}
	}

	@DisplayName( "It should write a PNG image as WebP to the specified path" )
	@Test
	public void testWriteWebP() {
		Assumptions.assumeTrue( WEBP_WRITE_SUPPORTED, "WebP native write library not available on this platform" );
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
		Assumptions.assumeTrue( WEBP_WRITE_SUPPORTED, "WebP native write library not available on this platform" );
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

	@DisplayName( "It should return a non-empty Base64 string when writing WebP via ImageWriteBase64" )
	@Test
	public void testWriteBase64WebP() {
		Assumptions.assumeTrue( WEBP_WRITE_SUPPORTED, "WebP native write library not available on this platform" );

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
		Assumptions.assumeTrue( WEBP_WRITE_SUPPORTED, "WebP native write library not available on this platform" );

		// @formatter:off
		runtime.executeSource( """
			result = GetWriteableImageFormats();
		""", context );
		// @formatter:on

		Array result = ( Array ) variables.get( Key.of( "result" ) );
		assertThat( result.stream().map( Object::toString ).map( String::toLowerCase ).toList() ).contains( "webp" );
	}
}
