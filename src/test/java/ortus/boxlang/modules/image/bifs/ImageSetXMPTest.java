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

public class ImageSetXMPTest extends BaseIntegrationTest {

	private static final String	SOURCE	= "src/test/resources/test-images/exif-test.jpg";
	private static final String	XMP		= "<x:xmpmeta xmlns:x='adobe:ns:meta/'><rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns##'>"
	    + "<rdf:Description rdf:about='' xmlns:xmp='http://ns.adobe.com/xap/1.0/' xmp:CreatorTool='bx-image-xmp-test'/></rdf:RDF></x:xmpmeta>";

	@DisplayName( "BIF form on a path, member form on an image and a handle; empty removes the packet" )
	@Test
	public void testAllForms() throws IOException {
		String file = "src/test/resources/generated/xmp-path.jpg";
		Files.copy( Paths.get( SOURCE ), Paths.get( file ), StandardCopyOption.REPLACE_EXISTING );
		String out = "src/test/resources/generated/xmp-image.jpg";
		// @formatter:off
		runtime.executeSource( """
			ImageSetXMP( "%s", "%s" );
			pathXmp = ImageGetXMP( "%s" );
			typesWith = ImageReadMetadata( "%s" ).getTypes();

			ImageSetXMP( "%s", "" );
			typesWithout = ImageReadMetadata( "%s" ).getTypes();

			imageRead( "%s" ).setXMP( "%s" ).write( "%s" );
			imageXmp = ImageGetXMP( "%s" );

			handleXmp = ImageReadMetadata( "%s" ).setXMP( "%s" ).getXMP();
		""".formatted( file, XMP, file, file, file, file, SOURCE, XMP, out, out, SOURCE, XMP ), context );
		// @formatter:on
		assertThat( ( String ) variables.get( Key.of( "pathXmp" ) ) ).contains( "bx-image-xmp-test" );
		assertThat( ( Array ) variables.get( Key.of( "typesWith" ) ) ).contains( "xmp" );
		assertThat( ( Array ) variables.get( Key.of( "typesWithout" ) ) ).doesNotContain( "xmp" );
		assertThat( ( String ) variables.get( Key.of( "imageXmp" ) ) ).contains( "bx-image-xmp-test" );
		assertThat( ( String ) variables.get( Key.of( "handleXmp" ) ) ).contains( "bx-image-xmp-test" );
	}

	@DisplayName( "Malformed XML is rejected" )
	@Test
	public void testInvalidXml() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                                              ImageReadMetadata( "%s" ).setXMP( "<not xml" );
		                                                                                              """.formatted( SOURCE ), context ) );
		assertThat( e.getMessage() ).contains( "XMP" );
	}
}
