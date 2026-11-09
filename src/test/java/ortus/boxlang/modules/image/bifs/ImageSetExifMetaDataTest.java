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

public class ImageSetExifMetaDataTest extends BaseIntegrationTest {

	private static final String SOURCE = "src/test/resources/test-images/exif-test.jpg";

	private static String copy( String name ) throws IOException {
		String file = "src/test/resources/generated/" + name;
		Files.copy( Paths.get( SOURCE ), Paths.get( file ), StandardCopyOption.REPLACE_EXISTING );
		return file;
	}

	@DisplayName( "BIF form on a file path writes all tags of the struct in place" )
	@Test
	public void testBifOnPath() throws IOException {
		String file = copy( "exif-struct-path.jpg" );
		// @formatter:off
		runtime.executeSource( """
			ImageSetExifMetaData( "%s", { "Artist": "Struct Jane", "Copyright": "(c) struct", "ISO": 1600 } );
			artist = ImageGetExifTag( "%s", "Artist" );
			copyright = ImageGetExifTag( "%s", "Copyright" );
			iso = ImageGetExifTag( "%s", "ISO Speed Ratings" );
			software = ImageGetExifTag( "%s", "Software" );
		""".formatted( file, file, file, file, file ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "artist" ) ) ).isEqualTo( "Struct Jane" );
		assertThat( variables.get( Key.of( "copyright" ) ) ).isEqualTo( "(c) struct" );
		assertThat( variables.get( Key.of( "iso" ) ) ).isEqualTo( "1600" );
		assertThat( variables.get( Key.of( "software" ) ) ).isEqualTo( "GIMP 2.10.34" );
	}

	@DisplayName( "Member form on an image and on a handle; a null value removes a tag" )
	@Test
	public void testMembers() throws IOException {
		String	file	= copy( "exif-struct-handle.jpg" );
		String	out		= "src/test/resources/generated/exif-struct-image.jpg";
		// @formatter:off
		runtime.executeSource( """
			imageRead( "%s" ).setExifMetaData( { "Artist": "Image Jane" } ).write( "%s" );
			imageArtist = ImageGetExifTag( "%s", "Artist" );

			meta = ImageReadMetadata( "%s" );
			meta.setExifMetaData( { "Artist": "Handle Jane", "Software": javaCast( "null", "" ) } ).write();
			handleArtist = ImageGetExifTag( "%s", "Artist" );
			hasSoftware = structKeyExists( ImageGetExifMetaData( "%s" ), "Software" );
		""".formatted( SOURCE, out, out, file, file, file ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "imageArtist" ) ) ).isEqualTo( "Image Jane" );
		assertThat( variables.get( Key.of( "handleArtist" ) ) ).isEqualTo( "Handle Jane" );
		assertThat( variables.get( Key.of( "hasSoftware" ) ) ).isEqualTo( false );
	}

	@DisplayName( "An unknown tag in the struct fails and changes nothing" )
	@Test
	public void testUnknownTag() throws IOException {
		String file = copy( "exif-struct-bad.jpg" );
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                      ImageSetExifMetaData( "%s", { "NoSuchTag": "x" } );
		                                                                      """.formatted( file ), context ) );
		runtime.executeSource( "software = ImageGetExifTag( \"%s\", \"Software\" );".formatted( file ), context );
		assertThat( variables.get( Key.of( "software" ) ) ).isEqualTo( "GIMP 2.10.34" );
	}
}
