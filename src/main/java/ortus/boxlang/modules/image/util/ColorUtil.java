package ortus.boxlang.modules.image.util;

import java.awt.Color;
import java.util.Locale;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

/**
 * Parses the color strings accepted by every image function.
 */
public class ColorUtil {

	private ColorUtil() {
	}

	/**
	 * Converts a color name or hex code into a {@link Color}.
	 *
	 * @param color A named color known to {@link BoxImage#COLORS}, or a hex code of the form #RGB, #RRGGBB or #RRGGBBAA (the # is optional)
	 *
	 * @return The color
	 *
	 * @throws BoxRuntimeException If the value is empty or not a recognized color
	 */
	public static Color parse( String color ) {
		if ( color == null || color.isBlank() ) {
			throw new BoxRuntimeException( "A color is required but was empty." );
		}

		String	trimmed	= color.trim();
		Color	named	= BoxImage.COLORS.get( trimmed.toLowerCase( Locale.ROOT ) );
		if ( named != null ) {
			return named;
		}

		String hex = trimmed.startsWith( "#" ) ? trimmed.substring( 1 ) : trimmed;
		if ( hex.chars().allMatch( c -> Character.digit( c, 16 ) >= 0 ) ) {
			switch ( hex.length() ) {
				case 3 : {
					int	r	= Character.digit( hex.charAt( 0 ), 16 ) * 17;
					int	g	= Character.digit( hex.charAt( 1 ), 16 ) * 17;
					int	b	= Character.digit( hex.charAt( 2 ), 16 ) * 17;
					return new Color( r, g, b );
				}
				case 6 :
					return new Color( Integer.parseInt( hex, 16 ) );
				case 8 : {
					int	rgb	= Integer.parseInt( hex.substring( 0, 6 ), 16 );
					int	a	= Integer.parseInt( hex.substring( 6 ), 16 );
					return new Color( ( rgb >> 16 ) & 0xFF, ( rgb >> 8 ) & 0xFF, rgb & 0xFF, a );
				}
				default :
					break;
			}
		}

		throw new BoxRuntimeException( "Unrecognized color [" + color + "]. Use a color name or a hex code such as #RGB, #RRGGBB or #RRGGBBAA." );
	}
}
