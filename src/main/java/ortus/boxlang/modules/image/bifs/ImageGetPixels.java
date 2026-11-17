package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.Array;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "getPixels" )
public class ImageGetPixels extends BIF {

	/**
	 * Constructor
	 */
	public ImageGetPixels() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "string", KeyDictionary.mode, "luma" )
		};
	}

	/**
	 * Returns the image's pixels as an array of rows, from top to bottom.
	 * The same function exists on the MatchBox backend and returns the same values.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name The image or name of variable that references an image to operate on.
	 *
	 * @argument.mode "luma" (default): each pixel is a number from 0 to 255, (77r + 150g + 29b + 128) >> 8, ignoring alpha.
	 *                "rgba": each pixel is an array [r, g, b, a] of numbers from 0 to 255.
	 *
	 * @return An array of rows, each an array of pixels.
	 */
	public Array _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		return theImage.getPixels( arguments.getAsString( KeyDictionary.mode ) );
	}

}
