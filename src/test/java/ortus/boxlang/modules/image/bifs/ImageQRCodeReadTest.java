package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageQRCodeReadTest extends BaseIntegrationTest {

	@DisplayName( "It reads a QR code from an image" )
	@Test
	public void testRead() {
		runtime.executeSource( """
		                       qr = ImageQRCodeGenerate( "read me", 250 );
		                       result = ImageQRCodeRead( qr );
		                       """, context );

		assertThat( variables.get( result ) ).isEqualTo( "read me" );
	}

	@DisplayName( "It reads from a variable name like the other image functions" )
	@Test
	public void testReadByName() {
		runtime.executeSource( """
		                       qr = ImageQRCodeGenerate( "by name", 250 );
		                       result = ImageQRCodeRead( "qr" );
		                       """, context );

		assertThat( variables.get( result ) ).isEqualTo( "by name" );
	}

	@DisplayName( "It can be called as a member function" )
	@Test
	public void testMember() {
		runtime.executeSource( """
		                       result = ImageQRCodeGenerate( "member read", 250 ).readQRCode();
		                       """, context );

		assertThat( variables.get( result ) ).isEqualTo( "member read" );
	}

	@DisplayName( "It reads a QR code from an image file" )
	@Test
	public void testReadFromFile() {
		runtime.executeSource( """
		                       ImageWrite( ImageQRCodeGenerate( "from file", 300, "jpg", "H" ), "src/test/resources/generated/qr-read.jpg" );
		                       result = ImageRead( "src/test/resources/generated/qr-read.jpg" ).readQRCode();
		                       """, context );

		assertThat( variables.get( result ) ).isEqualTo( "from file" );
	}

	@DisplayName( "It returns an empty string when there is no QR code" )
	@Test
	public void testNoCode() {
		runtime.executeSource( """
		                       result = ImageRead( "src/test/resources/logo.png" ).readQRCode();
		                       """, context );

		assertThat( variables.get( result ) ).isEqualTo( "" );
	}

	@DisplayName( "It reads with tryHarder turned off" )
	@Test
	public void testTryHarderOff() {
		runtime.executeSource( """
		                       result = ImageQRCodeGenerate( "fast", 250 ).readQRCode( false );
		                       """, context );

		assertThat( variables.get( result ) ).isEqualTo( "fast" );
	}

	@DisplayName( "It does not read a light-on-dark code unless tryHarder is on" )
	@Test
	public void testInvertedNeedsTryHarder() {
		runtime.executeSource( """
		                       qr = ImageQRCodeGenerate( text="inverted", size=250, foreground="white", background="black" );
		                       hard = qr.readQRCode( true );
		                       easy = qr.readQRCode( false );
		                       """, context );

		assertThat( variables.get( ortus.boxlang.runtime.scopes.Key.of( "hard" ) ) ).isEqualTo( "inverted" );
		assertThat( variables.get( ortus.boxlang.runtime.scopes.Key.of( "easy" ) ) ).isEqualTo( "" );
	}

}
