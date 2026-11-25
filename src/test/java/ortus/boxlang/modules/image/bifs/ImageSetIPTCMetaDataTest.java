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

public class ImageSetIPTCMetaDataTest extends BaseIntegrationTest {

	private static final String SOURCE = "src/test/resources/test-images/exif-test.jpg";

	@DisplayName( "BIF form on a path and member forms on an image and a handle" )
	@Test
	public void testAllForms() throws IOException {
		String file = "src/test/resources/generated/iptc-struct-path.jpg";
		Files.copy( Paths.get( SOURCE ), Paths.get( file ), StandardCopyOption.REPLACE_EXISTING );
		String out = "src/test/resources/generated/iptc-struct-image.jpg";
		// @formatter:off
		runtime.executeSource( """
			ImageSetIPTCMetaData( "%s", { "By-line": "Path Jane", "Keywords": [ "x", "y" ], "City": "Köln" } );
			iptc = ImageGetIPTCMetadata( "%s" );
			byline = iptc[ "By-line" ];
			city = iptc[ "City" ];
			keywords = iptc[ "Keywords" ];

			imageRead( "%s" ).setIPTCMetaData( { "Copyright Notice": "(c) image" } ).write( "%s" );
			imageCopyright = ImageGetIPTCTag( "%s", "Copyright Notice" );

			handleCredit = ImageReadMetadata( "%s" ).setIPTCMetaData( { "Credit": "Handle" } ).getIPTCTag( "Credit" );
		""".formatted( file, file, SOURCE, out, out, file ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "byline" ) ) ).isEqualTo( "Path Jane" );
		assertThat( variables.get( Key.of( "city" ) ) ).isEqualTo( "Köln" );
		assertThat( ( String ) variables.get( Key.of( "keywords" ) ) ).contains( "x" );
		assertThat( variables.get( Key.of( "imageCopyright" ) ) ).isEqualTo( "(c) image" );
		assertThat( variables.get( Key.of( "handleCredit" ) ) ).isEqualTo( "Handle" );
	}
}
