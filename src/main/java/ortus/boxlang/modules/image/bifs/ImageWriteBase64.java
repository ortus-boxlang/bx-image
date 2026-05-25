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
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class ImageWriteBase64 extends BIF {

	/**
	 * Constructor
	 */
	public ImageWriteBase64() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name ),
		    new Argument( true, "String", Key.format, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) )
		};
	}

	/**
	 * Writes the image to a Base64-encoded string in the specified format.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name The image or name of variable that references an image to encode.
	 *
	 * @argument.format The image format (e.g., "png", "jpg", "gif").
	 *
	 * @return A Base64-encoded string representation of the image.
	 */
	public String _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );
		try {
			return theImage.toBase64String( arguments.getAsString( Key.format ) );
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to create baes64 string", e );
		}
	}

}
