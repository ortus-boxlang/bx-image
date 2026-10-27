package ortus.boxlang.modules.image.bifs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import ortus.boxlang.modules.image.util.ImageGuard;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class IsImageFile extends BIF {

	/**
	 * Constructor
	 */
	public IsImageFile() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", Key.value, Set.of( Validator.REQUIRED ) )
		};
	}

	/**
	 * Determines whether the given file path or URL points to a valid image file.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.value The file path or URL of the image to check.
	 *
	 * @return True if the path points to a readable image, false otherwise.
	 */
	public Boolean _invoke( IBoxContext context, ArgumentsScope arguments ) {
		String	passedInPath	= StringCaster.cast( arguments.get( Key.value ) );
		String	imagePath		= passedInPath.substring( 0, 4 ).equalsIgnoreCase( "http" ) ? passedInPath
		    : FileSystemUtil.expandPath( context, passedInPath ).absolutePath().toString();

		try {
			URI		path	= new URI( imagePath );
			String	scheme	= path.getScheme();

			if ( path.isAbsolute()
			    && scheme != null
			    && ( scheme.equals( "http" )
			        || scheme.equals( "https" ) ) ) {
				try ( InputStream in = path.toURL().openStream() ) {
					return isReadable( ImageGuard.readBounded( in, ImageGuard.getMaxBytes(), passedInPath ) );
				}
			}
		} catch ( URISyntaxException e ) {
			// pass
		} catch ( BoxRuntimeException e ) {
			// Over the size limit: not an acceptable image
			return false;
		} catch ( IOException e ) {
			throw new BoxRuntimeException( "Unable to read image from " + passedInPath, e );
		}

		try {
			File file = FileSystemUtil.expandPath( context, passedInPath ).absolutePath().toFile();
			if ( !file.isFile() ) {
				return false;
			}
			// Answers from the header only, so nothing is decoded
			try ( ImageInputStream input = ImageIO.createImageInputStream( file ) ) {
				return input != null && isWithinLimits( ImageGuard.probeDimensions( input ) );
			}
		} catch ( IOException e ) {
			return false;
		}
	}

	private static boolean isReadable( byte[] data ) throws IOException {
		return isWithinLimits( ImageGuard.probeDimensions( data ) );
	}

	private static boolean isWithinLimits( long[] dimensions ) {
		return dimensions != null && ImageGuard.isWithinLimit( dimensions, ImageGuard.getMaxPixels() );
	}

}
