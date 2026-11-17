package ortus.boxlang.modules.image.bifs;

import java.nio.file.Path;
import java.util.Set;

import ortus.boxlang.modules.image.util.FontUtil;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class ImageRegisterFont extends BIF {

	/**
	 * Constructor
	 */
	public ImageRegisterFont() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "string", Key.path, Set.of( Validator.REQUIRED, Validator.NON_EMPTY ) )
		};
	}

	/**
	 * Registers a TrueType or OpenType font file so its family name can be used as the font in drawText and measureText.
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @argument.path Path to a .ttf or .otf file. Relative paths are resolved from the current working directory.
	 *
	 * @return The font's family name.
	 */
	public String _invoke( IBoxContext context, ArgumentsScope arguments ) {
		return FontUtil.register( Path.of( StringCaster.cast( arguments.get( Key.path ) ) ) );
	}

}
