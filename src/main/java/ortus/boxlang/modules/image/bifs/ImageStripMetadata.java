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

import ortus.boxlang.modules.image.BoxImageMetadata;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.modules.image.util.MetadataTargets;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class ImageStripMetadata extends BIF {

	/**
	 * Constructor
	 */
	public ImageStripMetadata() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.source, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "string", KeyDictionary.destination ),
		    new Argument( false, "any", KeyDictionary.types )
		};
	}

	/**
	 * Removes metadata from an image file without re-encoding it. By default everything the format
	 * can hold is removed (EXIF, IPTC, XMP, ICC profile, comments, Photoshop resources); pass
	 * types to remove only some. Without a destination the file is rewritten in place.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.source A file path, or an image that was read from a file.
	 *
	 * @argument.destination Where to write the stripped copy. Omit to rewrite the source file in place.
	 *
	 * @argument.types A list or array of metadata types to remove: exif, iptc, xmp, icc, comment, photoshop. Omit for all.
	 *
	 * @return The metadata handle of the written image.
	 */
	public BoxImageMetadata _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImageMetadata	handle	= MetadataTargets.handleFor( context, arguments.get( KeyDictionary.source ) );
		String[]			types	= MetadataTargets.typeNames( arguments.get( KeyDictionary.types ) );
		if ( types.length == 0 ) {
			handle.removeAll();
		} else {
			handle.remove( types );
		}
		String destination = arguments.getAsString( KeyDictionary.destination );
		if ( destination == null || destination.isEmpty() ) {
			handle.write();
		} else {
			handle.write( MetadataTargets.resolvePath( context, destination ) );
		}
		return handle;
	}

}
