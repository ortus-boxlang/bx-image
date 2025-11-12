package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "overlay" )
public class ImageOverlay extends BIF {

	/**
	 * Constructor
	 * someImage.addBorder(thickness [, color] [, bordertype])
	 */
	public ImageOverlay() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( false, "any", KeyDictionary.image1, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "any", KeyDictionary.image2, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "string", KeyDictionary.rule, "SRC_OVER" ),
		    new Argument( false, "numeric", KeyDictionary.transparency, .25 )
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage	theImage		= arguments.get( KeyDictionary.image1 ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.image1 )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.image1 ) );

		BoxImage	theImageToDraw	= arguments.get( KeyDictionary.image2 ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.image2 )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.image2 ) );

		theImage.overlay( theImageToDraw, arguments.getAsString( KeyDictionary.rule ), arguments.getAsDouble( KeyDictionary.transparency ) );
		return theImage;
	}

}
