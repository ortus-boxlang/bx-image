/**
 * [BoxLang]
 *
 * Copyright [2024] [Ortus Solutions, Corp]
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
package ortus.boxlang.modules.image;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.util.ImageGuard;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class ImageGuardTest extends BaseIntegrationTest {

	// 8000 x 8000 = 64 Mpx, over the 50 Mpx default, but 1 bit per pixel and white so it is tiny on disk and cheap to build
	private static final int	BOMB_SIDE	= 8000;
	private static Path			bomb;
	private static byte[]		bombBytes;

	@BeforeAll
	public static void createBomb() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write( new BufferedImage( BOMB_SIDE, BOMB_SIDE, BufferedImage.TYPE_BYTE_BINARY ), "png", out );
		bombBytes	= out.toByteArray();
		bomb		= Files.createTempFile( "bx-image-bomb", ".png" );
		Files.write( bomb, bombBytes );
	}

	@AfterAll
	public static void deleteBomb() throws IOException {
		Files.deleteIfExists( bomb );
	}

	@DisplayName( "The bomb is small on disk but declares more pixels than the default limit" )
	@Test
	public void testBombShape() throws IOException {
		long[] dimensions = ImageGuard.probeDimensions( bombBytes );
		assertThat( dimensions ).asList().containsExactly( ( long ) BOMB_SIDE, ( long ) BOMB_SIDE ).inOrder();
		assertThat( bombBytes.length ).isLessThan( 1024 * 1024 );
		assertThat( ImageGuard.getMaxPixels() ).isEqualTo( 50_000_000L );
	}

	@DisplayName( "isImageFile answers false for an image over the pixel limit" )
	@Test
	public void testIsImageFileRejectsBomb() {
		variables.put( "path", bomb.toString() );
		runtime.executeSource( "result = isImageFile( path );", context );
		assertThat( variables.get( result ) ).isEqualTo( false );
	}

	@DisplayName( "isImageFile answers false for a non image file" )
	@Test
	public void testIsImageFileNonImage() {
		runtime.executeSource( "result = isImageFile( 'build.gradle' );", context );
		assertThat( variables.get( result ) ).isEqualTo( false );
	}

	@DisplayName( "imageNew rejects an image over the pixel limit before decoding" )
	@Test
	public void testImageNewRejectsBomb() {
		variables.put( "path", bomb.toString() );
		Throwable t = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "result = imageNew( path );", context ) );
		assertThat( rootMessage( t ) ).contains( "maxPixels" );
	}

	@DisplayName( "imageReadBase64 rejects an image over the pixel limit" )
	@Test
	public void testBase64RejectsBomb() {
		variables.put( "b64", Base64.getEncoder().encodeToString( bombBytes ) );
		Throwable t = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "result = imageReadBase64( b64 );", context ) );
		assertThat( rootMessage( t ) ).contains( "maxPixels" );
	}

	@DisplayName( "A normal image still loads" )
	@Test
	public void testNormalImageLoads() {
		runtime.executeSource( "result = imageNew( 'src/test/resources/logo.png' ); ok = isImageFile( 'src/test/resources/logo.png' );", context );
		assertThat( variables.get( result ) ).isNotNull();
		assertThat( variables.get( ortus.boxlang.runtime.scopes.Key.of( "ok" ) ) ).isEqualTo( true );
	}

	@DisplayName( "A zero limit disables the pixel check" )
	@Test
	public void testZeroDisables() throws IOException {
		ImageGuard.assertWithinLimit( bombBytes, 0 );
		assertThat( ImageGuard.isWithinLimit( new long[] { 65535, 65535 }, 0 ) ).isTrue();
	}

	@DisplayName( "Pixel math does not overflow int" )
	@Test
	public void testNoOverflow() {
		assertThat( ImageGuard.isWithinLimit( new long[] { 65535, 65535 }, ImageGuard.DEFAULT_MAX_PIXELS ) ).isFalse();
	}

	@DisplayName( "readBounded stops once the byte limit is exceeded" )
	@Test
	public void testReadBounded() throws IOException {
		byte[] ok = ImageGuard.readBounded( new java.io.ByteArrayInputStream( new byte[ 100 ] ), 100, "test" );
		assertThat( ok.length ).isEqualTo( 100 );
		assertThrows( BoxRuntimeException.class, () -> ImageGuard.readBounded( new java.io.ByteArrayInputStream( new byte[ 101 ] ), 100, "test" ) );
	}

	@DisplayName( "Oversized Base64 is rejected before decoding" )
	@Test
	public void testBase64SizeLimit() {
		assertThrows( BoxRuntimeException.class, () -> ImageGuard.assertBase64Size( "A".repeat( 400 ), 100 ) );
		ImageGuard.assertBase64Size( "A".repeat( 100 ), 100 );
	}

	private static String rootMessage( Throwable t ) {
		StringBuilder sb = new StringBuilder();
		for ( ; t != null; t = t.getCause() ) {
			sb.append( t.getMessage() ).append( ' ' );
		}
		return sb.toString();
	}

}
