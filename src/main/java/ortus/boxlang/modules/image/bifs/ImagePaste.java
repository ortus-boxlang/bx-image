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
@BoxBIF( alias = "ImageDrawImage" )
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "paste" )
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "drawImage" )
public class ImagePaste extends BIF {

	/**
	 * Constructor
	 * someImage.addBorder(thickness [, color] [, bordertype])
	 */
	public ImagePaste() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( false, "any", KeyDictionary.image1, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "any", KeyDictionary.image2, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "numeric", KeyDictionary.x ),
		    new Argument( false, "numeric", KeyDictionary.y )
		};
	}

	/**
	 * Pastes one image onto another at the specified coordinates (also available as ImageDrawImage).
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.image1 The destination image (or name of variable) to paste onto.
	 *
	 * @argument.image2 The source image (or name of variable) to paste.
	 *
	 * @argument.x The x coordinate at which to paste the source image.
	 *
	 * @argument.y The y coordinate at which to paste the source image.
	 *
	 * @return The BoxImage instance with the pasted image.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage	theImage		= arguments.get( KeyDictionary.image1 ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.image1 )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.image1 ) );

		BoxImage	theImageToDraw	= arguments.get( KeyDictionary.image2 ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.image2 )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.image2 ) );

		theImage.drawImage( theImageToDraw, IntegerCaster.cast( arguments.get( KeyDictionary.x ) ), IntegerCaster.cast( arguments.get( KeyDictionary.x ) ) );
		return theImage;
	}

}
