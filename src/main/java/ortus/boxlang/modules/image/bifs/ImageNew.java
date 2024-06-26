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

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageKeys;
import ortus.boxlang.modules.image.ImageType;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.CastAttempt;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class ImageNew extends BIF {

	/**
	 * Constructor
	 * someImage.addBorder(thickness [, color] [, bordertype])
	 */
	public ImageNew() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( false, "any", Key.source, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "numeric", ImageKeys.width ),
		    new Argument( false, "numeric", ImageKeys.height ),
		    new Argument( false, "string", ImageKeys.imageType ),
		    new Argument( false, "string", ImageKeys.color, "black" ),
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		Object source = arguments.get( Key.source );

		if ( source instanceof BufferedImage sourceBufferedImage ) {
			return new BoxImage( sourceBufferedImage );
		} else if ( source instanceof BoxImage sourceBoxImage ) {
			return sourceBoxImage.copy();
		}

		CastAttempt<String> castAttempt = StringCaster.attempt( source );

		if ( !castAttempt.wasSuccessful() ) {
			throw new BoxRuntimeException( "The type: " + source.getClass().getTypeName()
			    + "cannot be used as the source of an image. You must provide an image, BufferedImage, file path, or url." );
		}

		String sourceString = castAttempt.get();

		if ( !sourceString.isEmpty() ) {
			try {
				return new BoxImage( URI.create( sourceString ) );
			} catch ( Exception e ) {
				throw new BoxRuntimeException( "Unable to load image: " + arguments.getAsString( Key.path ), e );
			}
		}

		return new BoxImage(
		    IntegerCaster.cast( arguments.get( ImageKeys.width ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.height ) ),
		    ImageType.valueOf( StringCaster.cast( arguments.get( ImageKeys.imageType ) ).toUpperCase() ),
		    StringCaster.cast( arguments.get( ImageKeys.color ) )
		);
	}

}
