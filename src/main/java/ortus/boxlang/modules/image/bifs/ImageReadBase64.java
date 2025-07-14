package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class ImageReadBase64 extends BIF {

	/**
	 * Constructor
	 */
	public ImageReadBase64() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "String", Key.string, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) )
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		String	providedPath	= arguments.getAsString( Key.string );
		String	imagePath		= providedPath.substring( 0, 4 ).equalsIgnoreCase( "http" ) ? providedPath
		    : FileSystemUtil.expandPath( context, providedPath ).absolutePath().toString();
		try {
			return BoxImage.fromBase64( imagePath );
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to read from base64 input", e );
		}
	}

}
