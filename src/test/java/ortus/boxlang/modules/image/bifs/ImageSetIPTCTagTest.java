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

public class ImageSetIPTCTagTest extends BaseIntegrationTest {

	private static final String SOURCE = "src/test/resources/test-images/exif-test.jpg";

	private static String copy( String name ) throws IOException {
		String file = "src/test/resources/generated/" + name;
		Files.copy( Paths.get( SOURCE ), Paths.get( file ), StandardCopyOption.REPLACE_EXISTING );
		return file;
	}

	@DisplayName( "BIF form on a file path: strings, arrays for repeatable tags, dates, any spelling" )
	@Test
	public void testBifOnPath() throws IOException {
		String file = copy( "iptc-tag-path.jpg" );
		// @formatter:off
		runtime.executeSource( """
			ImageSetIPTCTag( "%s", "Copyright Notice", "(c) 2026 akitogo" );
			ImageSetIPTCTag( "%s", "Keywords", [ "garden", "spring" ] );
			ImageSetIPTCTag( "%s", "CaptionAbstract", "Grüße aus Köln" );
			ImageSetIPTCTag( "%s", "Date Created", createDate( 2026, 11, 9 ) );
			iptc = ImageGetIPTCMetadata( "%s" );
			copyright = iptc[ "Copyright Notice" ];
			keywords = iptc[ "Keywords" ];
			caption = iptc[ "Caption/Abstract" ];
			dateCreated = ImageGetIPTCTag( "%s", "Date Created" );
			stillExif = ImageGetExifTag( "%s", "Software" );
		""".formatted( file, file, file, file, file, file, file ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "copyright" ) ) ).isEqualTo( "(c) 2026 akitogo" );
		assertThat( ( String ) variables.get( Key.of( "keywords" ) ) ).contains( "garden" );
		assertThat( ( String ) variables.get( Key.of( "keywords" ) ) ).contains( "spring" );
		assertThat( variables.get( Key.of( "caption" ) ) ).isEqualTo( "Grüße aus Köln" );
		assertThat( ( String ) variables.get( Key.of( "dateCreated" ) ) ).contains( "2026" );
		assertThat( variables.get( Key.of( "stillExif" ) ) ).isEqualTo( "GIMP 2.10.34" );
	}

	@DisplayName( "Member form on an image and on a handle; setting Keywords again replaces them" )
	@Test
	public void testMembers() throws IOException {
		String	file	= copy( "iptc-tag-handle.jpg" );
		String	out		= "src/test/resources/generated/iptc-tag-image.jpg";
		// @formatter:off
		runtime.executeSource( """
			imageRead( "%s" ).resize( 30, 30 ).setIPTCTag( "By-line", "Image Jane" ).write( "%s" );
			imageByline = ImageGetIPTCTag( "%s", "By-line" );

			meta = ImageReadMetadata( "%s" ).setIPTCTag( "Keywords", [ "a", "b" ] );
			meta.setIPTCTag( "Keywords", [ "only" ] ).write();
			keywords = ImageReadMetadata( "%s" ).getIPTCTag( "Keywords" );
		""".formatted( SOURCE, out, out, file, file ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "imageByline" ) ) ).isEqualTo( "Image Jane" );
		assertThat( variables.get( Key.of( "keywords" ) ) ).isEqualTo( "only" );
	}

	@DisplayName( "Several values for a non-repeatable tag and unknown tags are rejected" )
	@Test
	public void testErrors() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class,
		    () -> runtime.executeSource( """
		                                 ImageReadMetadata( "%s" ).setIPTCTag( "Object Name", [ "a", "b" ] );
		                                 """.formatted( SOURCE ), context ) );
		assertThat( e.getMessage() ).contains( "ObjectName" );
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                      ImageReadMetadata( "%s" ).setIPTCTag( "NoSuchTag", "x" );
		                                                                      """.formatted( SOURCE ), context ) );
	}
}
