package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageKeys;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.ArrayCaster;
import ortus.boxlang.runtime.dynamic.casters.BooleanCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "drawLines" )
public class ImageDrawLines extends BIF {

	/**
	 * Constructor
	 */
	public ImageDrawLines() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", ImageKeys.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "array", ImageKeys.xCoords, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "array", ImageKeys.yCoords, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( false, "boolean", ImageKeys.isPolygon, false ),
		    new Argument( false, "boolean", ImageKeys.filled, false )
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

		theImage.drawLines(
		    ArrayCaster.cast( arguments.get( ImageKeys.xCoords ) ),
		    ArrayCaster.cast( arguments.get( ImageKeys.yCoords ) ),
		    BooleanCaster.cast( arguments.get( ImageKeys.isPolygon ) ),
		    BooleanCaster.cast( arguments.get( ImageKeys.filled ) )
		);

		return theImage;
	}

}
