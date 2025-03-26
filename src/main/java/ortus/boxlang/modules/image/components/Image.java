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
package ortus.boxlang.modules.image.components;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageKeys;
import ortus.boxlang.runtime.components.Attribute;
import ortus.boxlang.runtime.components.BoxComponent;
import ortus.boxlang.runtime.components.Component;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.BooleanCaster;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;
import ortus.boxlang.runtime.validation.Validator;

@BoxComponent( allowsBody = false )
public class Image extends Component {

	static Key	locationKey	= Key.of( "location" );
	static Key	shoutKey	= Key.of( "shout" );

	public Image() {
		super();
		declaredAttributes = new Attribute[] {
		    new Attribute( ImageKeys.action, "string", Set.of( Validator.REQUIRED, Validator.valueOneOf(
		        "border",
		        "captcha",
		        "convert",
		        "info",
		        "read",
		        "resize",
		        "rotate",
		        "write",
		        "writeToBrowser"
		    ) ) ),
		    new Attribute( ImageKeys.angle, "string" ),
		    new Attribute( ImageKeys.color, "string" ),
		    new Attribute( ImageKeys.destination, "string" ),
		    new Attribute( ImageKeys.difficulty, "string" ),
		    new Attribute( ImageKeys.fontSize, "string" ),
		    new Attribute( ImageKeys.format, "string" ),
		    new Attribute( ImageKeys.height, "numeric" ),
		    new Attribute( ImageKeys.isBase64, "string" ),
		    new Attribute( ImageKeys.name, "string" ),
		    new Attribute( ImageKeys.overwrite, "boolean", false ),
		    new Attribute( ImageKeys.quality, "string" ),
		    new Attribute( ImageKeys.source, "any" ),
		    new Attribute( ImageKeys.structName, "string" ),
		    new Attribute( ImageKeys.text, "string" ),
		    new Attribute( ImageKeys.thickness, "string" ),
		    new Attribute( ImageKeys.width, "numeric" ),
		    new Attribute( ImageKeys.fonts, "string" ),
		    new Attribute( ImageKeys.interpolation, "string" )
		};
	}

	/**
	 * An example component that says hello
	 *
	 * @param context        The context in which the Component is being invoked
	 * @param attributes     The attributes to the Component
	 * @param body           The body of the Component
	 * @param executionState The execution state of the Component
	 *
	 * @attribute.name The name of the person greeting us.
	 *
	 * @attribute.location The location of the person.
	 *
	 * @attribute.shout Whether the person is shouting or not.
	 *
	 */
	public BodyResult _invoke( IBoxContext context, IStruct attributes, ComponentBody body, IStruct executionState ) {
		String		action	= attributes.getAsString( ImageKeys.action );
		BoxImage	image	= null;
		switch ( action ) {
			case "border" :
				image = getImageFromContext( context, attributes );
				String color = attributes.getAsString( ImageKeys.color );
				int thickness = IntegerCaster.cast( attributes.get( ImageKeys.thickness ) );
				image.addBorder( thickness, color );

				putImageInContext( image, context, attributes );

				break;
			case "captcha" :
				// Handle captcha action
				break;
			case "convert" :
				// Handle convert action
				image = getImageFromContext( context, attributes );
				String destination = StringCaster.cast( attributes.get( Key.destination ) );
				boolean overwrite = BooleanCaster.cast( attributes.get( Key.overwrite ) );

				if ( !overwrite && FileSystemUtil.exists( destination ) ) {
					throw new BoxRuntimeException( String.format( "Unable to write image, destination exists: {}", destination ) );
				}

				image.write( destination );
				break;
			case "info" :
				// Handle info action
				image = getImageFromContext( context, attributes );
				IStruct info = image.getExifMetaData();

				StringCaster.attempt( attributes.get( ImageKeys.structName ) )
				    .ifPresent( ( n ) -> {
					    context.getDefaultAssignmentScope().assign( context, Key.of( n ), info );
				    } );

				break;
			case "read" :
				// Handle read action
				break;
			case "resize" :
				// Handle resize action
				image = getImageFromContext( context, attributes );
				int width = IntegerCaster.cast( attributes.get( ImageKeys.width ) );
				int height = IntegerCaster.cast( attributes.get( ImageKeys.height ) );
				image.resize( width, height, "bilinear", 1 );

				putImageInContext( image, context, attributes );

				break;
			case "rotate" :
				// Handle rotate action
				break;
			case "write" :
				// Handle write action
				image = getImageFromContext( context, attributes );
				image.write( StringCaster.cast( attributes.get( ImageKeys.destination ) ) );
				break;
			case "writeToBrowser" :
				// Handle writeToBrowser action
				break;
			default :
				// Handle unknown action
				break;
		}

		return DEFAULT_RETURN;
	}

	private void putImageInContext( BoxImage image, IBoxContext context, IStruct attributes ) {
		StringCaster.attempt( attributes.get( ImageKeys.name ) )
		    .ifPresent( ( n ) -> {
			    context.getDefaultAssignmentScope().assign( context, Key.of( n ), image );
		    } );
	}

	private BoxImage getImageFromContext( IBoxContext context, IStruct attributes ) {
		Object source = attributes.get( ImageKeys.source );

		if ( source instanceof BoxImage i ) {
			return i;
		}

		if ( source instanceof String pathOrURL ) {
			try {
				return new BoxImage( pathOrURL );
			} catch ( Exception e ) {
				throw new BoxRuntimeException( String.format( "Unable to read image from: %s", pathOrURL ), e );
			}
		}

		throw new BoxRuntimeException( "You must supply a source for the Image component" );
	}

}
