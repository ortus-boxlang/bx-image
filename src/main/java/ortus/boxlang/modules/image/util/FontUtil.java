package ortus.boxlang.modules.image.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

/**
 * Loads TrueType and OpenType font files for drawing and measuring text.
 */
public class FontUtil {

	/** Loaded fonts keyed by absolute path, size and modified time so a replaced file is picked up */
	private static final Map<String, Font> CACHE = new ConcurrentHashMap<>();

	private FontUtil() {
	}

	/**
	 * Loads a font file (cached).
	 *
	 * @param file Path to a .ttf or .otf file
	 *
	 * @return A 1 point font with the file's glyphs; derive a sized font from it
	 *
	 * @throws BoxRuntimeException If the file is missing, unreadable or not a valid font
	 */
	public static Font load( Path file ) {
		Path absolute = file.toAbsolutePath().normalize();
		if ( !Files.isRegularFile( absolute ) ) {
			throw new BoxRuntimeException( "The font file [" + file + "] does not exist." );
		}

		try {
			String	key		= absolute + "|" + Files.size( absolute ) + "|" + Files.getLastModifiedTime( absolute ).toMillis();
			Font	cached	= CACHE.get( key );
			if ( cached != null ) {
				return cached;
			}

			Font font = Font.createFont( Font.TRUETYPE_FONT, absolute.toFile() );
			CACHE.put( key, font );
			return font;
		} catch ( FontFormatException e ) {
			throw new BoxRuntimeException( "The file [" + file + "] is not a valid TrueType or OpenType font: " + e.getMessage(), e );
		} catch ( IOException e ) {
			throw new BoxRuntimeException( "Unable to read the font file [" + file + "]: " + e.getMessage(), e );
		}
	}

	/**
	 * Loads a font file and registers it with the JVM so its family name can be used as {@code font} in a font attribute struct.
	 *
	 * @param file Path to a .ttf or .otf file
	 *
	 * @return The font's family name
	 */
	public static String register( Path file ) {
		Font font = load( file );
		GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont( font );
		return font.getFamily();
	}
}
