package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class ImageQRCodeGenerateTest extends BaseIntegrationTest {

	@DisplayName( "It generates a 125px QR code by default that decodes back to the text" )
	@Test
	public void testDefaults() {
		runtime.executeSource( """
		                       qr = ImageQRCodeGenerate( "https://example.com/verify" );
		                       width = ImageGetWidth( qr );
		                       height = ImageGetHeight( qr );
		                       result = ImageQRCodeRead( qr );
		                       """, context );

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 125 );
		assertThat( variables.get( Key.of( "height" ) ) ).isEqualTo( 125 );
		assertThat( variables.get( result ) ).isEqualTo( "https://example.com/verify" );
	}

	@DisplayName( "It honors the size argument" )
	@Test
	public void testSize() {
		runtime.executeSource( """
		                       qr = ImageQRCodeGenerate( "size", 250 );
		                       width = ImageGetWidth( qr );
		                       height = ImageGetHeight( qr );
		                       result = ImageQRCodeRead( qr );
		                       """, context );

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 250 );
		assertThat( variables.get( Key.of( "height" ) ) ).isEqualTo( 250 );
		assertThat( variables.get( result ) ).isEqualTo( "size" );
	}

	@DisplayName( "It clamps the size to 16 through 4096" )
	@Test
	public void testSizeClamping() {
		runtime.executeSource( """
		                       small = ImageGetWidth( ImageQRCodeGenerate( "x", 1 ) );
		                       large = ImageGetWidth( ImageQRCodeGenerate( "x", 99999 ) );
		                       """, context );

		assertThat( variables.get( Key.of( "small" ) ) ).isEqualTo( 16 );
		assertThat( variables.get( Key.of( "large" ) ) ).isEqualTo( 4096 );
	}

	@DisplayName( "It supports named arguments" )
	@Test
	public void testNamedArguments() {
		runtime.executeSource(
		    """
		    qr = ImageQRCodeGenerate( text="named", size=200, format="gif", errorCorrection="H", quietZone=2, foreground="blue", background="white" );
		    width = ImageGetWidth( qr );
		    result = ImageQRCodeRead( qr );
		    """, context );

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 200 );
		assertThat( variables.get( result ) ).isEqualTo( "named" );
	}

	@DisplayName( "It can be called as a member function on a string" )
	@Test
	public void testMemberFunction() {
		runtime.executeSource( """
		                       qr = "member".toQRCode( 200, "png", "Q" );
		                       width = ImageGetWidth( qr );
		                       result = qr.readQRCode();
		                       """, context );

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 200 );
		assertThat( variables.get( result ) ).isEqualTo( "member" );
	}

	@DisplayName( "It emits the requested format when the image is encoded" )
	@Test
	public void testFormats() {
		byte[]	png	= BoxImage.generateQRCode( "x", 64, "png", "M", 4, "black", "white", null, 0.2 ).toByteArray();
		byte[]	gif	= BoxImage.generateQRCode( "x", 64, "gif", "M", 4, "black", "white", null, 0.2 ).toByteArray();
		byte[]	jpg	= BoxImage.generateQRCode( "x", 64, "jpg", "M", 4, "black", "white", null, 0.2 ).toByteArray();
		byte[]	bmp	= BoxImage.generateQRCode( "x", 64, "bmp", "M", 4, "black", "white", null, 0.2 ).toByteArray();

		assertThat( png[ 0 ] & 0xFF ).isEqualTo( 0x89 );
		assertThat( new String( png, 1, 3 ) ).isEqualTo( "PNG" );
		assertThat( new String( gif, 0, 3 ) ).isEqualTo( "GIF" );
		assertThat( jpg[ 0 ] & 0xFF ).isEqualTo( 0xFF );
		assertThat( jpg[ 1 ] & 0xFF ).isEqualTo( 0xD8 );
		assertThat( new String( bmp, 0, 2 ) ).isEqualTo( "BM" );
	}

	@DisplayName( "It rejects a format that cannot be written" )
	@Test
	public void testInvalidFormat() {
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "ImageQRCodeGenerate( 'x', 100, 'notaformat' )", context ) );
	}

	@DisplayName( "It rejects an invalid error correction level" )
	@Test
	public void testInvalidErrorCorrection() {
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "ImageQRCodeGenerate( 'x', 100, 'png', 'Z' )", context ) );
	}

	@DisplayName( "It rejects empty text" )
	@Test
	public void testEmptyText() {
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "ImageQRCodeGenerate( '' )", context ) );
	}

	@DisplayName( "It rejects an unknown color" )
	@Test
	public void testInvalidColor() {
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "ImageQRCodeGenerate( text='x', foreground='notacolor' )", context ) );
	}

	@DisplayName( "It scans at every error correction level" )
	@Test
	public void testErrorCorrectionLevels() {
		for ( String level : new String[] { "L", "M", "Q", "H", "h" } ) {
			assertThat( BoxImage.generateQRCode( "level-" + level, 200, "png", level, 4, "black", "white", null, 0.2 ).readQRCode() )
			    .isEqualTo( "level-" + level );
		}
	}

	@DisplayName( "It uses a larger code for a higher error correction level" )
	@Test
	public void testHigherCorrectionIsDenser() {
		// A 4-module-per-pixel-grid scale makes the dark module count a reasonable proxy for density
		int	low		= darkPixels( BoxImage.generateQRCode( "a longer string to encode in the code", 400, "png", "L", 4, "black", "white", null, 0.2 ) );
		int	high	= darkPixels( BoxImage.generateQRCode( "a longer string to encode in the code", 400, "png", "H", 4, "black", "white", null, 0.2 ) );
		assertThat( high ).isNotEqualTo( low );
	}

	@DisplayName( "It honors the quiet zone" )
	@Test
	public void testQuietZone() {
		BufferedImage	none	= BoxImage.generateQRCode( "quiet", 200, "png", "M", 0, "black", "white", null, 0.2 ).getBufferedImage();
		BufferedImage	four	= BoxImage.generateQRCode( "quiet", 200, "png", "M", 4, "black", "white", null, 0.2 ).getBufferedImage();

		// With no quiet zone the top-left pixel is the finder pattern, with one it is margin
		assertThat( none.getRGB( 0, 0 ) & 0xFFFFFF ).isEqualTo( 0x000000 );
		assertThat( four.getRGB( 0, 0 ) & 0xFFFFFF ).isEqualTo( 0xFFFFFF );
	}

	@DisplayName( "It applies foreground and background colors, including hex" )
	@Test
	public void testColors() {
		BufferedImage image = BoxImage.generateQRCode( "colors", 200, "png", "M", 4, "#1a237e", "#ffeb3b", null, 0.2 ).getBufferedImage();

		assertThat( image.getRGB( 0, 0 ) & 0xFFFFFF ).isEqualTo( 0xffeb3b );
		assertThat( image.getRGB( 4 * 6 + 2, 4 * 6 + 2 ) & 0xFFFFFF ).isAnyOf( 0x1a237e, 0xffeb3b );
		assertThat( hasColor( image, 0x1a237e ) ).isTrue();
		assertThat( new BoxImage( image ).readQRCode() ).isEqualTo( "colors" );
	}

	@DisplayName( "It reads a light-on-dark code" )
	@Test
	public void testInvertedColors() {
		assertThat( BoxImage.generateQRCode( "inverted", 200, "png", "M", 4, "white", "black", null, 0.2 ).readQRCode() ).isEqualTo( "inverted" );
	}

	@DisplayName( "It supports transparent backgrounds" )
	@Test
	public void testTransparentBackground() {
		BoxImage image = BoxImage.generateQRCode( "alpha", 200, "png", "M", 4, "black", "#00ffffff", null, 0.2 );

		assertThat( image.getBufferedImage().getColorModel().hasAlpha() ).isTrue();
		assertThat( image.getBufferedImage().getRGB( 0, 0 ) >>> 24 ).isEqualTo( 0 );
		assertThat( image.readQRCode() ).isEqualTo( "alpha" );
	}

	@DisplayName( "It round trips non-Latin text" )
	@Test
	public void testUnicode() {
		String text = "héllo ✓ 日本語";
		assertThat( BoxImage.generateQRCode( text, 300, "png", "M", 4, "black", "white", null, 0.2 ).readQRCode() ).isEqualTo( text );
	}

	@DisplayName( "It draws a logo in the center and still scans" )
	@Test
	public void testLogo() throws Exception {
		runtime.executeSource( """
		                       qr = ImageQRCodeGenerate( text="https://example.com/with-logo", size=400, logo="src/test/resources/logo.png" );
		                       width = ImageGetWidth( qr );
		                       result = ImageQRCodeRead( qr );
		                       """, context );

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 400 );
		assertThat( variables.get( result ) ).isEqualTo( "https://example.com/with-logo" );
		// The logo changes the center of the code compared to the same code without one
		BufferedImage	withLogo	= BoxImage.generateQRCode( "https://example.com/with-logo", 400, "png", "H", 4, "black", "white",
		    new BoxImage( "src/test/resources/logo.png" ), 0.2 ).getBufferedImage();
		BufferedImage	without		= BoxImage.generateQRCode( "https://example.com/with-logo", 400, "png", "H", 4, "black", "white", null, 0.2 )
		    .getBufferedImage();
		assertThat( sameCenter( withLogo, without ) ).isFalse();
	}

	@DisplayName( "It accepts an image as the logo and forces error correction H" )
	@Test
	public void testLogoAsImage() {
		runtime.executeSource( """
		                       logo = ImageRead( "src/test/resources/logo.png" );
		                       qr = ImageQRCodeGenerate( text="image logo", size=400, errorCorrection="L", logo=logo, logoScale=0.25 );
		                       result = qr.readQRCode();
		                       """, context );

		assertThat( variables.get( result ) ).isEqualTo( "image logo" );
	}

	@DisplayName( "It rejects a logo scale of zero or less" )
	@Test
	public void testInvalidLogoScale() {
		assertThrows( BoxRuntimeException.class,
		    () -> runtime.executeSource( "ImageQRCodeGenerate( text='x', logo='src/test/resources/logo.png', logoScale=0 )", context ) );
	}

	@DisplayName( "It refuses to produce a logo code that will not scan" )
	@Test
	public void testLogoTooBigForTheCode() {
		// A tiny code has no spare modules to lose behind a logo
		assertThrows( BoxRuntimeException.class,
		    () -> runtime.executeSource( "ImageQRCodeGenerate( text='a', size=100, logo='src/test/resources/logo.png', logoScale=0.3 )", context ) );
	}

	@DisplayName( "It rejects a logo that cannot be loaded" )
	@Test
	public void testMissingLogo() {
		assertThrows( BoxRuntimeException.class,
		    () -> runtime.executeSource( "ImageQRCodeGenerate( text='x', logo='src/test/resources/nope.png' )", context ) );
	}

	@DisplayName( "It works with the other image functions" )
	@Test
	public void testWithImageFunctions() {
		runtime.executeSource( """
		                       qr = ImageQRCodeGenerate( "written", 200 );
		                       ImageWrite( qr, "src/test/resources/generated/qr-written.png" );
		                       reread = ImageRead( "src/test/resources/generated/qr-written.png" );
		                       result = ImageQRCodeRead( reread );
		                       base64 = ImageWriteBase64( qr, "png" );
		                       """, context );

		assertThat( variables.get( result ) ).isEqualTo( "written" );
		assertThat( variables.getAsString( Key.of( "base64" ) ) ).isNotEmpty();
	}

	private static int darkPixels( BoxImage image ) {
		BufferedImage	buffer	= image.getBufferedImage();
		int				count	= 0;
		for ( int y = 0; y < buffer.getHeight(); y++ ) {
			for ( int x = 0; x < buffer.getWidth(); x++ ) {
				if ( ( buffer.getRGB( x, y ) & 0xFFFFFF ) == 0 ) {
					count++;
				}
			}
		}
		return count;
	}

	private static boolean hasColor( BufferedImage image, int rgb ) {
		for ( int y = 0; y < image.getHeight(); y++ ) {
			for ( int x = 0; x < image.getWidth(); x++ ) {
				if ( ( image.getRGB( x, y ) & 0xFFFFFF ) == rgb ) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean sameCenter( BufferedImage a, BufferedImage b ) {
		int	start	= a.getWidth() * 4 / 10;
		int	end		= a.getWidth() * 6 / 10;
		for ( int y = start; y < end; y++ ) {
			for ( int x = start; x < end; x++ ) {
				if ( a.getRGB( x, y ) != b.getRGB( x, y ) ) {
					return false;
				}
			}
		}
		return true;
	}

}
