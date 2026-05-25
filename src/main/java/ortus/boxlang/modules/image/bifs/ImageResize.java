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
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "resize" )
public class ImageResize extends BIF {

	// TODO finish filling out interpolation options
	// TODO add member annotation
	/**
	 * Constructor
	 */
	public ImageResize() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name ),
		    new Argument( true, "numeric", KeyDictionary.width, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "numeric", KeyDictionary.height, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "String", KeyDictionary.interpolation, "bilinear" ),
		    new Argument( true, "numeric", KeyDictionary.blurFactor, 1, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) )
		};
	}

	/**
	 * Resizes the image to the specified width and height using the given interpolation method and blur factor.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name The image or name of variable that references an image to resize.
	 *
	 * @argument.width The new width in pixels.
	 *
	 * @argument.height The new height in pixels.
	 *
	 * @argument.interpolation The interpolation algorithm. One of "bilinear", "bicubic", or "nearest". Defaults to "bilinear".
	 *
	 * @argument.blurFactor The blur factor applied during resizing. Defaults to 1.
	 *
	 * @return The BoxImage instance after resizing.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		theImage.resize(
		    IntegerCaster.cast( arguments.get( KeyDictionary.width ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.height ) ),
		    arguments.getAsString( KeyDictionary.interpolation ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.blurFactor ) )
		);

		return theImage;
	}

}
