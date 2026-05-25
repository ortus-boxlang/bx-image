package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "drawLine" )
public class ImageDrawLine extends BIF {

	/**
	 * Constructor
	 */
	public ImageDrawLine() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.x1, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "numeric", KeyDictionary.y1, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "numeric", KeyDictionary.x2, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "numeric", KeyDictionary.y2, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) )
		};
	}

	/**
	 * Draws a straight line between two points on the image.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name The image or name of variable that references an image to operate on.
	 *
	 *                @argument.x1 The x coordinate of the start point.
	 *
	 *                @argument.y1 The y coordinate of the start point.
	 *
	 *                @argument.x2 The x coordinate of the end point.
	 *
	 *                @argument.y2 The y coordinate of the end point.
	 *
	 * @return The BoxImage instance.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		theImage.drawLine(
		    IntegerCaster.cast( arguments.get( KeyDictionary.x1 ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.y1 ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.x2 ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.y2 ) )
		);

		return theImage;
	}

}
