package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Array;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class ImageGetXMPTest extends BaseIntegrationTest {

	private static final String	SOURCE	= "src/test/resources/test-images/exif-test.jpg";
	private static final String	XMP		= "<x:xmpmeta xmlns:x='adobe:ns:meta/'><rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns##'>"
	    + "<rdf:Description rdf:about='' xmlns:xmp='http://ns.adobe.com/xap/1.0/' xmp:CreatorTool='bx-image-getxmp'/></rdf:RDF></x:xmpmeta>";

	@DisplayName( "It returns an empty string when there is no XMP and the packet when there is" )
	@Test
	public void testGet() throws IOException {
		String file = "src/test/resources/generated/getxmp.jpg";
		Files.copy( Paths.get( SOURCE ), Paths.get( file ), StandardCopyOption.REPLACE_EXISTING );
		// @formatter:off
		runtime.executeSource( """
			none = ImageGetXMP( "src/test/resources/logo.png" );
			imageNone = imageRead( "%s" ).getXMP();
			ImageSetXMP( "%s", "%s" );
			fromPath = ImageGetXMP( "%s" );
			fromHandle = ImageReadMetadata( "%s" ).getXMP();
			fromHandleBif = ImageGetXMP( ImageReadMetadata( "%s" ) );
			fromImage = imageRead( "%s" ).setXMP( "%s" ).getXMP();
		""".formatted( file, file, XMP, file, file, file, SOURCE, XMP ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "none" ) ) ).isEqualTo( "" );
		assertThat( variables.get( Key.of( "imageNone" ) ) ).isEqualTo( "" );
		assertThat( ( String ) variables.get( Key.of( "fromPath" ) ) ).contains( "bx-image-getxmp" );
		assertThat( ( String ) variables.get( Key.of( "fromHandle" ) ) ).contains( "bx-image-getxmp" );
		assertThat( ( String ) variables.get( Key.of( "fromHandleBif" ) ) ).contains( "bx-image-getxmp" );
		assertThat( ( String ) variables.get( Key.of( "fromImage" ) ) ).contains( "bx-image-getxmp" );
	}
}
