package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.BoxImage.Dimension;
import ortus.boxlang.modules.image.ImageKeys;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.DoubleCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class ImageShear extends BIF {

	// TODO finish filling out interpolation options
	// TODO add member annotation
	/**
	 * Constructor
	 */
	public ImageShear() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", ImageKeys.name ),
		    new Argument( true, "numeric", ImageKeys.amount, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "string", ImageKeys.direction, "horizontal", Set.of( Validator.NON_EMPTY, Validator.valueOneOf( "horizontal", "vertical" ) ) ),
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

		theImage.shear(
		    DoubleCaster.cast( arguments.get( ImageKeys.amount ) ),
		    arguments.getAsString( ImageKeys.direction ).equalsIgnoreCase( "horizontal" ) ? Dimension.WIDTH : Dimension.HEIGHT
		);

		return theImage;
	}

}
