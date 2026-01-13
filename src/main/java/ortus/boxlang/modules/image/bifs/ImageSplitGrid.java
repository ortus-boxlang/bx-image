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
import ortus.boxlang.runtime.types.Array;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "splitGrid" )
public class ImageSplitGrid extends BIF {

	/**
	 * Constructor
	 */
	public ImageSplitGrid() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.columns, Set.of( Validator.REQUIRED ) ),
		    new Argument( true, "numeric", KeyDictionary.rows, Set.of( Validator.REQUIRED ) )
		};
	}

	/**
	 * Splits an image into a grid of tiles based on the specified number of columns and rows.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 * 
	 * @argument.name The image or name of variable that references an image to split.
	 * 
	 * @argument.columns The number of horizontal tiles to split the image into.
	 * 
	 * @argument.rows The number of vertical tiles to split the image into.
	 * 
	 * @return An array of arrays containing BoxImage tiles. The outer array represents rows,
	 *         and each inner array contains the tiles for that row.
	 */
	public Array _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage	theImage	= arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		int			columns		= IntegerCaster.cast( arguments.get( KeyDictionary.columns ) );
		int			rows		= IntegerCaster.cast( arguments.get( KeyDictionary.rows ) );

		return theImage.splitGrid( columns, rows );
	}

}
