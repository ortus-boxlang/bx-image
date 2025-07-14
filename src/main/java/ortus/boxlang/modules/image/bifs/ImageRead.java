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
		String imagePath = FileSystemUtil.expandPath( context, arguments.getAsString( Key.path ) ).absolutePath().toString();
		try {
			return new BoxImage( imagePath );
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to load image: " + arguments.getAsString( Key.path ), e );
		}
	}

}
