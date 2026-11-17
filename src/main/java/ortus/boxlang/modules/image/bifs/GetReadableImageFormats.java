package ortus.boxlang.modules.image.bifs;

import javax.imageio.ImageIO;

import ortus.boxlang.modules.image.util.SvgUtil;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Array;

@BoxBIF
public class GetReadableImageFormats extends BIF {

	/**
	 * Returns an array of readable image formats: every format ImageIO can decode, plus svg
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 *
	 * @return Array of image format names
	 */
	public Array _invoke( IBoxContext context, ArgumentsScope arguments ) {
		Array formats = new Array();
		for ( String format : ImageIO.getReaderFormatNames() ) {
			formats.add( format );
		}
		if ( formats.stream().noneMatch( format -> SvgUtil.FORMAT.equalsIgnoreCase( String.valueOf( format ) ) ) ) {
			formats.add( SvgUtil.FORMAT );
		}
		return formats;
	}

}
