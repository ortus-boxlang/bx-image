package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "measureText" )
public class ImageMeasureText extends BIF {

	/**
	 * Constructor
	 */
	public ImageMeasureText() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "string", KeyDictionary.str, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "struct", KeyDictionary.attributeCollection )
		};
	}

	/**
	 * Measures a single line of text exactly as ImageDrawText would render it, without drawing anything.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.name The image or name of variable that references an image to operate on.
	 *
	 * @argument.str The text to measure. An empty string has a width of 0.
	 *
	 * @argument.attributeCollection Optional struct of font attributes (font, size, style, underline, strikeThrough), the same as ImageDrawText.
	 *
	 * @return A struct with width, height (ascent + descent), ascent, descent and leading, all in pixels.
	 */
	public IStruct _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		return theImage.measureText( StringCaster.cast( arguments.get( KeyDictionary.str ) ), arguments.getAsStruct( KeyDictionary.attributeCollection ) );
	}

}
