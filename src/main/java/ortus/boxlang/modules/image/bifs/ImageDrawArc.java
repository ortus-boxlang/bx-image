package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.BooleanCaster;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "drawArc" )
public class ImageDrawArc extends BIF {

	/**
	 * Constructor
	 */
	public ImageDrawArc() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.x, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.y, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.width, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.height, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.startAngle, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.archAngle, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "boolean", KeyDictionary.filled, false )
		};
	}

	/**
	 * Draws an arc on the image within the specified bounding rectangle, from the given start angle
	 * through the given arc angle.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name The image or name of variable that references an image to operate on.
	 *
	 * @argument.x The x coordinate of the upper-left corner of the bounding rectangle.
	 *
	 * @argument.y The y coordinate of the upper-left corner of the bounding rectangle.
	 *
	 * @argument.width The width of the bounding rectangle.
	 *
	 * @argument.height The height of the bounding rectangle.
	 *
	 * @argument.startAngle The starting angle of the arc in degrees.
	 *
	 * @argument.archAngle The angular extent of the arc in degrees.
	 *
	 * @argument.filled Whether to fill the arc shape. Defaults to false.
	 *
	 * @return The BoxImage instance.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		theImage.drawArc(
		    IntegerCaster.cast( arguments.get( KeyDictionary.x ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.y ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.width ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.height ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.startAngle ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.archAngle ) ),
		    BooleanCaster.cast( arguments.get( KeyDictionary.filled ) )
		);

		return theImage;
	}

}
