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

public class ImageRemoveMetadataTest extends BaseIntegrationTest {

	private static final String SOURCE = "src/test/resources/test-images/exif-test.jpg";

	private static String prepared( String name ) throws IOException {
		String file = "src/test/resources/generated/" + name;
		Files.copy( Paths.get( SOURCE ), Paths.get( file ), StandardCopyOption.REPLACE_EXISTING );
		runtime.executeSource( "ImageSetIPTCTag( \"%s\", \"Keywords\", [ \"k\" ] );".formatted( file ),
		    new ortus.boxlang.runtime.context.ScriptingRequestBoxContext( runtime.getRuntimeContext() ) );
		return file;
	}

	@DisplayName( "BIF form on a path removes the given types in place, default is all" )
	@Test
	public void testBifOnPath() throws IOException {
		String file = prepared( "remove-path.jpg" );
		// @formatter:off
		runtime.executeSource( """
			before = ImageReadMetadata( "%s" ).getTypes();
			ImageRemoveMetadata( "%s", "exif" );
			afterExif = ImageReadMetadata( "%s" ).getTypes();
			ImageRemoveMetadata( "%s", [ "iptc", "photoshop" ] );
			afterList = ImageReadMetadata( "%s" ).getTypes();
			ImageRemoveMetadata( "%s" );
			afterAll = ImageReadMetadata( "%s" ).getTypes();
			width = imageRead( "%s" ).getWidth();
		""".formatted( file, file, file, file, file, file, file, file ), context );
		// @formatter:on
		assertThat( ( Array ) variables.get( Key.of( "before" ) ) ).containsAtLeast( "exif", "iptc" );
		Array afterExif = ( Array ) variables.get( Key.of( "afterExif" ) );
		assertThat( afterExif ).doesNotContain( "exif" );
		assertThat( afterExif ).contains( "iptc" );
		Array afterList = ( Array ) variables.get( Key.of( "afterList" ) );
		assertThat( afterList ).containsNoneOf( "exif", "iptc", "photoshop" );
		assertThat( afterList ).contains( "xmp" ); // the GIMP fixture also carries XMP and ICC, which were not asked for
		assertThat( ( Array ) variables.get( Key.of( "afterAll" ) ) ).isEmpty();
		assertThat( ( int ) variables.get( Key.of( "width" ) ) ).isGreaterThan( 0 );
	}

	@DisplayName( "Member form on a handle and default 'all'" )
	@Test
	public void testHandle() throws IOException {
		String file = prepared( "remove-handle.jpg" );
		// @formatter:off
		runtime.executeSource( """
			meta = ImageReadMetadata( "%s" );
			meta.removeMetadata( "iptc" );
			pendingTypes = meta.getTypes();
			meta.write();
			typesAfterIptc = ImageReadMetadata( "%s" ).getTypes();
			ImageRemoveMetadata( "%s" );
			typesAfterAll = ImageReadMetadata( "%s" ).getTypes();
		""".formatted( file, file, file, file ), context );
		// @formatter:on
		assertThat( ( Array ) variables.get( Key.of( "pendingTypes" ) ) ).doesNotContain( "iptc" );
		assertThat( ( Array ) variables.get( Key.of( "typesAfterIptc" ) ) ).contains( "exif" );
		assertThat( ( Array ) variables.get( Key.of( "typesAfterAll" ) ) ).isEmpty();
	}

	@DisplayName( "On an image it undoes earlier setters; an unknown type is rejected" )
	@Test
	public void testImageAndErrors() {
		// @formatter:off
		runtime.executeSource( """
			img = imageRead( "%s" ).setExifTag( "Artist", "x" ).setIPTCTag( "Keywords", [ "k" ] );
			img.removeMetadata( "exif" );
			artist = img.getExifTag( "Artist" );
			keywords = img.getIPTCTag( "Keywords" );
			ImageRemoveMetadata( img, "all" );
			b64 = ImageWriteBase64( img, "jpg" );
			plain = ImageWriteBase64( imageRead( "%s" ), "jpg" );
			same = b64 == plain;
		""".formatted( SOURCE, SOURCE ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "artist" ) ) ).isNull();
		assertThat( variables.get( Key.of( "keywords" ) ) ).isEqualTo( "k" );
		assertThat( variables.get( Key.of( "same" ) ) ).isEqualTo( true );

		BoxRuntimeException e = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                                              ImageReadMetadata( "%s" ).removeMetadata( "bogus" );
		                                                                                              """.formatted( SOURCE ), context ) );
		assertThat( e.getMessage() ).contains( "bogus" );
	}
}
