/**
 * [BoxLang]
 *
 * Copyright [2024] [Ortus Solutions, Corp]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ortus.boxlang.modules.image.util;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.LoaderContext;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.parser.resources.ResourcePolicy;
import com.github.weisj.jsvg.view.FloatSize;
import com.github.weisj.jsvg.view.ViewBox;

import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

/**
 * Reads SVG documents and rasterises them to a {@link BufferedImage} with an alpha channel, using JSVG.
 * <p>
 * The XML parser has DTDs and external entities disabled, and external resources (linked images, fonts)
 * are never fetched: only embedded {@code data:} resources are rendered.
 */
public final class SvgUtil {

	/** Format name reported by {@code GetReadableImageFormats()} */
	public static final String			FORMAT			= "svg";

	/** Size in pixels used for both sides when an SVG declares neither a viewBox nor a width and height */
	public static final int				DEFAULT_SIZE	= 512;

	/** How many leading bytes are inspected when sniffing for an SVG root element */
	private static final int			SNIFF_BYTES		= 4096;

	/** Shared, thread-safe loader context that never loads external resources */
	private static final LoaderContext	LOADER_CONTEXT	= LoaderContext.builder()
	    .externalResourcePolicy( ResourcePolicy.DENY_EXTERNAL )
	    .build();

	private SvgUtil() {
	}

	/**
	 * Whether the source should be read as SVG: either its path ends in {@code .svg} or its content starts
	 * with an {@code <svg} root element, after an optional XML prolog, comments or doctype.
	 *
	 * @param path The source path or URL, may be null
	 * @param data The source bytes, may be null
	 *
	 * @return True if the source is SVG
	 */
	public static boolean isSvg( String path, byte[] data ) {
		return hasSvgExtension( path ) || hasSvgContent( data );
	}

	/**
	 * Whether a path or URL ends in {@code .svg}, ignoring case, a query string and a fragment.
	 *
	 * @param path The source path or URL, may be null
	 *
	 * @return True if the extension is svg
	 */
	public static boolean hasSvgExtension( String path ) {
		if ( path == null ) {
			return false;
		}
		String	clean	= path;
		int		cut		= clean.indexOf( '?' );
		if ( cut != -1 ) {
			clean = clean.substring( 0, cut );
		}
		cut = clean.indexOf( '#' );
		if ( cut != -1 ) {
			clean = clean.substring( 0, cut );
		}
		return clean.toLowerCase( Locale.ROOT ).endsWith( "." + FORMAT );
	}

	/**
	 * Whether the data starts with an {@code <svg} root element, after an optional XML prolog, comments or doctype.
	 *
	 * @param data The source bytes, may be null
	 *
	 * @return True if the content is SVG
	 */
	public static boolean hasSvgContent( byte[] data ) {
		return findRootTag( data ) != null;
	}

	/**
	 * Renders SVG data to an ARGB image.
	 * <p>
	 * With no target size the image is rendered at the SVG's intrinsic size, taken from its width and height or its viewBox,
	 * or {@link #DEFAULT_SIZE} square when it declares neither. With only one side given, the other keeps the intrinsic
	 * aspect ratio. With both given, the SVG is fitted into that box following its own {@code preserveAspectRatio}.
	 *
	 * @param data   The SVG bytes
	 * @param width  Target width in pixels, or null
	 * @param height Target height in pixels, or null
	 * @param source A description of the source, for error messages
	 *
	 * @return The rendered image
	 *
	 * @throws BoxRuntimeException If the SVG cannot be parsed, the size is invalid, or it exceeds {@code maxPixels}
	 */
	public static BufferedImage render( byte[] data, Integer width, Integer height, String source ) {
		if ( ( width != null && width <= 0 ) || ( height != null && height <= 0 ) ) {
			throw new BoxRuntimeException( "The SVG width and height must be greater than zero, got [" + width + "] x [" + height + "]." );
		}

		String		rootTag		= findRootTag( data );
		SVGDocument	document	= data == null || data.length == 0 ? null
		    : new SVGLoader().load( new ByteArrayInputStream( data ), null, LOADER_CONTEXT );
		if ( rootTag == null ) {
			// The extension said SVG but the root could not be sniffed: rely on the parser alone
			rootTag = "";
		}
		if ( document == null ) {
			throw new BoxRuntimeException( "Unable to read SVG image [" + source + "]: the content is not a well-formed SVG document." );
		}

		double[]	intrinsic	= intrinsicSize( document, rootTag );
		int			targetWidth;
		int			targetHeight;
		if ( width != null && height != null ) {
			targetWidth		= width;
			targetHeight	= height;
		} else if ( width != null ) {
			targetWidth		= width;
			targetHeight	= ( int ) Math.max( 1, Math.round( width * intrinsic[ 1 ] / intrinsic[ 0 ] ) );
		} else if ( height != null ) {
			targetHeight	= height;
			targetWidth		= ( int ) Math.max( 1, Math.round( height * intrinsic[ 0 ] / intrinsic[ 1 ] ) );
		} else {
			targetWidth		= ( int ) Math.max( 1, Math.ceil( intrinsic[ 0 ] ) );
			targetHeight	= ( int ) Math.max( 1, Math.ceil( intrinsic[ 1 ] ) );
		}

		long[] dimensions = new long[] { targetWidth, targetHeight };
		if ( !ImageGuard.isWithinLimit( dimensions, ImageGuard.getMaxPixels() ) ) {
			throw new BoxRuntimeException( "SVG image [" + source + "] would render at " + targetWidth + " x " + targetHeight + " ("
			    + ( ( long ) targetWidth * targetHeight ) + " pixels), which exceeds the maximum allowed " + ImageGuard.getMaxPixels()
			    + " pixels (maxPixels)." );
		}

		BufferedImage	image		= new BufferedImage( targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB );
		Graphics2D		graphics	= image.createGraphics();
		try {
			graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			graphics.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
			graphics.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
			graphics.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			document.render( ( Component ) null, graphics, new ViewBox( 0, 0, targetWidth, targetHeight ) );
		} catch ( RuntimeException e ) {
			throw new BoxRuntimeException( "Unable to render SVG image [" + source + "]: " + e.getMessage(), e );
		} finally {
			graphics.dispose();
		}
		return image;
	}

	/**
	 * The intrinsic size of the document: its own size when the root declares a viewBox or both a width and a height,
	 * otherwise {@link #DEFAULT_SIZE} square.
	 */
	private static double[] intrinsicSize( SVGDocument document, String rootTag ) {
		boolean	hasViewBox	= attributeValue( rootTag, "viewBox" ) != null;
		boolean	hasSize		= isAbsoluteLength( attributeValue( rootTag, "width" ) ) && isAbsoluteLength( attributeValue( rootTag, "height" ) );
		if ( hasViewBox || hasSize ) {
			FloatSize size = document.size();
			if ( size.width > 0 && size.height > 0 ) {
				return new double[] { size.width, size.height };
			}
		}
		return new double[] { DEFAULT_SIZE, DEFAULT_SIZE };
	}

	/**
	 * Finds the opening svg root tag in the first bytes of the data. Leading whitespace, processing instructions
	 * (including the XML prolog), comments and a doctype are skipped. This is a single linear scan rather than a
	 * regular expression, so crafted input cannot cause backtracking.
	 *
	 * @return The opening tag text, or null if the data does not start with an svg root element
	 */
	static String findRootTag( byte[] data ) {
		if ( data == null || data.length == 0 ) {
			return null;
		}
		String head = new String( data, 0, Math.min( data.length, SNIFF_BYTES ), StandardCharsets.UTF_8 );
		if ( head.startsWith( "\uFEFF" ) ) {
			head = head.substring( 1 );
		}

		int	length	= head.length();
		int	index	= 0;
		while ( true ) {
			while ( index < length && Character.isWhitespace( head.charAt( index ) ) ) {
				index++;
			}
			int end;
			if ( head.startsWith( "<?", index ) ) {
				end = head.indexOf( "?>", index + 2 );
				if ( end == -1 ) {
					return null;
				}
				index = end + 2;
			} else if ( head.startsWith( "<!--", index ) ) {
				end = head.indexOf( "-->", index + 4 );
				if ( end == -1 ) {
					return null;
				}
				index = end + 3;
			} else if ( head.regionMatches( true, index, "<!DOCTYPE", 0, 9 ) ) {
				end = head.indexOf( '>', index + 9 );
				if ( end == -1 ) {
					return null;
				}
				index = end + 1;
			} else {
				break;
			}
		}

		if ( !head.regionMatches( true, index, "<svg", 0, 4 ) ) {
			return null;
		}
		int after = index + 4;
		if ( after < length ) {
			char next = head.charAt( after );
			if ( !Character.isWhitespace( next ) && next != '>' && next != '/' ) {
				return null;
			}
		}
		int close = head.indexOf( '>', after );
		return head.substring( index, close == -1 ? length : close + 1 );
	}

	/**
	 * Reads an attribute's value from an opening tag, matching the name case-insensitively.
	 *
	 * @param tag  The opening tag text
	 * @param name The attribute name
	 *
	 * @return The trimmed value, or null if the tag has no such quoted attribute
	 */
	static String attributeValue( String tag, String name ) {
		String	lowerTag	= tag.toLowerCase( Locale.ROOT );
		String	lowerName	= name.toLowerCase( Locale.ROOT );
		int		from		= 0;
		while ( true ) {
			int at = lowerTag.indexOf( lowerName, from );
			if ( at == -1 ) {
				return null;
			}
			from = at + lowerName.length();
			// The name must start after whitespace, so stroke-width does not count as width
			if ( at == 0 || !Character.isWhitespace( lowerTag.charAt( at - 1 ) ) ) {
				continue;
			}
			int index = from;
			while ( index < tag.length() && Character.isWhitespace( tag.charAt( index ) ) ) {
				index++;
			}
			if ( index >= tag.length() || tag.charAt( index ) != '=' ) {
				continue;
			}
			index++;
			while ( index < tag.length() && Character.isWhitespace( tag.charAt( index ) ) ) {
				index++;
			}
			if ( index >= tag.length() ) {
				return null;
			}
			char quote = tag.charAt( index );
			if ( quote != '"' && quote != '\'' ) {
				continue;
			}
			int end = tag.indexOf( quote, index + 1 );
			if ( end == -1 ) {
				return null;
			}
			return tag.substring( index + 1, end ).trim();
		}
	}

	/**
	 * Whether an SVG length is present and absolute, i.e. not empty and not a percentage.
	 */
	private static boolean isAbsoluteLength( String value ) {
		return value != null && !value.isEmpty() && value.indexOf( '%' ) == -1;
	}

}
