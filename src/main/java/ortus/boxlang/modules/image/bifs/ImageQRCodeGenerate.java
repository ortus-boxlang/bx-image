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

import java.awt.image.BufferedImage;
import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.modules.image.util.QRCodeUtil;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.DoubleCaster;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;
import ortus.boxlang.runtime.validation.Validator;

/**
 * Generates a QR code image from text. The result is a regular BoxImage, so it works with {@code ImageWrite()},
 * {@code ImageWriteBase64()}, {@code ImageResize()} and the rest of the image functions.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * // Basic usage
 * qr = ImageQRCodeGenerate( "https://example.com/verify" );
 * ImageWrite( qr, "/path/to/qr.png" );
 *
 * // Size, format and error correction
 * qr = ImageQRCodeGenerate( "ME:12345678", 250, "gif", "H" );
 *
 * // Colors and a logo
 * qr = ImageQRCodeGenerate( text = "https://example.com", size = 400, foreground = "#1a237e", background = "white", logo = "/logo.png" );
 *
 * // Member function on a string
 * qr = "https://example.com".toQRCode( 250 );
 * </pre>
 */
@BoxBIF
@BoxMember( type = BoxLangType.STRING, name = "toQRCode" )
public class ImageQRCodeGenerate extends BIF {

	public ImageQRCodeGenerate() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "string", KeyDictionary.text, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( false, "numeric", KeyDictionary.size, QRCodeUtil.DEFAULT_SIZE ),
		    new Argument( false, "string", KeyDictionary.format, "png" ),
		    new Argument( false, "string", KeyDictionary.errorCorrection, "M" ),
		    new Argument( false, "numeric", KeyDictionary.quietZone, QRCodeUtil.DEFAULT_QUIET_ZONE ),
		    new Argument( false, "string", KeyDictionary.foreground, "black" ),
		    new Argument( false, "string", KeyDictionary.background, "white" ),
		    new Argument( false, "any", KeyDictionary.logo, "" ),
		    new Argument( false, "numeric", KeyDictionary.logoScale, QRCodeUtil.DEFAULT_LOGO_SCALE ),
		};
	}

	/**
	 * Generates a QR code image from text.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.text The text to encode. Required and must be non-empty.
	 *
	 * @argument.size Edge length of the square image in pixels. Clamped to 16 to 4096. Defaults to 125.
	 *
	 * @argument.format The output image format, e.g. {@code png}, {@code gif}, {@code jpg}. Any format returned by
	 *                  {@code GetWriteableImageFormats()} is valid. Defaults to {@code png}.
	 *
	 * @argument.errorCorrection How much of the code can be damaged and still scan: {@code L} (7%), {@code M} (15%),
	 *                           {@code Q} (25%) or {@code H} (30%). Defaults to {@code M}. Always {@code H} when a logo is supplied.
	 *
	 * @argument.quietZone The light margin around the code, in modules. Clamped to 0 to 32. Defaults to 4, which is what the QR specification requires.
	 *
	 * @argument.foreground The color of the dark modules, as a color name or hex code. Defaults to {@code black}.
	 *
	 * @argument.background The color of the light modules, as a color name or hex code. Defaults to {@code white}.
	 *
	 * @argument.logo Optional image drawn in the center of the code. A BoxImage, file path or URL.
	 *
	 * @argument.logoScale The fraction of the code's edge length the logo covers. Clamped to 0.3. Defaults to 0.2.
	 *
	 * @return The QR code as a BoxImage.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		return BoxImage.generateQRCode(
		    arguments.getAsString( KeyDictionary.text ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.size ) ),
		    StringCaster.cast( arguments.get( KeyDictionary.format ) ),
		    StringCaster.cast( arguments.get( KeyDictionary.errorCorrection ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.quietZone ) ),
		    StringCaster.cast( arguments.get( KeyDictionary.foreground ) ),
		    StringCaster.cast( arguments.get( KeyDictionary.background ) ),
		    resolveLogo( context, arguments.get( KeyDictionary.logo ) ),
		    DoubleCaster.cast( arguments.get( KeyDictionary.logoScale ) )
		);
	}

	/**
	 * Turns a logo argument into a BoxImage.
	 *
	 * @param context The current context, used to expand relative paths
	 * @param logo    A BoxImage, BufferedImage, file path, URL, or null/empty for no logo
	 *
	 * @return The logo, or null when none was supplied
	 */
	public static BoxImage resolveLogo( IBoxContext context, Object logo ) {
		if ( logo == null ) {
			return null;
		}
		if ( logo instanceof BoxImage boxImage ) {
			return boxImage;
		}
		if ( logo instanceof BufferedImage bufferedImage ) {
			return new BoxImage( bufferedImage );
		}

		String path = StringCaster.cast( logo );
		if ( path.isBlank() ) {
			return null;
		}

		String resolved = path.regionMatches( true, 0, "http", 0, 4 ) ? path : FileSystemUtil.expandPath( context, path ).absolutePath().toString();
		try {
			return new BoxImage( resolved );
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to load the QR code logo: " + path, e );
		}
	}

}
