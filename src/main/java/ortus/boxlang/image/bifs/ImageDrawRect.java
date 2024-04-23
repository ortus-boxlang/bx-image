package ortus.boxlang.image.bifs;

import java.util.Set;

import ortus.boxlang.image.BoxImage;
import ortus.boxlang.image.ImageKeys;
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
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "drawRect" )
public class ImageDrawRect extends BIF {

	/**
	 * Constructor
	 */
	public ImageDrawRect() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", ImageKeys.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", ImageKeys.x, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "numeric", ImageKeys.y, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "numeric", ImageKeys.width, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "numeric", ImageKeys.height, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( false, "boolean", ImageKeys.filled, false, Set.of( Validator.NON_EMPTY ) ),
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

		theImage.drawRect(
		    IntegerCaster.cast( arguments.get( ImageKeys.x ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.y ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.width ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.height ) ),
		    arguments.getAsBoolean( ImageKeys.filled )
		);

		return theImage;
	}

}
