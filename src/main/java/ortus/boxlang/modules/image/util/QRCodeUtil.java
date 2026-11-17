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
package ortus.boxlang.modules.image.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

/**
 * Encodes text into QR code images and decodes QR codes back into text using ZXing.
 */
public final class QRCodeUtil {

	/** Smallest allowed edge length of a generated QR code, in pixels. */
	public static final int		MIN_SIZE			= 16;

	/** Largest allowed edge length of a generated QR code, in pixels. */
	public static final int		MAX_SIZE			= 4096;

	/** Largest allowed quiet zone, in modules. */
	public static final int		MAX_QUIET_ZONE		= 32;

	/** Largest fraction of the code's edge length a logo may cover. Beyond this, scanners struggle even at level H. */
	public static final double	MAX_LOGO_SCALE		= 0.3;

	public static final int		DEFAULT_SIZE		= 125;
	public static final int		DEFAULT_QUIET_ZONE	= 4;
	public static final double	DEFAULT_LOGO_SCALE	= 0.2;

	private QRCodeUtil() {
	}

	/**
	 * Generates a QR code as a square image.
	 *
	 * <p>
	 * The module grid is scaled to the largest whole-pixel module size that fits the requested size, then stretched to the
	 * exact size using nearest-neighbour sampling so module edges stay sharp.
	 * </p>
	 *
	 * @param text            The text to encode. Must be non-empty.
	 * @param size            Edge length in pixels. Clamped to {@link #MIN_SIZE} to {@link #MAX_SIZE}.
	 * @param errorCorrection One of L, M, Q or H. Forced to H when a logo is supplied.
	 * @param quietZone       Light margin in modules. Clamped to 0 to {@link #MAX_QUIET_ZONE}.
	 * @param foreground      Color of the dark modules. A color name or hex code.
	 * @param background      Color of the light modules. A color name or hex code.
	 * @param logo            Optional image drawn in the center of the code. May be null.
	 * @param logoScale       Fraction of the code's edge length the logo covers. Clamped to {@link #MAX_LOGO_SCALE}.
	 *
	 * @return The generated QR code
	 */
	public static BufferedImage generate( String text, int size, String errorCorrection, int quietZone, String foreground, String background,
	    BoxImage logo, double logoScale ) {
		if ( text == null || text.isEmpty() ) {
			throw new BoxRuntimeException( "The text to encode in a QR code cannot be empty." );
		}
		if ( logo != null && logoScale <= 0 ) {
			throw new BoxRuntimeException( "The logoScale must be greater than 0 and no more than " + MAX_LOGO_SCALE + ", got [" + logoScale + "]." );
		}

		size		= Math.max( MIN_SIZE, Math.min( MAX_SIZE, size ) );
		quietZone	= Math.max( 0, Math.min( MAX_QUIET_ZONE, quietZone ) );
		logoScale	= Math.min( MAX_LOGO_SCALE, logoScale );

		Color					fg		= parseColor( foreground );
		Color					bg		= parseColor( background );
		ErrorCorrectionLevel	level	= logo != null ? ErrorCorrectionLevel.H : parseErrorCorrection( errorCorrection );

		ByteMatrix				matrix;
		try {
			matrix = Encoder.encode( text, level, encodeHints( text ) ).getMatrix();
		} catch ( WriterException e ) {
			throw new BoxRuntimeException( "Unable to encode the text as a QR code: " + e.getMessage(), e );
		}

		int				modules			= matrix.getWidth();
		int				totalModules	= modules + 2 * quietZone;
		int				scale			= Math.max( 1, size / totalModules );
		int				gridPixels		= totalModules * scale;

		int				imageType		= fg.getAlpha() < 255 || bg.getAlpha() < 255 ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
		BufferedImage	grid			= new BufferedImage( gridPixels, gridPixels, imageType );
		int				fgRgb			= fg.getRGB();
		int				bgRgb			= bg.getRGB();
		for ( int y = 0; y < gridPixels; y++ ) {
			for ( int x = 0; x < gridPixels; x++ ) {
				int		mx		= x / scale - quietZone;
				int		my		= y / scale - quietZone;
				boolean	dark	= mx >= 0 && my >= 0 && mx < modules && my < modules && matrix.get( mx, my ) == 1;
				grid.setRGB( x, y, dark ? fgRgb : bgRgb );
			}
		}

		BufferedImage result = grid;
		if ( gridPixels != size ) {
			result = new BufferedImage( size, size, imageType );
			Graphics2D g = result.createGraphics();
			g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
			g.drawImage( grid, 0, 0, size, size, null );
			g.dispose();
		}

		if ( logo != null ) {
			drawLogo( result, logo.getBufferedImage(), logoScale, bg );

			// A logo hides modules, and a short text makes a small code where every module counts. Never hand back a code that will not scan.
			if ( !text.equals( decode( result, true ) ) ) {
				throw new BoxRuntimeException(
				    "The logo makes the QR code unreadable. Use a smaller logoScale, a larger size, more contrasting colors or a shorter logo." );
			}
		}

		return result;
	}

	/**
	 * Decodes the first QR code found in an image.
	 *
	 * @param image     The image to scan
	 * @param tryHarder When true, spends more effort looking for the code and also tries light-on-dark codes
	 *
	 * @return The decoded text, or an empty string if no QR code could be read
	 */
	public static String decode( BufferedImage image, boolean tryHarder ) {
		Map<DecodeHintType, Object> hints = new EnumMap<>( DecodeHintType.class );
		hints.put( DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name() );
		if ( tryHarder ) {
			hints.put( DecodeHintType.TRY_HARDER, Boolean.TRUE );
		}

		LuminanceSource	source	= new BufferedImageLuminanceSource( image );
		String			text	= decode( source, hints );
		if ( text.isEmpty() && tryHarder ) {
			text = decode( source.invert(), hints );
		}
		return text;
	}

	/**
	 * Converts a color name or hex code into a {@link Color}.
	 *
	 * @param color A named color known to {@link BoxImage#COLORS}, or a hex code of the form #RGB, #RRGGBB or #AARRGGBB (the # is optional)
	 *
	 * @return The color
	 *
	 * @throws BoxRuntimeException If the value is not a recognized color
	 */
	public static Color parseColor( String color ) {
		if ( color == null || color.isBlank() ) {
			throw new BoxRuntimeException( "A color is required but was empty." );
		}

		String	trimmed	= color.trim();
		Color	named	= BoxImage.COLORS.get( trimmed.toLowerCase( Locale.ROOT ) );
		if ( named != null ) {
			return named;
		}

		String hex = trimmed.startsWith( "#" ) ? trimmed.substring( 1 ) : trimmed;
		try {
			switch ( hex.length() ) {
				case 3 : {
					String expanded = "" + hex.charAt( 0 ) + hex.charAt( 0 ) + hex.charAt( 1 ) + hex.charAt( 1 ) + hex.charAt( 2 ) + hex.charAt( 2 );
					return new Color( Integer.parseInt( expanded, 16 ) );
				}
				case 6 :
					return new Color( Integer.parseInt( hex, 16 ) );
				case 8 :
					return new Color( ( int ) Long.parseLong( hex, 16 ), true );
				default :
					break;
			}
		} catch ( NumberFormatException e ) {
			// fall through to the error below
		}

		throw new BoxRuntimeException( "Unrecognized color [" + color + "]. Use a color name or a hex code such as #000000." );
	}

	private static ErrorCorrectionLevel parseErrorCorrection( String errorCorrection ) {
		String value = errorCorrection == null ? "M" : errorCorrection.trim().toUpperCase( Locale.ROOT );
		return switch ( value ) {
			case "L" -> ErrorCorrectionLevel.L;
			case "M" -> ErrorCorrectionLevel.M;
			case "Q" -> ErrorCorrectionLevel.Q;
			case "H" -> ErrorCorrectionLevel.H;
			default -> throw new BoxRuntimeException( "Invalid errorCorrection [" + errorCorrection + "]. Must be one of L, M, Q or H." );
		};
	}

	/**
	 * Plain ASCII and Latin-1 text keeps ZXing's default encoding for the widest scanner compatibility. Anything else is encoded as UTF-8.
	 */
	private static Map<EncodeHintType, Object> encodeHints( String text ) {
		Map<EncodeHintType, Object> hints = new EnumMap<>( EncodeHintType.class );
		if ( !StandardCharsets.ISO_8859_1.newEncoder().canEncode( text ) ) {
			hints.put( EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name() );
		}
		return hints;
	}

	private static String decode( LuminanceSource source, Map<DecodeHintType, Object> hints ) {
		try {
			return new QRCodeReader().decode( new BinaryBitmap( new HybridBinarizer( source ) ), hints ).getText();
		} catch ( NotFoundException | ChecksumException | FormatException e ) {
			return "";
		}
	}

	/**
	 * Draws the logo centered on the code, on top of a padded background-colored plate so the logo does not bleed into the modules.
	 */
	private static void drawLogo( BufferedImage target, BufferedImage logo, double logoScale, Color background ) {
		int			size		= target.getWidth();
		int			maxEdge		= Math.max( 1, ( int ) Math.round( size * logoScale ) );
		double		ratio		= Math.min( ( double ) maxEdge / logo.getWidth(), ( double ) maxEdge / logo.getHeight() );
		int			logoWidth	= Math.max( 1, ( int ) Math.round( logo.getWidth() * ratio ) );
		int			logoHeight	= Math.max( 1, ( int ) Math.round( logo.getHeight() * ratio ) );
		int			padding		= Math.max( 2, maxEdge / 10 );
		int			x			= ( size - logoWidth ) / 2;
		int			y			= ( size - logoHeight ) / 2;

		Graphics2D	g			= target.createGraphics();
		g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g.setColor( background );
		g.fillRect( x - padding, y - padding, logoWidth + 2 * padding, logoHeight + 2 * padding );
		g.drawImage( logo, x, y, logoWidth, logoHeight, null );
		g.dispose();
	}

}
