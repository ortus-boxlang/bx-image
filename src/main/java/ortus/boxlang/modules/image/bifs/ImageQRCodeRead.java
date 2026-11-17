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

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.BooleanCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

/**
 * Reads the QR code in an image and returns the text it encodes.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * img = ImageRead( "/path/to/qr.png" );
 * text = ImageQRCodeRead( img );
 *
 * // Member function
 * text = img.readQRCode();
 *
 * // No QR code found returns an empty string
 * if ( !len( text ) ) {
 *     writeOutput( "No QR code found" );
 * }
 * </pre>
 */
@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "readQRCode" )
public class ImageQRCodeRead extends BIF {

	public ImageQRCodeRead() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "boolean", KeyDictionary.tryHarder, true )
		};
	}

	/**
	 * Reads the QR code in an image.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name The image or name of variable that references an image to operate on.
	 *
	 * @argument.tryHarder When {@code true}, spends more effort finding the code and also reads light-on-dark codes.
	 *                     Set to {@code false} for the fastest scan. Defaults to {@code true}.
	 *
	 * @return The decoded text, or an empty string if no QR code was found.
	 */
	public String _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		return theImage.readQRCode( BooleanCaster.cast( arguments.get( KeyDictionary.tryHarder ) ) );
	}

}
