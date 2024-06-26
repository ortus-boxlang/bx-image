package ortus.boxlang.modules.image.bifs;

import javax.imageio.ImageIO;

import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Array;

@BoxBIF
public class GetReadableImageFormats extends BIF {

	/**
	 * Returns an array of readable image formats
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @return Array of image format names
	 */
	public Array _invoke( IBoxContext context, ArgumentsScope arguments ) {
		return Array.fromArray( ImageIO.getReaderFormatNames() );
	}

}
