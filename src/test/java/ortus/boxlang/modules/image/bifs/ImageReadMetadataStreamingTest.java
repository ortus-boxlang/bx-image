/**
 * [BoxLang]
 *
 * Copyright [2026] [Ortus Solutions, Corp]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

/**
 * A metadata handle on a local file streams it: reading, the struct getters (also with pending
 * changes) and writing work on files larger than {@code maxBytes}. Only what holds the whole image in
 * memory ({@code toBinary()}) keeps the limit. The test lowers {@code maxBytes} below the size of the
 * test image instead of creating a file over the 50 MB default.
 */
public class ImageReadMetadataStreamingTest extends BaseIntegrationTest {

	private static final String	SOURCE		= "src/test/resources/test-images/exif-test.jpg";
	private static final Key	MAX_BYTES	= Key.of( "maxBytes" );
	private static final long	TINY_LIMIT	= 1024L;

	private Object				previousLimit;

	/**
	 * Lowers maxBytes for each test; the test image (about 380 KB) is far above it.
	 */
	@BeforeEach
	public void lowerLimit() throws IOException {
		IStruct settings = moduleService.getModuleSettings( moduleName );
		previousLimit = settings.get( MAX_BYTES );
		settings.put( MAX_BYTES, TINY_LIMIT );
		assertThat( Files.size( Paths.get( SOURCE ) ) ).isGreaterThan( TINY_LIMIT );
	}

	/**
	 * Restores the module setting for the other tests.
	 */
	@AfterEach
	public void restoreLimit() {
		IStruct settings = moduleService.getModuleSettings( moduleName );
		if ( previousLimit == null ) {
			settings.remove( MAX_BYTES );
		} else {
			settings.put( MAX_BYTES, previousLimit );
		}
	}

	private static String copy( String name ) throws IOException {
		String file = "src/test/resources/generated/" + name;
		Files.createDirectories( Paths.get( file ).getParent() );
		Files.copy( Paths.get( SOURCE ), Paths.get( file ), StandardCopyOption.REPLACE_EXISTING );
		return file;
	}

	@DisplayName( "A local file above maxBytes is read, shows pending changes and is written in place and to a copy" )
	@Test
	public void testLocalFileIsStreamed() throws IOException {
		String	file	= copy( "streamed-in-place.jpg" );
		String	copy	= "src/test/resources/generated/streamed-copy.jpg";
		// @formatter:off
		runtime.executeSource( """
			meta = ImageReadMetadata( "%s" );
			software = meta.getExifTag( "Software" );
			xmp = ImageGetXMP( "%s" );
			meta.setExifTag( "Artist", "Streamed Jane" ).setIPTCTag( "Keywords", [ "river", "boat" ] );
			pendingArtist = meta.getExif()[ "Artist" ];
			pendingKeywords = meta.getIPTC()[ "Keywords" ];
			meta.write();
			meta.write( "%s" );
			artist = ImageGetExifTag( "%s", "Artist" );
			copiedArtist = ImageReadMetadata( "%s" ).getExifTag( "Artist" );
			keywords = ImageGetIPTCTag( "%s", "Keywords" );
			ImageSetIPTCTag( "%s", "By-line", "Path Jane" );
			byline = ImageGetIPTCTag( "%s", "By-line" );
		""".formatted( file, file, copy, file, copy, file, file, file ), context );
		// @formatter:on
		assertThat( variables.get( Key.of( "software" ) ) ).isEqualTo( "GIMP 2.10.34" );
		assertThat( variables.get( Key.of( "pendingArtist" ) ) ).isEqualTo( "Streamed Jane" );
		assertThat( ( String ) variables.get( Key.of( "pendingKeywords" ) ) ).contains( "river" );
		assertThat( variables.get( Key.of( "artist" ) ) ).isEqualTo( "Streamed Jane" );
		assertThat( variables.get( Key.of( "copiedArtist" ) ) ).isEqualTo( "Streamed Jane" );
		assertThat( ( String ) variables.get( Key.of( "keywords" ) ) ).contains( "boat" );
		assertThat( variables.get( Key.of( "byline" ) ) ).isEqualTo( "Path Jane" );
		// The image data is copied byte for byte: only the metadata segments grew
		long	sourceSize	= Files.size( Paths.get( SOURCE ) );
		long	copySize	= Files.size( Paths.get( copy ) );
		assertThat( copySize ).isGreaterThan( sourceSize - 4096 );
		assertThat( copySize ).isLessThan( sourceSize + 4096 );
	}

	@DisplayName( "toBinary() holds the image in memory and keeps the maxBytes limit" )
	@Test
	public void testToBinaryKeepsTheLimit() throws IOException {
		String				file	= copy( "streamed-binary.jpg" );
		BoxRuntimeException	e		= assertThrows( BoxRuntimeException.class,
		    () -> runtime.executeSource( "bytes = ImageReadMetadata( \"%s\" ).toBinary();".formatted( file ), context ) );
		assertThat( rootMessage( e ) ).contains( "maxBytes" );
	}

	private static String rootMessage( Throwable t ) {
		Throwable root = t;
		while ( root.getCause() != null && root.getCause() != root ) {
			root = root.getCause();
		}
		return root.getMessage() == null ? t.getMessage() : root.getMessage();
	}
}
