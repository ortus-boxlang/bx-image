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
package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;
import ortus.boxlang.runtime.validation.Validator;

/**
 * Generates a CAPTCHA image with distorted text designed to be human-readable but
 * resistant to automated parsing.
 *
 * <p>
 * Argument order is compatible with ColdFusion's {@code ImageCreateCaptcha()}:
 * {@code height, width, text [, difficulty [, fonts [, fontSize]]]}
 * </p>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * // Basic usage
 * captcha = ImageGenerateCaptcha( 75, 200, "A3X9K2" );
 * ImageWrite( captcha, "/path/to/captcha.png" );
 *
 * // With difficulty and font options (ColdFusion-compatible positional form)
 * captcha = ImageGenerateCaptcha( 35, 400, "loner" );
 * captcha = ImageGenerateCaptcha( 35, 400, "loner", "high" );
 * captcha = ImageGenerateCaptcha( 35, 400, "loner", "high", "serif,sansserif", 24 );
 * </pre>
 */
@BoxBIF
public class ImageGenerateCaptcha extends BIF {

	public ImageGenerateCaptcha() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( false, "numeric", KeyDictionary.height, 75 ),
		    new Argument( false, "numeric", KeyDictionary.width, 200 ),
		    new Argument( true, "string", KeyDictionary.text, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( false, "string", KeyDictionary.difficulty, "low" ),
		    new Argument( false, "string", KeyDictionary.fonts, "" ),
		    new Argument( false, "numeric", KeyDictionary.fontSize, 24 ),
		    new Argument( false, "string", KeyDictionary.destination, "" ),
		    new Argument( false, "boolean", KeyDictionary.overwrite, false ),
		};
	}

	/**
	 * Generates and returns a CAPTCHA image with distorted text.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.height Height of the generated image in pixels. Defaults to 75.
	 *
	 * @argument.width Width of the generated image in pixels. Defaults to 200.
	 *
	 * @argument.text The text string to render in the CAPTCHA image. Required. Uppercase letters
	 *                and digits are recommended for readability.
	 *
	 * @argument.difficulty Controls the level of visual distortion applied to the image.
	 *                      One of {@code low} (minimal rotation, clean background),
	 *                      {@code medium} (moderate rotation, noise dots, crossing lines), or
	 *                      {@code high} (heavy rotation, dense noise, wavy lines, random character colors).
	 *                      Defaults to {@code low}.
	 *
	 * @argument.fonts Comma-separated list of font family names to randomly select per character,
	 *                 e.g. {@code "Arial,Verdana,Georgia"}. Defaults to the JVM's default sans-serif font.
	 *
	 * @argument.fontSize Font size in points. Defaults to 24.
	 *
	 * @argument.destination Optional file path to write the generated PNG to. If omitted the image
	 *                       is only returned, not written to disk.
	 *
	 * @argument.overwrite When {@code true}, overwrites an existing file at {@code destination}.
	 *                     Defaults to {@code false}.
	 *
	 * @return The generated CAPTCHA as a BoxImage.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		int		height		= IntegerCaster.cast( arguments.get( KeyDictionary.height ) );
		int		width		= IntegerCaster.cast( arguments.get( KeyDictionary.width ) );
		String	text		= arguments.getAsString( KeyDictionary.text );
		String	difficulty	= arguments.getAsString( KeyDictionary.difficulty );
		String	fontsArg	= StringCaster.cast( arguments.get( KeyDictionary.fonts ) );
		int		fontSize	= IntegerCaster.cast( arguments.get( KeyDictionary.fontSize ) );
		String	destination	= StringCaster.cast( arguments.get( KeyDictionary.destination ) );
		boolean	overwrite	= ( boolean ) arguments.get( KeyDictionary.overwrite );

		if ( difficulty == null || difficulty.isEmpty() ) {
			difficulty = "low";
		}

		String[]	fontList	= ( fontsArg == null || fontsArg.isEmpty() )
		    ? new String[ 0 ]
		    : fontsArg.split( "\\s*,\\s*" );

		BoxImage	captcha		= BoxImage.generateCaptcha( text, width, height, fontSize, difficulty, fontList );

		if ( destination != null && !destination.isEmpty() ) {
			String resolvedPath = FileSystemUtil.expandPath( context, destination ).absolutePath().toString();
			if ( !overwrite && FileSystemUtil.exists( resolvedPath ) ) {
				throw new BoxRuntimeException( "Unable to write captcha image, destination exists: " + resolvedPath );
			}
			captcha.write( resolvedPath );
		}

		return captcha;
	}

}
