package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.util.FileSystemUtil;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class ImageWrite extends BIF {

	/**
	 * Constructor
	 */
	public ImageWrite() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "String", Key.path, Set.of( Validator.NON_EMPTY ) )
		};
	}

	/**
	 * Writes an image to a file. If no path is provided, writes to the original source path.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage	theImage		= arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		// If no path provided, write to original source path
		String		providedPath	= arguments.getAsString( Key.path );
		if ( providedPath == null || providedPath.isEmpty() ) {
			theImage.write();
		} else {
			String imagePath = providedPath.substring( 0, 4 ).equalsIgnoreCase( "http" ) ? providedPath
			    : FileSystemUtil.expandPath( context, providedPath ).absolutePath().toString();
			theImage.write( imagePath );
		}

		return theImage;
	}

}
