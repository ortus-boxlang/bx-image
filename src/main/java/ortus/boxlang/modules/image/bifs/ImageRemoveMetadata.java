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
import ortus.boxlang.modules.image.BoxImageMetadata;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.modules.image.util.MetadataTargets;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "removeMetadata" )
@BoxMember( type = BoxLangType.CUSTOM2, customType = BoxImageMetadata.class, name = "removeMetadata" )
public class ImageRemoveMetadata extends BIF {

	/**
	 * Constructor
	 */
	public ImageRemoveMetadata() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "any", KeyDictionary.types, "all" )
		};
	}

	/**
	 * Removes metadata types. On a metadata handle it changes the working copy; on a file path the
	 * file is rewritten in place without re-encoding. On an image object it only undoes earlier
	 * setters, because an image's write() never carries source metadata.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name An image, a metadata handle, the name of a variable holding one, or a file path.
	 *
	 * @argument.types A list or array of types: exif, iptc, xmp, icc, comment, photoshop, or all (the default).
	 *
	 * @return The image or metadata handle, for chaining.
	 */
	public Object _invoke( IBoxContext context, ArgumentsScope arguments ) {
		Object		target	= MetadataTargets.resolve( context, arguments.get( KeyDictionary.name ) );
		String[]	types	= MetadataTargets.typeNames( arguments.get( KeyDictionary.types ) );
		if ( types.length == 0 ) {
			types = new String[] { "all" };
		}
		if ( target instanceof BoxImage image ) {
			return image.removeMetadata( types );
		}
		if ( target instanceof BoxImageMetadata handle ) {
			return handle.remove( types );
		}
		return BoxImageMetadata.read( ( String ) target ).remove( types ).write();
	}

}
