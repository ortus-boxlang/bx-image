package ortus.boxlang.modules.image.components;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.compiler.parser.BoxSourceType;
import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;

public class ImageTest extends BaseIntegrationTest {

	@DisplayName( "It should add a border" )
	@Test
	public void testDrawBeveledRect() throws IOException {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="border" source="src/test/resources/logo.png" thickness=5 color="red" name="theImage" />
			<bx:image action="write" source="#theImage#" destination="src/test/resources/generated/logo-component-border.png" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-component-border.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-component-border.png" ) );

		// assertThat( true ).isTrue();
		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testResize() throws IOException {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="resize" source="src/test/resources/logo.png" width="1024" height="385" name="theImage" />
			<bx:image action="write" source="#theImage#" destination="src/test/resources/generated/logo-component-resize.png" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-component-resize.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-component-resize.png" ) );

		// assertThat( true ).isTrue();
		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testConvert() throws IOException {
		try {
			FileSystemUtil.deleteFile( "src/test/resources/generated/logo.jpg" );
		} catch ( Exception e ) {
			// pass
		}
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="convert" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo.jpg" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		assertThat( FileSystemUtil.exists( "src/test/resources/generated/logo.jpg" ) ).isTrue();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testConvertWithOverwrite() throws IOException {
		try {
			FileSystemUtil.deleteFile( "src/test/resources/generated/logo-overwrite.jpg" );
		} catch ( Exception e ) {
			// pass
		}
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="convert" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo-overwrite.jpg" />
			<bx:image action="convert" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo-overwrite.jpg" overwrite=true />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		assertThat( FileSystemUtil.exists( "src/test/resources/generated/logo-overwrite.jpg" ) ).isTrue();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testConvertWithNoOverwrite() throws IOException {
		try {
			FileSystemUtil.deleteFile( "src/test/resources/generated/logo-overwrite-fail.jpg" );
		} catch ( Exception e ) {
			// pass
		}

		assertThrows( BoxRuntimeException.class, () -> {
			// @formatter:off
			runtime.executeSource( """
				<bx:image action="convert" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo-overwrite-fail.jpg" />
				<bx:image action="convert" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo-overwrite-fail.jpg" />
			""", context, BoxSourceType.BOXTEMPLATE );
			// @formatter:on
		} );
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testInfo() throws IOException {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="info" source="src/test/resources/logo.png" structName="imageInfo" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		IStruct info = variables.getAsStruct( Key.of( "imageInfo" ) );

		assertThat( info ).isNotNull();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testRotate() throws IOException {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="rotate" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo-component-rotated.png" angle=45 overwrite=true />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-component-rotated.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-component-rotated.png" ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testRead() throws IOException {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="read" source="src/test/resources/logo.png" name="theImage" />
			<bx:set result = theImage.$bx.$class.name />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		assertThat( variables.get( result ) ).isEqualTo( "ortus.boxlang.modules.image.BoxImage" );
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testWriteToBrowser() throws IOException {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="read" source="src/test/resources/logo.png" name="theImage" />
			<bx:image action="writeToBrowser" name="theImage" source="src/test/resources/logo.png"/>
			<bx:set result = theImage.$bx.$class.name />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		assertThat( variables.get( result ) ).isEqualTo( "ortus.boxlang.modules.image.BoxImage" );
	}

	@DisplayName( "It generates a QR code and reads it back" )
	@Test
	public void testQRCodeAndRead() {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="qrcode" text="https://example.com/component" size="250" format="png" errorCorrection="H" foreground="##1a237e" background="white" name="qr" />
			<bx:image action="qrread" source="#qr#" name="decoded" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		assertThat( variables.get( Key.of( "decoded" ) ) ).isEqualTo( "https://example.com/component" );
	}

	@DisplayName( "It writes a QR code to a destination and reads it from a path" )
	@Test
	public void testQRCodeDestination() {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="qrcode" text="to disk" destination="src/test/resources/generated/qr-component.png" overwrite=true />
			<bx:image action="qrread" source="src/test/resources/generated/qr-component.png" name="decoded" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		assertThat( variables.get( Key.of( "decoded" ) ) ).isEqualTo( "to disk" );
	}

	@DisplayName( "It draws a logo on a QR code" )
	@Test
	public void testQRCodeLogo() {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="qrcode" text="https://example.com/logo-qr" size="400" logo="src/test/resources/logo.png" name="qr" />
			<bx:image action="qrread" source="#qr#" name="decoded" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		assertThat( variables.get( Key.of( "decoded" ) ) ).isEqualTo( "https://example.com/logo-qr" );
	}

	@DisplayName( "It will not overwrite an existing QR code file unless told to" )
	@Test
	public void testQRCodeOverwrite() {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="qrcode" text="first" destination="src/test/resources/generated/qr-overwrite.png" overwrite=true />
		""", context, BoxSourceType.BOXTEMPLATE );

		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
			<bx:image action="qrcode" text="second" destination="src/test/resources/generated/qr-overwrite.png" />
		""", context, BoxSourceType.BOXTEMPLATE ) );
		// @formatter:on
	}

	@DisplayName( "It requires text for the qrcode action" )
	@Test
	public void testQRCodeRequiresText() {
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                      	<bx:image action="qrcode" name="qr" />
		                                                                      """, context, BoxSourceType.BOXTEMPLATE ) );
	}

	@DisplayName( "It requires a name for the qrread action" )
	@Test
	public void testQRReadRequiresName() {
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                      	<bx:image action="qrread" source="src/test/resources/logo.png" />
		                                                                      """, context, BoxSourceType.BOXTEMPLATE ) );
	}

	@DisplayName( "It returns an empty string from qrread when there is no QR code" )
	@Test
	public void testQRReadNoCode() {
		// @formatter:off
		runtime.executeSource( """
			<bx:image action="qrread" source="src/test/resources/logo.png" name="decoded" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		assertThat( variables.get( Key.of( "decoded" ) ) ).isEqualTo( "" );
	}

}
