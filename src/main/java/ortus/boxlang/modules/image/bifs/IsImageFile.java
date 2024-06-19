package ortus.boxlang.modules.image.bifs;

import java.io.File;
import java.net.URI;
import java.util.Set;

import javax.imageio.ImageIO;

import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
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
		try {
			URI		path	= new URI( StringCaster.cast( arguments.get( Key.value ) ) );
			String	scheme	= path.getScheme();

			if ( path.isAbsolute()
			    && scheme != null
			    && ( scheme.equals( "http" )
			        || scheme.equals( "https" ) ) ) {
				return ImageIO.read( path.toURL() ) != null;
			}

			return ImageIO.read( new File( path.toString() ) ) != null;
		} catch ( Exception e ) {
			return false;
		}
	}

}
