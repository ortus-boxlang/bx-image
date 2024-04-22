package ortus.boxlang.image.bifs;

import java.io.File;
import java.util.Set;

import org.apache.commons.imaging.Imaging;

import ortus.boxlang.image.BoxImage;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class ImageRead extends BIF {

	/**
	 * Constructor
	 */
	public ImageRead() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "String", Key.path, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) )
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		try {
			return new BoxImage( Imaging.getBufferedImage( new File( arguments.getAsString( Key.path ) ) ) );
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to load image: " + arguments.getAsString( Key.path ), e );
		}
	}

}
