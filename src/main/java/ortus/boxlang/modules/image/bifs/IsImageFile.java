package ortus.boxlang.modules.image.bifs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class IsImageFile extends BIF {

	protected static final Logger logger = LoggerFactory.getLogger( IsImageFile.class );

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
		String passedInPath = StringCaster.cast( arguments.get( Key.value ) );
		try {
			URI		path	= new URI( passedInPath );
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
			return ImageIO.read( new File( passedInPath ) ) != null;
		} catch ( IOException e ) {
			return false;
		}
	}

}
