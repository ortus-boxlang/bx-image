/**
 * [BoxLang]
 *
 * Copyright [2023] [Ortus Solutions, Corp]
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

import java.io.IOException;
import java.util.Set;

import com.drew.imaging.ImageProcessingException;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.ImageMetadataUtil;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "getExifMetaData" )
public class ImageGetExifMetaData extends BIF {

	/**
	 * Constructor
	 */
	public ImageGetExifMetaData() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) )
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public IStruct _invoke( IBoxContext context, ArgumentsScope arguments ) {
		try {
			Object arg = arguments.get( KeyDictionary.name );

			if ( arg instanceof BoxImage image ) {
				return image.getExifMetaData();
			} else if ( arg instanceof String imageString ) {
				return ImageMetadataUtil.readExifMetaData( imageString );
			}

		} catch ( ImageProcessingException e ) {
			throw new BoxRuntimeException( "Unable to process image metadata", e );
		} catch ( IOException e ) {
			throw new BoxRuntimeException( "Unable to read image", e );
		}

		throw new BoxRuntimeException( "Unable to read Exif metadata" );
	}
}
