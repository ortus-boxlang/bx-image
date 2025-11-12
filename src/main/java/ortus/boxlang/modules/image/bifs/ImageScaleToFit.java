package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
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
		    new Argument( true, "any", KeyDictionary.name ),
		    new Argument( false, "any", KeyDictionary.width ),
		    new Argument( false, "any", KeyDictionary.height ),
		    new Argument( false, "String", KeyDictionary.interpolation, "bilinear" )
		};
	}

	/**
	 * Scales an image to fit within specified dimensions while maintaining aspect ratio.
	 * - If both width and height are provided: fits within both dimensions
	 * - If only width is provided: fits to width maintaining aspect ratio (uses WIDTH dimension)
	 * - If only height is provided: fits to height maintaining aspect ratio (uses HEIGHT dimension)
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage				theImage		= arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		CastAttempt<Integer>	width			= IntegerCaster.attempt( arguments.get( KeyDictionary.width ) );
		CastAttempt<Integer>	height			= IntegerCaster.attempt( arguments.get( KeyDictionary.height ) );
		String					interpolation	= arguments.getAsString( KeyDictionary.interpolation );

		// Both width and height provided - fit within both dimensions
		if ( width.wasSuccessful() && height.wasSuccessful() ) {
			theImage.scaleToFit( width.get(), height.get(), interpolation );
		}
		// Only width provided - fit to width (original behavior for backward compatibility)
		else if ( width.wasSuccessful() ) {
			Integer size = width.get();
			theImage.scaleToFit( size, BoxImage.Dimension.WIDTH, interpolation );
		}
		// Only height provided - fit to height
		else if ( height.wasSuccessful() ) {
			Integer size = height.get();
			theImage.scaleToFit( size, BoxImage.Dimension.HEIGHT, interpolation );
		}

		return theImage;
	}

}
