package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageKeys;
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
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "copy" )
public class ImageCopy extends BIF {

	/**
	 * Constructor
	 */
	public ImageCopy() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", ImageKeys.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", ImageKeys.x, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", ImageKeys.y, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", ImageKeys.width, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", ImageKeys.height, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "numeric", ImageKeys.dx, 0 ),
		    new Argument( false, "numeric", ImageKeys.dy, 0 )
		};
	}

	/**
	 * Draws a rectangle using the configured background color.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 * 
	 * @argument.name The image or name of variable that references an image to operate on.
	 * 
	 * @argument.x The x coordinate of the rectangular area of the image to copy.
	 * 
	 * @argument.y The y coordinate of the rectangular area of the image to copy.
	 * 
	 * @argument.width The width of the the rectangular area of the image to copy.
	 * 
	 * @argument.height The height of the the rectangular area of the image to copy.
	 * 
	 * @argument.dx The amount to shift the x coordinate when drawing the copied area.
	 * 
	 * @argument.dy The amount to shift the y coordinate when drawing the copied area.
	 * 
	 * @return The BoxImage instance.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( ImageKeys.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( ImageKeys.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( ImageKeys.name ) );

		return theImage.copy(
		    IntegerCaster.cast( arguments.get( ImageKeys.x ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.y ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.width ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.height ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.dx ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.dy ) )
		);
	}

}
