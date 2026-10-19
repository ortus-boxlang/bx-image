package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.ArrayCaster;
import ortus.boxlang.runtime.dynamic.casters.FloatCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "setDrawingGradient" )
public class ImageSetDrawingGradient extends BIF {

	/**
	 * Constructor
	 */
	public ImageSetDrawingGradient() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.x1, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.y1, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.x2, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.y2, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "array", KeyDictionary.colors, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( false, "array", KeyDictionary.fractions )
		};
	}

	/**
	 * Sets a linear gradient as the paint for subsequent fill, stroke and text operations.
	 * Call setDrawingColor to go back to a solid color.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name The image or name of variable that references an image to operate on.
	 *
	 * @argument.x1 The x coordinate of the gradient start point.
	 *
	 * @argument.y1 The y coordinate of the gradient start point.
	 *
	 * @argument.x2 The x coordinate of the gradient end point.
	 *
	 * @argument.y2 The y coordinate of the gradient end point.
	 *
	 * @argument.colors An array of two or more color names or hex codes.
	 *
	 * @argument.fractions Optional array of positions between 0 and 1, strictly increasing, one per color. Defaults to evenly spaced.
	 *
	 * @return The BoxImage instance.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		theImage.setDrawingGradient(
		    FloatCaster.cast( arguments.get( KeyDictionary.x1 ) ),
		    FloatCaster.cast( arguments.get( KeyDictionary.y1 ) ),
		    FloatCaster.cast( arguments.get( KeyDictionary.x2 ) ),
		    FloatCaster.cast( arguments.get( KeyDictionary.y2 ) ),
		    ArrayCaster.cast( arguments.get( KeyDictionary.colors ) ),
		    arguments.get( KeyDictionary.fractions ) == null ? null : ArrayCaster.cast( arguments.get( KeyDictionary.fractions ) )
		);

		return theImage;
	}

}
