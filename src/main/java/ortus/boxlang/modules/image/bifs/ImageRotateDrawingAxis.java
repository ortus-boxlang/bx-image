package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.DoubleCaster;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "rotateDrawingAxis" )
public class ImageRotateDrawingAxis extends BIF {

	// TODO the docs don't seem correct to me https://cfdocs.org/imagerotate
	// it says there should be x,y arguments but I wasn't able to get them working in either lucee or adobe
	/**
	 * Constructor
	 */
	public ImageRotateDrawingAxis() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name ),
		    new Argument( true, "numeric", KeyDictionary.angle, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "numeric", KeyDictionary.x, 0 ),
		    new Argument( true, "numeric", KeyDictionary.y, 0 )
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

		theImage.rotateDrawingAxis(
		    DoubleCaster.cast( arguments.get( KeyDictionary.angle ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.x ) ),
		    IntegerCaster.cast( arguments.get( KeyDictionary.y ) )
		);

		return theImage;
	}

}
