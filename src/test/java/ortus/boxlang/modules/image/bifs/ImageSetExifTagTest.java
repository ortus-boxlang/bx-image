package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

/**
 * The setter family on image objects: metadata is stamped onto the encoded bytes when the image
 * is written, the default write() stays unchanged.
 */
public class ImageSetExifTagTest extends BaseIntegrationTest {

	private static final String	SOURCE	= "src/test/resources/test-images/exif-test.jpg";
	private static final String	XMP		= "<x:xmpmeta xmlns:x='adobe:ns:meta/'><rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns##'>"
	    + "<rdf:Description rdf:about='' xmlns:xmp='http://ns.adobe.com/xap/1.0/' xmp:CreatorTool='bx-image-test'/></rdf:RDF></x:xmpmeta>";

	@DisplayName( "Tags set on an image are written with the image; source metadata is not carried over" )
	@Test
	public void testWriteWithMetadata() {
		String destination = "src/test/resources/generated/setters.jpg";
		// @formatter:off
		runtime.executeSource( """
			img = imageRead( "%s" ).resize( 40, 40 )
				.setExifTag( "Artist", "Jane Tester" )
				.setExifTag( "Copyright", "(c) 2026" )
				.setIPTCTag( "Keywords", [ "resized", "test" ] )
				.setXMP( "%s" );
			pendingArtist = img.getExifTag( "Artist" );
			pendingInStruct = img.getExifMetaData()[ "Artist" ];
			pendingXmp = img.getXMP();
			img.write( "%s" );

			artist = ImageGetExifTag( "%s", "Artist" );
			copyright = ImageGetExifTag( "%s", "Copyright" );
			keywords = ImageGetIPTCTag( "%s", "Keywords" );
			xmp = ImageGetXMP( "%s" );
			hasSoftware = structKeyExists( ImageGetExifMetaData( "%s" ), "Software" );
			width = imageRead( "%s" ).getWidth();
		""".formatted( SOURCE, XMP, destination, destination, destination, destination, destination, destination, destination ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "pendingArtist" ) ) ).isEqualTo( "Jane Tester" );
		assertThat( variables.get( Key.of( "pendingInStruct" ) ) ).isEqualTo( "Jane Tester" );
		assertThat( ( String ) variables.get( Key.of( "pendingXmp" ) ) ).contains( "bx-image-test" );
		assertThat( variables.get( Key.of( "artist" ) ) ).isEqualTo( "Jane Tester" );
		assertThat( variables.get( Key.of( "copyright" ) ) ).isEqualTo( "(c) 2026" );
		assertThat( ( String ) variables.get( Key.of( "keywords" ) ) ).contains( "resized" );
		assertThat( ( String ) variables.get( Key.of( "xmp" ) ) ).contains( "bx-image-test" );
		assertThat( variables.get( Key.of( "hasSoftware" ) ) ).isEqualTo( false );
		assertThat( ( int ) variables.get( Key.of( "width" ) ) ).isEqualTo( 40 );
	}

	@DisplayName( "A plain write() without setters produces the same file as before" )
	@Test
	public void testDefaultUnchanged() {
		// @formatter:off
		runtime.executeSource( """
			img = imageRead( "%s" ).resize( 40, 40 );
			plain = ImageWriteBase64( img, "jpg" );
			img2 = imageRead( "%s" ).resize( 40, 40 );
			img2.setExifTag( "Artist", "x" ).removeMetadata( "all" );
			reverted = ImageWriteBase64( img2, "jpg" );
			same = plain == reverted;
		""".formatted( SOURCE, SOURCE ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "same" ) ) ).isEqualTo( true );
	}

	@DisplayName( "BIF form, struct setters and Base64 output" )
	@Test
	public void testBifAndStructAndBase64() {
		// @formatter:off
		runtime.executeSource( """
			img = imageRead( "%s" );
			ImageSetExifMetaData( img, { "Artist": "Struct Jane", "ISO": 800 } );
			ImageSetIPTCMetaData( img, { "Copyright Notice": "(c) struct", "Keywords": [ "a", "b" ] } );
			ImageSetXMP( img, "%s" );
			b64 = ImageWriteBase64( img, "jpg" );
			back = imageReadBase64( b64 );
			artist = back.getExifTag( "Artist" );
			iso = back.getExifTag( "ISO Speed Ratings" );
			copyright = back.getIPTCTag( "Copyright Notice" );
		""".formatted( SOURCE, XMP ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "artist" ) ) ).isEqualTo( "Struct Jane" );
		assertThat( variables.get( Key.of( "iso" ) ) ).isEqualTo( "800" );
		assertThat( variables.get( Key.of( "copyright" ) ) ).isEqualTo( "(c) struct" );
	}

	@DisplayName( "Setting a tag on a file path rewrites the file in place" )
	@Test
	public void testPathInPlace() throws Exception {
		String file = "src/test/resources/generated/setters-inplace.jpg";
		java.nio.file.Files.copy( java.nio.file.Paths.get( SOURCE ), java.nio.file.Paths.get( file ),
		    java.nio.file.StandardCopyOption.REPLACE_EXISTING );
		// @formatter:off
		runtime.executeSource( """
			ImageSetExifTag( "%s", "Artist", "Path Jane" );
			ImageSetXMP( "%s", "%s" );
			artist = ImageGetExifTag( "%s", "Artist" );
			software = ImageGetExifTag( "%s", "Software" );
			xmp = ImageGetXMP( "%s" );
		""".formatted( file, file, XMP, file, file, file ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "artist" ) ) ).isEqualTo( "Path Jane" );
		assertThat( variables.get( Key.of( "software" ) ) ).isEqualTo( "GIMP 2.10.34" );
		assertThat( ( String ) variables.get( Key.of( "xmp" ) ) ).contains( "bx-image-test" );
	}

	@DisplayName( "Writing EXIF into a PNG fails with a clear error and writes nothing" )
	@Test
	public void testUnsupportedFormat() {
		String destination = "src/test/resources/generated/setters-unsupported.png";
		new java.io.File( destination ).delete();
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class,
		    () -> runtime.executeSource( """
		                                 imageRead( "%s" ).setExifTag( "Artist", "x" ).write( "%s" );
		                                 """.formatted( SOURCE, destination ), context ) );
		assertThat( e.getMessage() ).contains( "EXIF" );
		assertThat( new java.io.File( destination ).exists() ).isFalse();

		// XMP is fine in a PNG
		// @formatter:off
		runtime.executeSource( """
			imageRead( "%s" ).setXMP( "%s" ).write( "%s" );
			xmp = ImageGetXMP( "%s" );
		""".formatted( SOURCE, XMP, destination, destination ), context );
		// @formatter:on
		assertThat( ( String ) variables.get( Key.of( "xmp" ) ) ).contains( "bx-image-test" );
	}
}
