package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.BoxImage.Dimension;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.DoubleCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "shear" )
public class ImageShear extends BIF {

	// TODO I think this should be changed to shear( x, y );
	/**
	 * Constructor
	 */
	public ImageShear() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name ),
		    new Argument( true, "numeric", KeyDictionary.amount, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "string", KeyDictionary.direction, "horizontal",
		        Set.of( Validator.NON_EMPTY, Validator.valueOneOf( "horizontal", "vertical" ) ) ),
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		theImage.shear(
		    DoubleCaster.cast( arguments.get( KeyDictionary.amount ) ),
		    arguments.getAsString( KeyDictionary.direction ).equalsIgnoreCase( "horizontal" ) ? Dimension.WIDTH : Dimension.HEIGHT
		);

		return theImage;
	}

}
