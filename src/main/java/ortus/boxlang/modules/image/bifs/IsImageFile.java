package ortus.boxlang.modules.image.bifs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.imageio.ImageIO;

import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;
import ortus.boxlang.runtime.util.ResolvedFilePath;
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
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public Boolean _invoke( IBoxContext context, ArgumentsScope arguments ) {
		String	passedInPath	= StringCaster.cast( arguments.get( Key.value ) );
		String	imagePath		= FileSystemUtil.expandPath( context, passedInPath ).absolutePath().toString();
		try {
			URI		path	= new URI( imagePath );
			String	scheme	= path.getScheme();

			if ( path.isAbsolute()
			    && scheme != null
			    && ( scheme.equals( "http" )
			        || scheme.equals( "https" ) ) ) {
				return ImageIO.read( path.toURL() ) != null;
			}
		} catch ( URISyntaxException e ) {
			// pass
		} catch ( IOException e ) {
			throw new BoxRuntimeException( "Unable to read image from " + passedInPath, e );
		}

		try {
			ResolvedFilePath resolved = FileSystemUtil.expandPath( context, passedInPath );

			return ImageIO.read( resolved.absolutePath().toFile() ) != null;
		} catch ( IOException e ) {
			return false;
		}
	}

}
