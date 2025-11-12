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
import java.util.concurrent.atomic.AtomicBoolean;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageEvents;
import ortus.boxlang.modules.image.services.ImageService;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.components.Attribute;
import ortus.boxlang.runtime.components.BoxComponent;
import ortus.boxlang.runtime.components.Component;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.BooleanCaster;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.logging.BoxLangLogger;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.Struct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;
import ortus.boxlang.runtime.validation.Validator;

@BoxComponent( allowsBody = false )
public class Image extends Component {

	private ImageService	imageService	= ( ImageService ) runtime.getGlobalService( KeyDictionary.imageService );

	static Key				locationKey		= Key.of( "location" );
	static Key				shoutKey		= Key.of( "shout" );

	BoxLangLogger			logger;

	public Image() {
		super();
		declaredAttributes	= new Attribute[] {
		    new Attribute( KeyDictionary.action, "string", Set.of( Validator.REQUIRED, Validator.valueOneOf(
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
		    new Attribute( KeyDictionary.angle, "string" ),
		    new Attribute( KeyDictionary.color, "string" ),
		    new Attribute( KeyDictionary.destination, "string" ),
		    new Attribute( KeyDictionary.difficulty, "string" ),
		    new Attribute( KeyDictionary.fontSize, "string" ),
		    new Attribute( KeyDictionary.format, "string" ),
		    new Attribute( KeyDictionary.height, "numeric" ),
		    new Attribute( KeyDictionary.isBase64, "string" ),
		    new Attribute( KeyDictionary.name, "string" ),
		    new Attribute( KeyDictionary.overwrite, "boolean", false ),
		    new Attribute( KeyDictionary.quality, "string" ),
		    new Attribute( KeyDictionary.source, "any" ),
		    new Attribute( KeyDictionary.structName, "string" ),
		    new Attribute( KeyDictionary.text, "string" ),
		    new Attribute( KeyDictionary.thickness, "string" ),
		    new Attribute( KeyDictionary.width, "numeric" ),
		    new Attribute( KeyDictionary.fonts, "string" ),
		    new Attribute( KeyDictionary.interpolation, "string" )
		};

		logger				= imageService.getLogger();
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
		String		action		= attributes.getAsString( KeyDictionary.action );
		BoxImage	image		= null;
		IStruct		eventData	= null;

		switch ( action ) {
			case "border" :
				image = getImageFromContext( context, attributes );
				String color = attributes.getAsString( KeyDictionary.color );
				int thickness = IntegerCaster.cast( attributes.get( KeyDictionary.thickness ) );
				image.addBorder( thickness, color );

				putImageInContext( image, context, attributes );

				break;
			case "captcha" :
				// Handle captcha action
				break;
			case "convert" :
				// Handle convert action
				image = getImageFromContext( context, attributes );

				writeIfAble( image, attributes );
				break;
			case "info" :
				// Handle info action
				image = getImageFromContext( context, attributes );
				IStruct info = image.getExifMetaData();

				StringCaster.attempt( attributes.get( KeyDictionary.structName ) )
				    .ifPresent( ( n ) -> {
					    context.getDefaultAssignmentScope().assign( context, Key.of( n ), info );
				    } );

				break;
			case "read" :
				// Handle read action
				image = getImageFromContext( context, attributes );
				putImageInContext( image, context, attributes );
				break;
			case "resize" :
				// Handle resize action
				image = getImageFromContext( context, attributes );
				int width = IntegerCaster.cast( attributes.get( KeyDictionary.width ) );
				int height = IntegerCaster.cast( attributes.get( KeyDictionary.height ) );
				image.resize( width, height, "bilinear", 1 );

				putImageInContext( image, context, attributes );

				break;
			case "rotate" :
				// Handle rotate action
				image = getImageFromContext( context, attributes );

				int angle = IntegerCaster.cast( attributes.get( KeyDictionary.angle ) );

				image.rotate( angle );

				writeIfAble( image, attributes );
				break;
			case "write" :
				// Handle write action
				image = getImageFromContext( context, attributes );
				image.write( StringCaster.cast( attributes.get( KeyDictionary.destination ) ) );
				break;
			case "writeToBrowser" :

				imageService.writeToBrowser( context, getImageFromContext( context, attributes ), attributes );

				break;
			default :
				AtomicBoolean wasHandled = new AtomicBoolean( false );
				Runnable handleAction = () -> {
					wasHandled.set( true );
				};

				eventData = Struct.of(
				    KeyDictionary.image, getImageFromContext( context, attributes ),
				    Key.of( "action" ), action,
				    Key.of( "handleAction" ), handleAction
				);

				announce( ImageEvents.IMAGE_WRITE_TO_BROWSER, eventData );

				if ( !wasHandled.get() ) {
					throw new BoxRuntimeException( String.format( "Unknown action for Image component: %s", action ) );
				}
				break;
		}

		return DEFAULT_RETURN;
	}

	private void writeIfAble( BoxImage image, IStruct attributes ) {
		String	destination	= StringCaster.cast( attributes.get( Key.destination ) );
		boolean	overwrite	= BooleanCaster.cast( attributes.get( Key.overwrite ) );

		if ( !overwrite && FileSystemUtil.exists( destination ) ) {
			throw new BoxRuntimeException( String.format( "Unable to write image, destination exists: {}", destination ) );
		}

		image.write( destination );
	}

	private void putImageInContext( BoxImage image, IBoxContext context, IStruct attributes ) {
		StringCaster.attempt( attributes.get( KeyDictionary.name ) )
		    .ifPresent( ( n ) -> {
			    context.getDefaultAssignmentScope().assign( context, Key.of( n ), image );
		    } );
	}

	private BoxImage getImageFromContext( IBoxContext context, IStruct attributes ) {
		Object source = attributes.get( KeyDictionary.source );

		if ( source instanceof BoxImage i ) {
			return i;
		}

		if ( source instanceof String pathOrURL ) {
			try {
				boolean isBase64 = BooleanCaster.cast( attributes.get( KeyDictionary.isBase64 ) );

				if ( isBase64 ) {
					return BoxImage.fromBase64( pathOrURL );
				}

				// Check if it's a local file path or a URL
				if ( pathOrURL.startsWith( "http://" ) || pathOrURL.startsWith( "https://" ) || pathOrURL.startsWith( "file://" ) ) {
					logger.info( "Image URL detected: {}", pathOrURL );
					// It's a URL, pass it directly
					return new BoxImage( pathOrURL );
				} else {
					logger.info( "Assuming local file path for image: {}", pathOrURL );
					// It's a local file path, convert to proper file URI
					return new BoxImage( FileSystemUtil.createFileUri( pathOrURL ) );
				}
			} catch ( Exception e ) {
				throw new BoxRuntimeException( String.format( "Unable to read image from: %s", pathOrURL ), e );
			}
		}

		throw new BoxRuntimeException( "You must supply a source for the Image component" );
	}

}
