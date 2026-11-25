package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Array;

public class ImageStripMetadataTest extends BaseIntegrationTest {

	private static final String SOURCE = "src/test/resources/test-images/exif-test.jpg";

	@DisplayName( "It strips all metadata into a copy and keeps the pixels" )
	@Test
	public void testStripToCopy() {
		String destination = "src/test/resources/generated/stripped.jpg";
		// @formatter:off
		runtime.executeSource( """
			ImageStripMetadata( "%s", "%s" );
			types = ImageReadMetadata( "%s" ).getTypes();
			hasSoftware = structKeyExists( ImageGetExifMetaData( "%s" ), "Software" );
			samePixels = ImageWriteBase64( imageRead( "%s" ), "png" ) == ImageWriteBase64( imageRead( "%s" ), "png" );
		""".formatted( SOURCE, destination, destination, destination, SOURCE, destination ), context );
		// @formatter:on
		assertThat( ( Array ) variables.get( Key.of( "types" ) ) ).isEmpty();
		assertThat( variables.get( Key.of( "hasSoftware" ) ) ).isEqualTo( false );
		assertThat( variables.get( Key.of( "samePixels" ) ) ).isEqualTo( true );
	}

	@DisplayName( "It strips in place and can be limited to some types" )
	@Test
	public void testStripInPlaceSelected() throws IOException {
		String file = "src/test/resources/generated/stripped-inplace.jpg";
		Files.copy( Paths.get( SOURCE ), Paths.get( file ), StandardCopyOption.REPLACE_EXISTING );
		// @formatter:off
		runtime.executeSource( """
			ImageSetIPTCTag( "%s", "Keywords", [ "privacy" ] );
			typesBefore = ImageReadMetadata( "%s" ).getTypes();
			ImageStripMetadata( "%s", "", "exif" );
			typesAfter = ImageReadMetadata( "%s" ).getTypes();
			ImageStripMetadata( "%s" );
			typesFinal = ImageReadMetadata( "%s" ).getTypes();
		""".formatted( file, file, file, file, file, file ), context );
		// @formatter:on
		assertThat( ( Array ) variables.get( Key.of( "typesBefore" ) ) ).containsAtLeast( "exif", "iptc" );
		Array after = ( Array ) variables.get( Key.of( "typesAfter" ) );
		assertThat( after ).contains( "iptc" );
		assertThat( after ).doesNotContain( "exif" );
		assertThat( ( Array ) variables.get( Key.of( "typesFinal" ) ) ).isEmpty();
	}
}
