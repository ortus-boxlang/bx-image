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
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "flip" )
public class ImageFlip extends BIF {

	/**
	 * Constructor
	 */
	public ImageFlip() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "string", KeyDictionary.transpose, Set.of(
		        Validator.REQUIRED,
		        Validator.valueOneOf( "horizontal", "vertical", "diagonal", "antidiagonal", "90", "180", "270" )
		    ) )
		};
	}

	/**
	 * Flips or transposes an image.
	 *
	 * Supported transpose operations:
	 * - "horizontal" : Flip the image horizontally (mirror left-right)
	 * - "vertical" : Flip the image vertically (mirror top-bottom)
	 * - "diagonal" : Transpose along the main diagonal (equivalent to 90° rotation + horizontal flip)
	 * - "antidiagonal" : Transpose along the anti-diagonal (equivalent to 270° rotation + horizontal flip)
	 * - "90" : Rotate 90 degrees clockwise
	 * - "180" : Rotate 180 degrees
	 * - "270" : Rotate 270 degrees clockwise (or 90 degrees counter-clockwise)
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @return The flipped/transposed BoxImage instance for method chaining
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		return theImage.transpose( arguments.getAsString( KeyDictionary.transpose ) );
	}

}
