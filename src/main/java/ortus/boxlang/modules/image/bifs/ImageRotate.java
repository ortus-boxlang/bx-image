package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageKeys;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class ImageRotate extends BIF {

	// TODO the docs don't seem correct to me https://cfdocs.org/imagerotate
	// it says there should be x,y arguments but I wasn't able to get them working in either lucee or adobe
	/**
	 * Constructor
	 */
	public ImageRotate() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", ImageKeys.name ),
		    new Argument( true, "numeric", ImageKeys.angle, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( ImageKeys.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( ImageKeys.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( ImageKeys.name ) );

		theImage.rotate(
		    IntegerCaster.cast( arguments.get( ImageKeys.angle ) )
		);

		return theImage;
	}

}
