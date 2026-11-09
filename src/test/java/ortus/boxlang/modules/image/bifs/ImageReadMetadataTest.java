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

public class ImageReadMetadataTest extends BaseIntegrationTest {

	private static final String	SOURCE	= "src/test/resources/test-images/exif-test.jpg";
	private static final String	XMP		= "<x:xmpmeta xmlns:x='adobe:ns:meta/'><rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns##'>"
	    + "<rdf:Description rdf:about='' xmlns:xmp='http://ns.adobe.com/xap/1.0/' xmp:CreatorTool='bx-image-test'/></rdf:RDF></x:xmpmeta>";

	@DisplayName( "It reads metadata from a path without decoding the image" )
	@Test
	public void testRead() {
		// @formatter:off
		runtime.executeSource( """
			meta = ImageReadMetadata( "%s" );
			software = meta.getExifTag( "Software" );
			softwareBySpecName = meta.getExifTag( "software" );
			types = meta.getTypes();
			format = meta.getFormat();
			exif = meta.getExif();
			hasSoftwareKey = structKeyExists( exif, "Software" );
			changed = meta.hasChanges();
			missing = isNull( meta.getExifTag( "Artist" ) );
		""".formatted( SOURCE ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "software" ) ) ).isEqualTo( "GIMP 2.10.34" );
		assertThat( variables.get( Key.of( "softwareBySpecName" ) ) ).isEqualTo( "GIMP 2.10.34" );
		assertThat( ( Array ) variables.get( Key.of( "types" ) ) ).contains( "exif" );
		assertThat( variables.get( Key.of( "format" ) ) ).isEqualTo( "jpg" );
		assertThat( variables.get( Key.of( "hasSoftwareKey" ) ) ).isEqualTo( true );
		assertThat( variables.get( Key.of( "changed" ) ) ).isEqualTo( false );
		assertThat( variables.get( Key.of( "missing" ) ) ).isEqualTo( true );
	}

	@DisplayName( "It writes EXIF, IPTC and XMP to a copy, keeps the existing EXIF and the pixels" )
	@Test
	public void testEditAndWriteCopy() {
		String destination = "src/test/resources/generated/metadata-copy.jpg";
		// @formatter:off
		runtime.executeSource( """
			meta = ImageReadMetadata( "%s" )
				.setExifTag( "Artist", "Jane Tester" )
				.setExifTag( "Date/Time Original", createDateTime( 2026, 11, 9, 14, 30, 0 ) )
				.setExifTag( "ISO", 400 )
				.setExifTag( "FNumber", 2.8 )
				.setExifTag( "GPSLatitude", 52.503472 )
				.setExifTag( "GPSLongitude", 13.41 )
				.setIPTCTag( "Keywords", [ "garden", "spring" ] )
				.setIPTCTag( "Copyright Notice", "(c) 2026 akitogo" )
				.setIPTCTag( "Caption/Abstract", "Grüße aus Köln" )
				.setXMP( "%s" );
			changed = meta.hasChanges();
			pendingArtist = meta.getExifTag( "Artist" );
			pendingKeywords = meta.getIPTCTag( "Keywords" );
			meta.write( "%s" );

			check = ImageReadMetadata( "%s" );
			artist = check.getExifTag( "Artist" );
			dateTimeOriginal = check.getExifTag( "Date/Time Original" );
			iso = ImageGetExifTag( "%s", "ISO Speed Ratings" );
			fnumber = check.getExifTag( "F-Number" );
			latitude = check.getExifTag( "GPS Latitude" );
			latitudeRef = check.getExifTag( "GPS Latitude Ref" );
			software = check.getExifTag( "Software" );
			keywords = check.getIPTCTag( "Keywords" );
			copyright = ImageGetIPTCTag( "%s", "Copyright Notice" );
			caption = ImageGetIPTCMetadata( "%s" )[ "Caption/Abstract" ];
			xmp = ImageGetXMP( "%s" );
			types = check.getTypes();
			samePixels = ImageWriteBase64( imageRead( "%s" ), "png" ) == ImageWriteBase64( imageRead( "%s" ), "png" );
		""".formatted( SOURCE, XMP, destination, destination, destination, destination, destination, destination, SOURCE, destination ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "changed" ) ) ).isEqualTo( true );
		assertThat( variables.get( Key.of( "pendingArtist" ) ) ).isEqualTo( "Jane Tester" );
		assertThat( variables.get( Key.of( "artist" ) ) ).isEqualTo( "Jane Tester" );
		assertThat( variables.get( Key.of( "dateTimeOriginal" ) ) ).isEqualTo( "2026:11:09 14:30:00" );
		assertThat( variables.get( Key.of( "iso" ) ) ).isEqualTo( "400" );
		assertThat( ( String ) variables.get( Key.of( "fnumber" ) ) ).matches( "f/2[.,]8" ); // decimal separator follows the JVM locale
		assertThat( variables.get( Key.of( "latitudeRef" ) ) ).isEqualTo( "N" );
		assertThat( ( String ) variables.get( Key.of( "latitude" ) ) ).startsWith( "52°" );
		assertThat( variables.get( Key.of( "software" ) ) ).isEqualTo( "GIMP 2.10.34" );
		assertThat( ( String ) variables.get( Key.of( "keywords" ) ) ).contains( "garden" );
		assertThat( ( String ) variables.get( Key.of( "keywords" ) ) ).contains( "spring" );
		assertThat( variables.get( Key.of( "copyright" ) ) ).isEqualTo( "(c) 2026 akitogo" );
		assertThat( variables.get( Key.of( "caption" ) ) ).isEqualTo( "Grüße aus Köln" );
		assertThat( ( String ) variables.get( Key.of( "xmp" ) ) ).contains( "bx-image-test" );
		assertThat( ( Array ) variables.get( Key.of( "types" ) ) ).containsAtLeast( "exif", "iptc", "xmp" );
		assertThat( variables.get( Key.of( "samePixels" ) ) ).isEqualTo( true );
	}

	@DisplayName( "It rewrites a file in place and works as a BIF on a handle" )
	@Test
	public void testWriteInPlace() throws IOException {
		String file = "src/test/resources/generated/metadata-inplace.jpg";
		Files.copy( Paths.get( SOURCE ), Paths.get( file ), StandardCopyOption.REPLACE_EXISTING );
		// @formatter:off
		runtime.executeSource( """
			meta = ImageReadMetadata( "%s" );
			ImageSetExifTag( meta, "Copyright", "(c) akitogo" );
			ImageSetIPTCTag( meta, "By-line", "Jane Tester" );
			meta.write();
			copyright = ImageGetExifTag( "%s", "Copyright" );
			byline = ImageGetIPTCTag( "%s", "By-line" );
			tmpFiles = directoryList( "src/test/resources/generated", false, "name", "*.tmp" );
		""".formatted( file, file, file ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "copyright" ) ) ).isEqualTo( "(c) akitogo" );
		assertThat( variables.get( Key.of( "byline" ) ) ).isEqualTo( "Jane Tester" );
		assertThat( ( Array ) variables.get( Key.of( "tmpFiles" ) ) ).isEmpty();
	}

	@DisplayName( "toBinary() returns the image with the new metadata" )
	@Test
	public void testToBinary() {
		// @formatter:off
		runtime.executeSource( """
			meta = ImageReadMetadata( "%s" ).setExifTag( "Artist", "Binary Jane" );
			bin = meta.toBinary();
			isBin = isBinary( bin );
			img = imageReadBase64( toBase64( bin ) );
			width = img.getWidth();
			artist = img.getExifTag( "Artist" );
		""".formatted( SOURCE ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "isBin" ) ) ).isEqualTo( true );
		assertThat( ( int ) variables.get( Key.of( "width" ) ) ).isGreaterThan( 0 );
		assertThat( variables.get( Key.of( "artist" ) ) ).isEqualTo( "Binary Jane" );
	}

	@DisplayName( "It removes single tags and whole types" )
	@Test
	public void testRemove() {
		String destination = "src/test/resources/generated/metadata-removed.jpg";
		// @formatter:off
		runtime.executeSource( """
			ImageReadMetadata( "%s" )
				.setIPTCTag( "Keywords", [ "keep" ] )
				.removeExifTag( "Software" )
				.write( "%s" );
			exif = ImageGetExifMetaData( "%s" );
			hasSoftware = structKeyExists( exif, "Software" );
			hasKeywords = structKeyExists( ImageGetIPTCMetadata( "%s" ), "Keywords" );

			ImageRemoveMetadata( "%s", "iptc" );
			typesAfter = ImageReadMetadata( "%s" ).getTypes();
		""".formatted( SOURCE, destination, destination, destination, destination, destination ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "hasSoftware" ) ) ).isEqualTo( false );
		assertThat( variables.get( Key.of( "hasKeywords" ) ) ).isEqualTo( true );
		Array types = ( Array ) variables.get( Key.of( "typesAfter" ) );
		assertThat( types ).contains( "exif" );
		assertThat( types ).doesNotContain( "iptc" );
	}

	@DisplayName( "It rejects unknown tags, bad values and unsupported formats with clear errors" )
	@Test
	public void testErrors() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                                              ImageReadMetadata( "%s" ).setExifTag( "NoSuchTag", "x" );
		                                                                                              """.formatted( SOURCE ), context ) );
		assertThat( e.getMessage() ).contains( "NoSuchTag" );

		e = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                          ImageReadMetadata( "%s" ).setExifTag( "FNumber", "fast" );
		                                                                          """.formatted( SOURCE ), context ) );
		assertThat( e.getMessage() ).contains( "FNumber" );

		e = assertThrows( BoxRuntimeException.class,
		    () -> runtime.executeSource( """
		                                 ImageReadMetadata( "src/test/resources/logo.png" ).setExifTag( "Artist", "x" );
		                                 """, context ) );
		assertThat( e.getMessage() ).contains( "EXIF" );

		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                      ImageReadMetadata( "src/test/resources/does-not-exist.jpg" );
		                                                                      """, context ) );
	}

	@DisplayName( "PNG takes XMP but no EXIF" )
	@Test
	public void testPng() {
		String destination = "src/test/resources/generated/metadata-xmp.png";
		// @formatter:off
		runtime.executeSource( """
			meta = ImageReadMetadata( "src/test/resources/logo.png" );
			canExif = meta.canWrite( "exif" );
			canXmp = meta.canWrite( "xmp" );
			meta.setXMP( "%s" ).write( "%s" );
			xmp = ImageGetXMP( "%s" );
			width = imageRead( "%s" ).getWidth();
		""".formatted( XMP, destination, destination, destination ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "canExif" ) ) ).isEqualTo( false );
		assertThat( variables.get( Key.of( "canXmp" ) ) ).isEqualTo( true );
		assertThat( ( String ) variables.get( Key.of( "xmp" ) ) ).contains( "bx-image-test" );
		assertThat( ( int ) variables.get( Key.of( "width" ) ) ).isGreaterThan( 0 );
	}
}
