package ortus.boxlang.modules.image;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.util.Base64;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.runtime.scopes.Key;

public class BoxImageToByteArrayAndBase64Test extends BaseIntegrationTest {

	private static final byte[]	PNG_SIGNATURE	= new byte[] {
	    ( byte ) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
	};

	private static final byte[]	JPEG_SIGNATURE	= new byte[] {
	    ( byte ) 0xFF, ( byte ) 0xD8, ( byte ) 0xFF
	};

	@DisplayName( "BoxImage can encode to byte[]" )
	@Test
	public void testToByteArrayDefaultAndExplicitFormat() {
		runtime.executeSource(
		    """
		    img = ImageRead( "src/test/resources/logo.png" );
		    bytesDefault = img.toByteArray();
		    bytesPng = img.toByteArray( "png" );
		    """,
		    context );

		byte[]	bytesDefault	= ( byte[] ) variables.get( Key.of( "bytesDefault" ) );
		byte[]	bytesPng		= ( byte[] ) variables.get( Key.of( "bytesPng" ) );

		assertThat( bytesDefault ).isNotNull();
		assertThat( bytesDefault.length ).isGreaterThan( PNG_SIGNATURE.length );
		for ( int i = 0; i < PNG_SIGNATURE.length; i++ ) {
			assertThat( bytesDefault[ i ] ).isEqualTo( PNG_SIGNATURE[ i ] );
		}

		assertThat( bytesPng ).isNotNull();
		assertThat( bytesPng.length ).isGreaterThan( PNG_SIGNATURE.length );
		for ( int i = 0; i < PNG_SIGNATURE.length; i++ ) {
			assertThat( bytesPng[ i ] ).isEqualTo( PNG_SIGNATURE[ i ] );
		}
	}

	@DisplayName( "BoxImage can encode to Base64 string (and round-trip)" )
	@Test
	public void testToBase64StringDefaultAndExplicitFormatRoundTrip() {
		runtime.executeSource(
		    """
		    img = ImageRead( "src/test/resources/logo.png" );
		    base64Default = img.toBase64String();
		    base64Png = img.toBase64String( "png" );

		    img2 = ImageReadBase64( base64Png );
		    width1 = img.getWidth();
		    height1 = img.getHeight();
		    width2 = img2.getWidth();
		    height2 = img2.getHeight();
		    """,
		    context );

		String	base64Default	= ( String ) variables.get( Key.of( "base64Default" ) );
		String	base64Png		= ( String ) variables.get( Key.of( "base64Png" ) );

		assertThat( base64Default ).isNotNull();
		assertThat( base64Default ).isNotEmpty();
		assertThat( base64Png ).isNotNull();
		assertThat( base64Png ).isNotEmpty();
		assertThat( base64Default ).isEqualTo( base64Png );

		byte[] decoded = Base64.getDecoder().decode( base64Png );
		assertThat( decoded.length ).isGreaterThan( PNG_SIGNATURE.length );
		for ( int i = 0; i < PNG_SIGNATURE.length; i++ ) {
			assertThat( decoded[ i ] ).isEqualTo( PNG_SIGNATURE[ i ] );
		}

		int	width1	= ( int ) variables.get( Key.of( "width1" ) );
		int	height1	= ( int ) variables.get( Key.of( "height1" ) );
		int	width2	= ( int ) variables.get( Key.of( "width2" ) );
		int	height2	= ( int ) variables.get( Key.of( "height2" ) );

		assertThat( width1 ).isGreaterThan( 0 );
		assertThat( height1 ).isGreaterThan( 0 );
		assertThat( width2 ).isEqualTo( width1 );
		assertThat( height2 ).isEqualTo( height1 );
	}

	@DisplayName( "BoxImage can encode to JPG when specified" )
	@Test
	public void testToByteArrayAndBase64StringExplicitJpg() {
		runtime.executeSource(
		    """
		    img = ImageNew( "", 40, 40, "rgb", "white" );
		    bytesJpg = img.toByteArray( "jpg" );
		    base64Jpg = img.toBase64String( "jpg" );
		    """,
		    context );

		byte[]	bytesJpg	= ( byte[] ) variables.get( Key.of( "bytesJpg" ) );
		String	base64Jpg	= ( String ) variables.get( Key.of( "base64Jpg" ) );

		assertThat( bytesJpg ).isNotNull();
		assertThat( bytesJpg.length ).isGreaterThan( JPEG_SIGNATURE.length );
		String bytesHeader = String.format(
		    "%02X %02X %02X %02X",
		    bytesJpg[ 0 ] & 0xFF,
		    bytesJpg[ 1 ] & 0xFF,
		    bytesJpg[ 2 ] & 0xFF,
		    bytesJpg[ 3 ] & 0xFF
		);
		for ( int i = 0; i < JPEG_SIGNATURE.length; i++ ) {
			assertWithMessage( "Expected JPEG signature; got header: %s", bytesHeader )
			    .that( bytesJpg[ i ] )
			    .isEqualTo( JPEG_SIGNATURE[ i ] );
		}

		assertThat( base64Jpg ).isNotNull();
		assertThat( base64Jpg ).isNotEmpty();
		byte[] decoded = Base64.getDecoder().decode( base64Jpg );
		assertThat( decoded.length ).isGreaterThan( JPEG_SIGNATURE.length );
		String decodedHeader = String.format(
		    "%02X %02X %02X %02X",
		    decoded[ 0 ] & 0xFF,
		    decoded[ 1 ] & 0xFF,
		    decoded[ 2 ] & 0xFF,
		    decoded[ 3 ] & 0xFF
		);
		for ( int i = 0; i < JPEG_SIGNATURE.length; i++ ) {
			assertWithMessage( "Expected decoded JPEG signature; got header: %s", decodedHeader )
			    .that( decoded[ i ] )
			    .isEqualTo( JPEG_SIGNATURE[ i ] );
		}
	}

	@DisplayName( "BoxImage uses JPG as default when source is .jpg" )
	@Test
	public void testToByteArrayAndBase64StringDefaultUsesJpgWhenSourceIsJpg() {
		runtime.executeSource(
		    """
		    src = "src/test/resources/generated/toByteArray-default.jpg";
		    img = ImageNew( "", 40, 40, "rgb", "white" );
		    ImageWrite( img, src );
		    imgJpg = ImageRead( src );

		    bytesDefault = imgJpg.toByteArray();
		    base64Default = imgJpg.toBase64String();
		    """,
		    context );

		byte[]	bytesDefault	= ( byte[] ) variables.get( Key.of( "bytesDefault" ) );
		String	base64Default	= ( String ) variables.get( Key.of( "base64Default" ) );

		assertThat( bytesDefault ).isNotNull();
		assertThat( bytesDefault.length ).isGreaterThan( JPEG_SIGNATURE.length );
		for ( int i = 0; i < JPEG_SIGNATURE.length; i++ ) {
			assertThat( bytesDefault[ i ] ).isEqualTo( JPEG_SIGNATURE[ i ] );
		}

		assertThat( base64Default ).isNotNull();
		assertThat( base64Default ).isNotEmpty();
		byte[] decoded = Base64.getDecoder().decode( base64Default );
		assertThat( decoded.length ).isGreaterThan( JPEG_SIGNATURE.length );
		for ( int i = 0; i < JPEG_SIGNATURE.length; i++ ) {
			assertThat( decoded[ i ] ).isEqualTo( JPEG_SIGNATURE[ i ] );
		}
	}
}
