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
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "addBorder" )
public class ImageAddBorder extends BIF {

	// TODO finish bordertypes

	/**
	 * Constructor
	 */
	public ImageAddBorder() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.thickness, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( false, "string", KeyDictionary.color, "black" ),
		    new Argument( false, "string", KeyDictionary.borderType )
		};
	}

	/**
	 * Add a border to the image.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name The image or name of variable that references an image to operate on
	 *
	 * @argument.thickness Border thickness
	 *
	 * @argument.color Border color: hex or constant color
	 *
	 * @argument.borderType Borer type - one of zero | constant | copy | reflect | wrap
	 *
	 * @return The BoxImage instance.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		theImage.addBorder( IntegerCaster.cast( arguments.get( KeyDictionary.thickness ) ), arguments.getAsString( KeyDictionary.color ) );

		return theImage;
	}

}
