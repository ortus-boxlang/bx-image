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

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageEvents;
import ortus.boxlang.modules.image.bifs.ImageQRCodeGenerate;
import ortus.boxlang.modules.image.services.ImageService;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.modules.image.util.QRCodeUtil;
import ortus.boxlang.runtime.components.Attribute;
import ortus.boxlang.runtime.components.BoxComponent;
import ortus.boxlang.runtime.components.Component;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.BooleanCaster;
import ortus.boxlang.runtime.dynamic.casters.DoubleCaster;
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

	/**
	 * Creates the Image component and declares supported attributes.
	 *
	 * <p>
	 * This component mirrors CFML style image operations through an action-based
	 * attribute contract. The declared attributes are validated by the component
	 * framework before execution.
	 * </p>
	 */
	public Image() {
		super();
		declaredAttributes	= new Attribute[] {
		    new Attribute( KeyDictionary.action, "string", Set.of( Validator.REQUIRED, Validator.valueOneOf(
		        "border",
		        "captcha",
		        "convert",
		        "info",
		        "qrcode",
		        "qrread",
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
		    new Attribute( KeyDictionary.interpolation, "string" ),
		    new Attribute( KeyDictionary.size, "numeric" ),
		    new Attribute( KeyDictionary.errorCorrection, "string" ),
		    new Attribute( KeyDictionary.quietZone, "numeric" ),
		    new Attribute( KeyDictionary.foreground, "string" ),
		    new Attribute( KeyDictionary.background, "string" ),
		    new Attribute( KeyDictionary.logo, "any" ),
		    new Attribute( KeyDictionary.logoScale, "numeric" ),
		    new Attribute( KeyDictionary.tryHarder, "boolean", true )
		};

		logger				= imageService.getLogger();
	}

	/**
	 * Performs image operations based on the {@code action} attribute.
	 *
	 * <p>
	 * Supported actions: {@code border}, {@code captcha}, {@code convert}, {@code info}, {@code qrcode}, {@code qrread},
	 * {@code read}, {@code resize}, {@code rotate}, {@code write}, {@code writeToBrowser}.
	 * </p>
	 *
	 * @param context        The context in which the Component is being invoked
	 * @param attributes     The attributes to the Component
	 * @param body           The body of the Component
	 * @param executionState The execution state of the Component
	 *
	 * @attribute.action The image operation to perform. Required. One of:
	 *                   {@code border}, {@code captcha}, {@code convert}, {@code info}, {@code qrcode}, {@code qrread},
	 *                   {@code read}, {@code resize}, {@code rotate}, {@code write}, {@code writeToBrowser}.
	 *
	 * @attribute.source The image variable, file path, or URL to operate on.
	 *                   Required for all actions except {@code captcha}.
	 *
	 * @attribute.name Variable name in which to store the resulting image.
	 *                 Used by {@code read}, {@code resize}, {@code border}, {@code captcha}.
	 *
	 * @attribute.destination File path to write the image to.
	 *                        Used by {@code write}, {@code convert}, {@code rotate}, {@code captcha}.
	 *
	 * @attribute.overwrite When {@code true}, overwrites an existing file at {@code destination}.
	 *                      Defaults to {@code false}.
	 *
	 * @attribute.width Target width in pixels. Used by {@code resize} and {@code captcha}.
	 *
	 * @attribute.height Target height in pixels. Used by {@code resize} and {@code captcha}.
	 *
	 * @attribute.angle Rotation angle in degrees. Used by {@code rotate}.
	 *
	 * @attribute.color Border color as a named color or hex value. Used by {@code border}.
	 *
	 * @attribute.thickness Border thickness in pixels. Used by {@code border}.
	 *
	 * @attribute.text The text string to render in the CAPTCHA image.
	 *                 Required for {@code captcha} action.
	 *
	 * @attribute.difficulty CAPTCHA distortion level: {@code low}, {@code medium}, or {@code high}.
	 *                       Defaults to {@code low}. Used by {@code captcha}.
	 *
	 * @attribute.fonts Comma-separated font family names for CAPTCHA text rendering,
	 *                  e.g. {@code "Arial,Verdana,Georgia"}. Used by {@code captcha}.
	 *
	 * @attribute.fontSize Font size in points for CAPTCHA text. Defaults to 24.
	 *                     Used by {@code captcha}.
	 *
	 * @attribute.structName Variable name in which to store the image info struct.
	 *                       Used by {@code info}.
	 *
	 * @attribute.format Image format for conversion, e.g. {@code png}, {@code jpg}.
	 *                   Used by {@code convert} and {@code writeToBrowser}.
	 *
	 * @attribute.quality Output quality (0–1) for lossy formats such as JPEG.
	 *                    Used by {@code write} and {@code writeToBrowser}.
	 *
	 * @attribute.isBase64 When {@code true}, treats the {@code source} value as a Base64-encoded
	 *                     image string. Used by {@code read}.
	 *
	 * @attribute.interpolation Interpolation algorithm for resizing.
	 *                          One of {@code bilinear}, {@code bicubic}, {@code nearestNeighbor}.
	 *                          Defaults to {@code bilinear}. Used by {@code resize}.
	 *
	 * @attribute.size Edge length in pixels of the square QR code, 16 to 4096. Defaults to 125. Used by {@code qrcode}.
	 *
	 * @attribute.errorCorrection QR code error correction level: {@code L}, {@code M}, {@code Q} or {@code H}.
	 *                            Defaults to {@code M}. Always {@code H} when a logo is used. Used by {@code qrcode}.
	 *
	 * @attribute.quietZone Light margin around the QR code in modules, 0 to 32. Defaults to 4. Used by {@code qrcode}.
	 *
	 * @attribute.foreground Color of the dark QR code modules, as a color name or hex code. Defaults to {@code black}.
	 *                       Used by {@code qrcode}.
	 *
	 * @attribute.background Color of the light QR code modules, as a color name or hex code. Defaults to {@code white}.
	 *                       Used by {@code qrcode}.
	 *
	 * @attribute.logo Image to draw in the center of the QR code. A BoxImage, file path or URL. Used by {@code qrcode}.
	 *
	 * @attribute.logoScale Fraction of the QR code's edge length the logo covers, up to 0.3. Defaults to 0.2.
	 *                      Used by {@code qrcode}.
	 *
	 * @attribute.tryHarder When {@code true} (the default), spends more effort finding the QR code and reads
	 *                      light-on-dark codes. Used by {@code qrread}.
	 *
	 */
	public BodyResult _invoke( IBoxContext context, IStruct attributes, ComponentBody body, IStruct executionState ) {
		String		action				= attributes.getAsString( KeyDictionary.action );
		String		normalizedAction	= action.trim().toLowerCase( Locale.ROOT );
		BoxImage	image				= null;
		IStruct		eventData			= null;

		switch ( normalizedAction ) {
			case "border" :
				image = getImageFromContext( context, attributes );
				String color = attributes.getAsString( KeyDictionary.color );
				int thickness = IntegerCaster.cast( attributes.get( KeyDictionary.thickness ) );
				image.addBorder( thickness, color );

				putImageInContext( image, context, attributes );

				break;
			case "captcha" : {
				String captchaText = attributes.getAsString( KeyDictionary.text );
				if ( captchaText == null || captchaText.isEmpty() ) {
					throw new BoxRuntimeException( "The text attribute is required for the captcha action" );
				}
				int		captchaWidth		= attributes.get( KeyDictionary.width ) != null
				    ? IntegerCaster.cast( attributes.get( KeyDictionary.width ) )
				    : 200;
				int		captchaHeight		= attributes.get( KeyDictionary.height ) != null
				    ? IntegerCaster.cast( attributes.get( KeyDictionary.height ) )
				    : 75;
				int		captchaFontSize		= attributes.get( KeyDictionary.fontSize ) != null
				    ? IntegerCaster.cast( attributes.get( KeyDictionary.fontSize ) )
				    : 24;
				String	captchaDifficulty	= attributes.getAsString( KeyDictionary.difficulty );
				if ( captchaDifficulty == null || captchaDifficulty.isEmpty() )
					captchaDifficulty = "low";
				String		fontsAttr		= attributes.getAsString( KeyDictionary.fonts );
				String[]	captchaFonts	= ( fontsAttr == null || fontsAttr.isEmpty() )
				    ? new String[ 0 ]
				    : fontsAttr.split( "\\s*,\\s*" );

				image = BoxImage.generateCaptcha( captchaText, captchaWidth, captchaHeight, captchaFontSize, captchaDifficulty, captchaFonts );

				String captchaDest = attributes.getAsString( KeyDictionary.destination );
				if ( captchaDest != null && !captchaDest.isEmpty() ) {
					boolean captchaOverwrite = BooleanCaster.cast( attributes.get( KeyDictionary.overwrite ) );
					if ( !captchaOverwrite && FileSystemUtil.exists( captchaDest ) ) {
						throw new BoxRuntimeException( "Unable to write captcha image, destination exists: " + captchaDest );
					}
					image.write( captchaDest );
				}

				putImageInContext( image, context, attributes );

				// When no destination or name is given, stream directly to browser (matches cfimage behavior)
				String	captchaName			= attributes.getAsString( KeyDictionary.name );
				boolean	hasCaptchaOutput	= ( captchaDest != null && !captchaDest.isEmpty() )
				    || ( captchaName != null && !captchaName.isEmpty() );
				if ( !hasCaptchaOutput ) {
					imageService.writeToBrowser( context, image, attributes );
				}
				break;
			}
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
			case "qrcode" : {
				String qrText = attributes.getAsString( KeyDictionary.text );
				if ( qrText == null || qrText.isEmpty() ) {
					throw new BoxRuntimeException( "The text attribute is required for the qrcode action" );
				}

				String	qrFormat	= attributes.getAsString( KeyDictionary.format );
				String	qrCorrect	= attributes.getAsString( KeyDictionary.errorCorrection );
				String	qrFore		= attributes.getAsString( KeyDictionary.foreground );
				String	qrBack		= attributes.getAsString( KeyDictionary.background );

				image = BoxImage.generateQRCode(
				    qrText,
				    attributes.get( KeyDictionary.size ) != null ? IntegerCaster.cast( attributes.get( KeyDictionary.size ) ) : QRCodeUtil.DEFAULT_SIZE,
				    qrFormat == null || qrFormat.isEmpty() ? "png" : qrFormat,
				    qrCorrect == null || qrCorrect.isEmpty() ? "M" : qrCorrect,
				    attributes.get( KeyDictionary.quietZone ) != null ? IntegerCaster.cast( attributes.get( KeyDictionary.quietZone ) )
				        : QRCodeUtil.DEFAULT_QUIET_ZONE,
				    qrFore == null || qrFore.isEmpty() ? "black" : qrFore,
				    qrBack == null || qrBack.isEmpty() ? "white" : qrBack,
				    ImageQRCodeGenerate.resolveLogo( context, attributes.get( KeyDictionary.logo ) ),
				    attributes.get( KeyDictionary.logoScale ) != null ? DoubleCaster.cast( attributes.get( KeyDictionary.logoScale ) )
				        : QRCodeUtil.DEFAULT_LOGO_SCALE
				);

				String qrDest = attributes.getAsString( KeyDictionary.destination );
				if ( qrDest != null && !qrDest.isEmpty() ) {
					boolean qrOverwrite = BooleanCaster.cast( attributes.get( KeyDictionary.overwrite ) );
					if ( !qrOverwrite && FileSystemUtil.exists( qrDest ) ) {
						throw new BoxRuntimeException( "Unable to write QR code image, destination exists: " + qrDest );
					}
					image.write( qrDest );
				}

				putImageInContext( image, context, attributes );

				// When no destination or name is given, stream directly to browser (matches the captcha action)
				String	qrName		= attributes.getAsString( KeyDictionary.name );
				boolean	hasQrOutput	= ( qrDest != null && !qrDest.isEmpty() ) || ( qrName != null && !qrName.isEmpty() );
				if ( !hasQrOutput ) {
					imageService.writeToBrowser( context, image, attributes );
				}
				break;
			}
			case "qrread" : {
				String qrReadName = attributes.getAsString( KeyDictionary.name );
				if ( qrReadName == null || qrReadName.isEmpty() ) {
					throw new BoxRuntimeException( "The name attribute is required for the qrread action, it receives the decoded text" );
				}

				image = getImageFromContext( context, attributes );
				String decoded = image.readQRCode( BooleanCaster.cast( attributes.get( KeyDictionary.tryHarder ) ) );
				context.getDefaultAssignmentScope().assign( context, Key.of( qrReadName ), decoded );
				break;
			}
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
			case "writetobrowser" :

				imageService.writeToBrowser( context, getImageFromContext( context, attributes ), attributes );

				break;
			default :
				AtomicBoolean wasHandled = new AtomicBoolean( false );
				Runnable handleAction = () -> {
					wasHandled.set( true );
				};

				eventData = attributes.get( KeyDictionary.source ) == null
				    ? Struct.of(
				        Key.of( "action" ), action,
				        Key.of( "handleAction" ), handleAction
				    )
				    : Struct.of(
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

	/**
	 * Writes the provided image to the requested destination when allowed.
	 *
	 * <p>
	 * If overwrite is false and the destination already exists, this method throws
	 * an exception to prevent accidental file replacement.
	 * </p>
	 *
	 * @param image      The image to write
	 * @param attributes Component attributes that provide destination and overwrite
	 *                   values
	 *
	 * @throws BoxRuntimeException When overwrite is disabled and destination exists
	 */
	private void writeIfAble( BoxImage image, IStruct attributes ) {
		String	destination	= StringCaster.cast( attributes.get( Key.destination ) );
		boolean	overwrite	= BooleanCaster.cast( attributes.get( Key.overwrite ) );

		if ( !overwrite && FileSystemUtil.exists( destination ) ) {
			throw new BoxRuntimeException( String.format( "Unable to write image, destination exists: {}", destination ) );
		}

		image.write( destination );
	}

	/**
	 * Stores the image in the default assignment scope when a target name is provided.
	 *
	 * @param image      The image to place in the context
	 * @param context    The current execution context
	 * @param attributes Component attributes that may contain a target name
	 */
	private void putImageInContext( BoxImage image, IBoxContext context, IStruct attributes ) {
		StringCaster.attempt( attributes.get( KeyDictionary.name ) )
		    .ifPresent( ( n ) -> {
			    context.getDefaultAssignmentScope().assign( context, Key.of( n ), image );
		    } );
	}

	/**
	 * Resolves the source attribute into a BoxImage instance.
	 *
	 * <p>
	 * Supported source inputs are:
	 * </p>
	 * <ul>
	 * <li>An existing BoxImage instance</li>
	 * <li>A Base64-encoded image string when isBase64=true</li>
	 * <li>A URL string (http, https, file)</li>
	 * <li>A local file path string</li>
	 * </ul>
	 *
	 * @param context    The current execution context
	 * @param attributes Component attributes containing source and optional flags
	 *
	 * @return The resolved BoxImage
	 *
	 * @throws BoxRuntimeException When source is missing or cannot be read
	 */
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
					logger.debug( "Image URL detected: {}", pathOrURL );
					// It's a URL, pass it directly
					return new BoxImage( pathOrURL );
				} else {
					logger.debug( "Assuming local file path for image: {}", pathOrURL );
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
