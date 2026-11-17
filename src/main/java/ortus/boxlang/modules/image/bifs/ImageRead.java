package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class ImageRead extends BIF {

	/**
	 * Constructor
	 */
	public ImageRead() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "String", Key.path, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( false, "numeric", KeyDictionary.width ),
		    new Argument( false, "numeric", KeyDictionary.height )
		};
	}

	/**
	 * Reads an image from a file path or URL and returns a BoxImage object.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.path The file path or URL of the image to read. SVG files are rendered to a raster image.
	 *
	 * @argument.width Optional width in pixels. An SVG is rendered straight at this size so it stays sharp; other formats are resized after
	 *                 decoding. When only one of width or height is given, the other keeps the aspect ratio.
	 *
	 * @argument.height Optional height in pixels. See width.
	 *
	 * @return A new BoxImage loaded from the specified path.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		String	providedPath	= arguments.getAsString( Key.path );
		String	imagePath		= BoxImage.isURISource( providedPath ) ? providedPath
		    : FileSystemUtil.expandPath( context, providedPath ).absolutePath().toString();
		Object	width			= arguments.get( KeyDictionary.width );
		Object	height			= arguments.get( KeyDictionary.height );
		try {
			return new BoxImage(
			    imagePath,
			    width == null ? null : IntegerCaster.cast( width ),
			    height == null ? null : IntegerCaster.cast( height )
			);
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to load image: " + arguments.getAsString( Key.path ), e );
		}
	}

}
