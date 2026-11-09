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
public class ImageReadMetadata extends BIF {

	/**
	 * Constructor
	 */
	public ImageReadMetadata() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.source, Set.of( Validator.REQUIRED ) )
		};
	}

	/**
	 * Reads the metadata of an image without decoding its pixels and returns a metadata handle.
	 * The handle has getters (getExif(), getIPTC(), getXMP(), getExifTag(), getIPTCTag(), getTypes()),
	 * chainable setters (setExifTag(), setIPTCTag(), setXMP(), setExifMetaData(), setIPTCMetaData(),
	 * remove(), removeAll()) and writers (write(), write( destination ), toBinary(), toBase64()).
	 * Writing copies the image data byte for byte and only replaces the metadata.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.source A file path or URL, or an image that was read from a file.
	 *
	 * @return The metadata handle.
	 */
	public BoxImageMetadata _invoke( IBoxContext context, ArgumentsScope arguments ) {
		return MetadataTargets.handleFor( context, arguments.get( KeyDictionary.source ) );
	}

}
