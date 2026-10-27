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
package ortus.boxlang.modules.image.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;

import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.dynamic.casters.LongCaster;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

/**
 * Guards image decoding against decompression bombs. A tiny file can declare enormous dimensions,
 * so the pixel count is read from the image header and checked BEFORE any raster is allocated.
 * The size of the source data is also capped before it is buffered into memory.
 * <p>
 * Limits come from the module settings {@code maxPixels} and {@code maxBytes}. A value of zero or less disables the limit.
 */
public final class ImageGuard {

	/** Default maximum number of pixels (width x height) an image may declare: 50 Mpx */
	public static final long	DEFAULT_MAX_PIXELS	= 50_000_000L;

	/** Default maximum number of source bytes that will be buffered: 50 MB */
	public static final long	DEFAULT_MAX_BYTES	= 50L * 1024 * 1024;

	private ImageGuard() {
	}

	/**
	 * The configured maximum pixel count, or {@link #DEFAULT_MAX_PIXELS} if unset.
	 *
	 * @return The limit; zero or less means unlimited
	 */
	public static long getMaxPixels() {
		return getSetting( "maxPixels", DEFAULT_MAX_PIXELS );
	}

	/**
	 * The configured maximum source size in bytes, or {@link #DEFAULT_MAX_BYTES} if unset.
	 *
	 * @return The limit; zero or less means unlimited
	 */
	public static long getMaxBytes() {
		return getSetting( "maxBytes", DEFAULT_MAX_BYTES );
	}

	private static long getSetting( String name, long defaultValue ) {
		try {
			IStruct	settings	= BoxRuntime.getInstance().getModuleService().getModuleSettings( KeyDictionary.bximage );
			Object	value		= settings == null ? null : settings.get( name );
			return value == null ? defaultValue : LongCaster.cast( value );
		} catch ( Exception e ) {
			// Module not registered (e.g. unit tests) or a bad value: use the safe default
			return defaultValue;
		}
	}

	/**
	 * Reads a stream fully, failing as soon as more than {@code maxBytes} are seen.
	 *
	 * @param in       The stream to read
	 * @param maxBytes The limit; zero or less means unlimited
	 * @param source   A description of the source, for the error message
	 *
	 * @return The bytes read
	 *
	 * @throws IOException         If reading fails
	 * @throws BoxRuntimeException If the stream exceeds the limit
	 */
	public static byte[] readBounded( InputStream in, long maxBytes, String source ) throws IOException {
		if ( maxBytes <= 0 ) {
			return in.readAllBytes();
		}
		ByteArrayOutputStream	out		= new ByteArrayOutputStream();
		byte[]					buffer	= new byte[ 8192 ];
		long					total	= 0;
		int						read;
		while ( ( read = in.read( buffer ) ) != -1 ) {
			total += read;
			if ( total > maxBytes ) {
				throw new BoxRuntimeException( "Image [" + source + "] exceeds the maximum allowed size of " + maxBytes + " bytes (maxBytes)." );
			}
			out.write( buffer, 0, read );
		}
		return out.toByteArray();
	}

	/**
	 * Rejects a Base64 string whose decoded size would exceed {@code maxBytes}, before decoding it.
	 *
	 * @param base64   The Base64 payload (without a data URI prefix)
	 * @param maxBytes The limit; zero or less means unlimited
	 *
	 * @throws BoxRuntimeException If the decoded data would be too large
	 */
	public static void assertBase64Size( String base64, long maxBytes ) {
		if ( maxBytes > 0 && ( long ) base64.length() / 4 * 3 > maxBytes ) {
			throw new BoxRuntimeException( "Base64 image data exceeds the maximum allowed size of " + maxBytes + " bytes (maxBytes)." );
		}
	}

	/**
	 * Reads the declared width and height from the image header without decoding the pixels.
	 *
	 * @param input The image input stream; its position is not restored
	 *
	 * @return {@code {width, height}}, or {@code null} if no reader recognizes the data
	 *
	 * @throws IOException If the header cannot be read
	 */
	public static long[] probeDimensions( ImageInputStream input ) throws IOException {
		ImageReader reader = null;
		try {
			// The bundled pure-Java WebP reader is preferred, as BoxImage does when decoding
			WebPImageReaderSpi webp = new WebPImageReaderSpi();
			if ( webp.canDecodeInput( input ) ) {
				reader = webp.createReaderInstance( null );
			} else {
				Iterator<ImageReader> readers = ImageIO.getImageReaders( input );
				if ( !readers.hasNext() ) {
					return null;
				}
				reader = readers.next();
			}
			reader.setInput( input, true, true );
			return new long[] { reader.getWidth( 0 ), reader.getHeight( 0 ) };
		} finally {
			if ( reader != null ) {
				reader.dispose();
			}
		}
	}

	/**
	 * Reads the declared dimensions from in-memory image data.
	 *
	 * @param data The image bytes
	 *
	 * @return {@code {width, height}}, or {@code null} if no reader recognizes the data
	 *
	 * @throws IOException If the header cannot be read
	 */
	public static long[] probeDimensions( byte[] data ) throws IOException {
		try ( ImageInputStream input = ImageIO.createImageInputStream( new ByteArrayInputStream( data ) ) ) {
			return input == null ? null : probeDimensions( input );
		}
	}

	/**
	 * Whether the declared dimensions fit within {@code maxPixels}.
	 *
	 * @param dimensions {@code {width, height}}
	 * @param maxPixels  The limit; zero or less means unlimited
	 *
	 * @return True if within the limit
	 */
	public static boolean isWithinLimit( long[] dimensions, long maxPixels ) {
		return maxPixels <= 0 || dimensions[ 0 ] * dimensions[ 1 ] <= maxPixels;
	}

	/**
	 * Throws if the in-memory image declares more pixels than allowed. Data no reader recognizes is left for the decoder to reject.
	 *
	 * @param data      The image bytes
	 * @param maxPixels The limit; zero or less means unlimited
	 *
	 * @throws IOException         If the header cannot be read
	 * @throws BoxRuntimeException If the image is too large
	 */
	public static void assertWithinLimit( byte[] data, long maxPixels ) throws IOException {
		if ( maxPixels <= 0 ) {
			return;
		}
		long[] dimensions = probeDimensions( data );
		if ( dimensions != null && !isWithinLimit( dimensions, maxPixels ) ) {
			throw new BoxRuntimeException( "Image dimensions " + dimensions[ 0 ] + " x " + dimensions[ 1 ] + " ("
			    + dimensions[ 0 ] * dimensions[ 1 ] + " pixels) exceed the maximum allowed " + maxPixels + " pixels (maxPixels)." );
		}
	}

}
