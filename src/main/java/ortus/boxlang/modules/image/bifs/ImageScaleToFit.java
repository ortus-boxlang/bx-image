package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageKeys;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.CastAttempt;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "scaleToFit" )
public class ImageScaleToFit extends BIF {

	// TODO finish filling out interpolation options
	// TODO add blur factor

	/**
	 * Constructor
	 */
	public ImageScaleToFit() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", ImageKeys.name ),
		    new Argument( true, "any", ImageKeys.width ),
		    new Argument( true, "any", ImageKeys.height ),
		    new Argument( true, "String", ImageKeys.interpolation, "bilinear" )
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage				theImage	= arguments.get( ImageKeys.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( ImageKeys.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( ImageKeys.name ) );

		CastAttempt<Integer>	width		= IntegerCaster.attempt( arguments.get( ImageKeys.width ) );
		CastAttempt<Integer>	height		= IntegerCaster.attempt( arguments.get( ImageKeys.height ) );

		Integer					size		= width.getOrSupply( () -> height.getOrSupply( () -> 0 ) );

		theImage.scaleToFit(
		    size,
		    width.wasSuccessful() ? BoxImage.Dimension.WIDTH : BoxImage.Dimension.HEIGHT,
		    arguments.getAsString( ImageKeys.interpolation )
		);

		return theImage;
	}

}
