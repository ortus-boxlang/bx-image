package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageKeys;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "drawText" )
public class ImageDrawText extends BIF {

	/**
	 * Constructor
	 */
	public ImageDrawText() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", ImageKeys.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "string", ImageKeys.str, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "numeric", ImageKeys.x, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( true, "numeric", ImageKeys.y, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) ),
		    new Argument( false, "struct", ImageKeys.attributeCollection )
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage	theImage	= arguments.get( ImageKeys.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( ImageKeys.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( ImageKeys.name ) );

		IStruct		attr		= arguments.getAsStruct( ImageKeys.attributeCollection );

		if ( attr == null ) {
			return theImage.drawText(
			    StringCaster.cast( arguments.get( ImageKeys.str ) ),
			    IntegerCaster.cast( arguments.get( ImageKeys.x ) ),
			    IntegerCaster.cast( arguments.get( ImageKeys.y ) )
			);
		}

		return theImage.drawText(
		    StringCaster.cast( arguments.get( ImageKeys.str ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.x ) ),
		    IntegerCaster.cast( arguments.get( ImageKeys.y ) ),
		    attr
		);
	}

}
