package ortus.boxlang.modules.image;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.RenderingHints;

public class EnumConverterUtil {

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

	public static Object getInterpolation( String interpolation ) {
		switch ( interpolation.toUpperCase() ) {
			case "BILINEAR" :
				return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			case "NEAREST" :
				return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		}

		return RenderingHints.VALUE_INTERPOLATION_BICUBIC;
	}

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
