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
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "setIPTCMetaData" )
@BoxMember( type = BoxLangType.CUSTOM2, customType = BoxImageMetadata.class, name = "setIPTCMetaData" )
public class ImageSetIPTCMetaData extends BIF {

	/**
	 * Constructor
	 */
	public ImageSetIPTCMetaData() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "struct", KeyDictionary.tags, Set.of( Validator.REQUIRED ) )
		};
	}

	/**
	 * Sets several IPTC tags from a struct of tag name to value; see the single-tag setter for how
	 * images, handles and file paths are treated.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name An image, a metadata handle, the name of a variable holding one, or a file path.
	 *
	 * @argument.tags A struct of IPTC tag names to values. A null value removes the tag.
	 *
	 * @return The image or metadata handle, for chaining.
	 */
	public Object _invoke( IBoxContext context, ArgumentsScope arguments ) {
		Object	target	= MetadataTargets.resolve( context, arguments.get( KeyDictionary.name ) );
		IStruct	tags	= arguments.getAsStruct( KeyDictionary.tags );
		if ( target instanceof BoxImage image ) {
			return image.setIPTCMetaData( tags );
		}
		if ( target instanceof BoxImageMetadata handle ) {
			return handle.setIPTCMetaData( tags );
		}
		return BoxImageMetadata.read( ( String ) target ).setIPTCMetaData( tags ).write();
	}

}
