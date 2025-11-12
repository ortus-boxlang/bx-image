/**
 * [BoxLang]
 *
 * Copyright [2023] [Ortus Solutions, Corp]
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.RenderingHints;

/**
 * Utility class for converting string-based enum values to their corresponding Java AWT constants.
 * This class provides conversion methods for various image manipulation parameters used throughout
 * the BoxLang Image module, making it easier to translate user-friendly string values into the
 * integer constants required by Java's image processing APIs.
 *
 * <p>
 * All methods are static and provide sensible defaults when invalid or unrecognized values are provided.
 * </p>
 */
public class EnumConverterUtil {

	/**
	 * Converts a string-based end cap style to its corresponding BasicStroke constant.
	 * End caps define the decoration applied to the ends of unclosed subpaths and dash segments.
	 *
	 * @param endCap The end cap style as a string. Supported values (case-insensitive):
	 *               <ul>
	 *               <li>"BUTT" - Ends unclosed subpaths and dash segments with no added decoration</li>
	 *               <li>"SQUARE" - Ends unclosed subpaths and dash segments with a square projection</li>
	 *               <li>"ROUND" - Ends unclosed subpaths and dash segments with a round decoration (default)</li>
	 *               </ul>
	 *
	 * @return The corresponding BasicStroke cap constant. Defaults to BasicStroke.CAP_ROUND if the value is unrecognized.
	 */
	public static int getEndCapInt( String endCap ) {
		switch ( endCap.toUpperCase() ) {
			case "BUTT" :
				return BasicStroke.CAP_BUTT;
			case "SQUARE" :
				return BasicStroke.CAP_SQUARE;
		}
		;

		return BasicStroke.CAP_ROUND;
	}

	/**
	 * Converts a string-based line join style to its corresponding BasicStroke constant.
	 * Line joins define the decoration applied at the intersection of two path segments
	 * and at the intersection of the endpoints of a subpath.
	 *
	 * @param endCap The line join style as a string. Supported values (case-insensitive):
	 *               <ul>
	 *               <li>"MITER" - Joins path segments by extending their outside edges until they meet</li>
	 *               <li>"JOIN" or "ROUND" - Joins path segments by rounding off the corner</li>
	 *               <li>Any other value - Defaults to BEVEL, which joins path segments by connecting
	 *               the outer corners with a straight segment</li>
	 *               </ul>
	 *
	 * @return The corresponding BasicStroke join constant. Defaults to BasicStroke.JOIN_BEVEL if the value is unrecognized.
	 */
	public static int getLineJoinsInt( String endCap ) {
		switch ( endCap.toUpperCase() ) {
			case "MITER" :
				return BasicStroke.JOIN_MITER;
			case "JOIN" :
			case "ROUND" :
				return BasicStroke.JOIN_ROUND;
		}
		;

		return BasicStroke.JOIN_BEVEL;
	}

	/**
	 * Converts a string-based interpolation method to its corresponding RenderingHints constant.
	 * Interpolation defines the algorithm used when scaling or transforming images.
	 *
	 * @param interpolation The interpolation method as a string. Supported values (case-insensitive):
	 *                      <ul>
	 *                      <li>"BILINEAR" - Bilinear interpolation, providing a balance between speed and quality</li>
	 *                      <li>"NEAREST" - Nearest-neighbor interpolation, fastest but lowest quality</li>
	 *                      <li>Any other value - Defaults to BICUBIC, the highest quality but slowest method</li>
	 *                      </ul>
	 *
	 * @return The corresponding RenderingHints interpolation value. Defaults to VALUE_INTERPOLATION_BICUBIC if the value is unrecognized.
	 */
	public static Object getInterpolation( String interpolation ) {
		switch ( interpolation.toUpperCase() ) {
			case "BILINEAR" :
				return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			case "NEAREST" :
				return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		}

		return RenderingHints.VALUE_INTERPOLATION_BICUBIC;
	}

	/**
	 * Converts a string-based overlay (composite) rule to its corresponding AlphaComposite constant.
	 * Overlay rules define how source and destination pixels are combined during image compositing operations.
	 *
	 * @param overlayRule The overlay rule as a string. Supported values (case-insensitive):
	 *                    <ul>
	 *                    <li>"SRC" - Source pixels replace destination pixels (default)</li>
	 *                    <li>"DST_IN" - Destination pixels are kept where source is opaque</li>
	 *                    <li>"DST_OUT" - Destination pixels are kept where source is transparent</li>
	 *                    <li>"DST_OVER" - Destination pixels are composited over source pixels</li>
	 *                    <li>"SRC_IN" - Source pixels are kept where destination is opaque</li>
	 *                    <li>"SRC_OVER" - Source pixels are composited over destination pixels</li>
	 *                    <li>"SRC_OUT" - Source pixels are kept where destination is transparent</li>
	 *                    </ul>
	 *
	 * @return The corresponding AlphaComposite constant. Defaults to AlphaComposite.SRC if the value is unrecognized.
	 */
	public static int getOveralyRule( String overlayRule ) {
		switch ( overlayRule.toUpperCase() ) {
			case "SRC" :
				return AlphaComposite.SRC;
			case "DST_IN" :
				return AlphaComposite.DST_IN;
			case "DST_OUT" :
				return AlphaComposite.DST_OUT;
			case "DST_OVER" :
				return AlphaComposite.DST_OVER;
			case "SRC_IN" :
				return AlphaComposite.SRC_IN;
			case "SRC_OVER" :
				return AlphaComposite.SRC_OVER;
			case "SRC_OUT" :
				return AlphaComposite.SRC_OUT;
		}

		return AlphaComposite.SRC;
	}

	/**
	 * Converts a Java AWT transparency type constant to a human-readable string description.
	 * This is useful for reporting image information back to BoxLang code.
	 *
	 * @param transparencyType The transparency type constant from java.awt.Transparency:
	 *                         <ul>
	 *                         <li>Transparency.BITMASK - Returns "BITMASK" (image data contains or might contain
	 *                         arbitrary alpha values between and including 0.0 and 1.0)</li>
	 *                         <li>Transparency.OPAQUE - Returns "OPAQUE" (image data is completely opaque)</li>
	 *                         <li>Transparency.TRANSLUCENT - Returns "TRANSLUCENT" (image data contains or might
	 *                         contain arbitrary alpha values)</li>
	 *                         </ul>
	 *
	 * @return A string description of the transparency type. Returns "UNKNOWN" if the type is not recognized.
	 */
	public static String getTransparencyDescription( int transparencyType ) {
		switch ( transparencyType ) {
			case java.awt.Transparency.BITMASK :
				return "BITMASK";
			case java.awt.Transparency.OPAQUE :
				return "OPAQUE";
			case java.awt.Transparency.TRANSLUCENT :
				return "TRANSLUCENT";
		}

		return "UNKNOWN";
	}

	/**
	 * Converts a Java AWT color space type constant to a human-readable string description.
	 * This is useful for reporting detailed image color space information back to BoxLang code.
	 * The method covers both standard color spaces (CS_*) and generic color space types (TYPE_*).
	 *
	 * @param colorSpaceType The color space type constant from java.awt.color.ColorSpace.
	 *                       Supports all standard ColorSpace constants including:
	 *                       <ul>
	 *                       <li>CS_* constants (CIEXYZ, GRAY, LINEAR_RGB, PYCC, sRGB)</li>
	 *                       <li>TYPE_* constants for generic N-component color spaces (TYPE_2CLR through TYPE_FCLR)</li>
	 *                       <li>TYPE_* constants for standard color models (RGB, GRAY, CMYK, CMY, HSV, HLS, Lab, Luv, XYZ, YCbCr, Yxy)</li>
	 *                       </ul>
	 *
	 * @return A detailed string description of the color space. Returns "Unknown color space" if the type is not recognized.
	 */
	public static String getColorSpaceDescription( int colorSpaceType ) {
		switch ( colorSpaceType ) {
			case java.awt.color.ColorSpace.CS_CIEXYZ :
				return "The CIEXYZ conversion color space defined above.";
			case java.awt.color.ColorSpace.CS_GRAY :
				return "The built-in linear gray scale color space.";
			case java.awt.color.ColorSpace.CS_LINEAR_RGB :
				return "A built-in linear RGB color space.";
			case java.awt.color.ColorSpace.CS_PYCC :
				return "The Photo YCC conversion color space.";
			case java.awt.color.ColorSpace.CS_sRGB :
				return "The sRGB color space defined at http://www.w3.org/pub/WWW/Graphics/Color/sRGB.html .";
			case java.awt.color.ColorSpace.TYPE_2CLR :
				return "Generic 2 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_3CLR :
				return "Generic 3 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_4CLR :
				return "Generic 4 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_5CLR :
				return "Generic 5 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_6CLR :
				return "Generic 6 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_7CLR :
				return "Generic 7 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_8CLR :
				return "Generic 8 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_9CLR :
				return "Generic 9 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_ACLR :
				return "Generic 10 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_BCLR :
				return "Generic 11 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_CCLR :
				return "Generic 12 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_CMY :
				return "Any of the family of CMY color spaces.";
			case java.awt.color.ColorSpace.TYPE_CMYK :
				return "Any of the family of CMYK color spaces.";
			case java.awt.color.ColorSpace.TYPE_DCLR :
				return "Generic 13 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_ECLR :
				return "Generic 14 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_FCLR :
				return "Generic 15 component color spaces.";
			case java.awt.color.ColorSpace.TYPE_GRAY :
				return "Any of the family of GRAY color spaces.";
			case java.awt.color.ColorSpace.TYPE_HLS :
				return "Any of the family of HLS color spaces.";
			case java.awt.color.ColorSpace.TYPE_HSV :
				return "Any of the family of HSV color spaces.";
			case java.awt.color.ColorSpace.TYPE_Lab :
				return "Any of the family of Lab color spaces.";
			case java.awt.color.ColorSpace.TYPE_Luv :
				return "Any of the family of Luv color spaces.";
			case java.awt.color.ColorSpace.TYPE_RGB :
				return "Any of the family of RGB color spaces.";
			case java.awt.color.ColorSpace.TYPE_XYZ :
				return "Any of the family of XYZ color spaces.";
			case java.awt.color.ColorSpace.TYPE_YCbCr :
				return "Any of the family of YCbCr color spaces.";
			case java.awt.color.ColorSpace.TYPE_Yxy :
				return "Any of the family of Yxy color spaces.";
		}
		;

		return "Unknown color space";
	}

}
