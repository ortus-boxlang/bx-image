package ortus.boxlang.modules.image;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifImageDirectory;
import com.drew.metadata.exif.ExifInteropDirectory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.iptc.IptcDirectory;

import javaxt.io.Image;
import ortus.boxlang.runtime.dynamic.casters.ArrayCaster;
import ortus.boxlang.runtime.dynamic.casters.BooleanCaster;
import ortus.boxlang.runtime.dynamic.casters.CastAttempt;
import ortus.boxlang.runtime.dynamic.casters.FloatCaster;
import ortus.boxlang.runtime.dynamic.casters.IntegerCaster;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.types.Array;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.Struct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class BoxImage {

	public static final Map<String, Color>	COLORS;
	public static final String				DEFAULT_FONT_FAMILY	= Font.SANS_SERIF;
	public static final int					DEFAULT_FONT_STYLE	= Font.PLAIN;
	public static final int					DEFAULT_FONT_SIZE	= 10;

	static {
		COLORS = new HashMap<String, Color>();
		COLORS.put( "black", Color.black );
		COLORS.put( "blue", Color.blue );
		COLORS.put( "cyan", Color.cyan );
		COLORS.put( "darkgray", Color.darkGray );
		COLORS.put( "gray", Color.gray );
		COLORS.put( "green", Color.green );
		COLORS.put( "lightgray", Color.lightGray );
		COLORS.put( "magenta", Color.magenta );
		COLORS.put( "orange", Color.orange );
		COLORS.put( "pink", Color.pink );
		COLORS.put( "red", Color.red );
		COLORS.put( "white", Color.white );
		COLORS.put( "yellow", Color.yellow );
	}

	private String		sourcePath;
	private Graphics2D	graphics;
	private Image		image;
	private String		drawingColor	= "white";
	private String		backgroundColor	= "white";
	private IStruct		exifData;
	private IStruct		iptcData;

	public enum Dimension {
		WIDTH,
		HEIGHT
	}

	public static BoxImage fromBase64( String base64String ) throws IOException {
		return new BoxImage( ImageIO.read( new ByteArrayInputStream( Base64.getDecoder().decode( base64String ) ) ) );
	}

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

	public static IStruct readExifMetaData( String path ) throws ImageProcessingException, FileNotFoundException, IOException {
		IStruct		exifData	= new Struct();
		Metadata	metaData	= ImageMetadataReader.readMetadata( new FileInputStream( path ) );

		Stream.of(
		    metaData.getFirstDirectoryOfType( ExifImageDirectory.class ),
		    metaData.getFirstDirectoryOfType( ExifInteropDirectory.class ),
		    metaData.getFirstDirectoryOfType( ExifIFD0Directory.class ),
		    metaData.getFirstDirectoryOfType( ExifThumbnailDirectory.class ),
		    metaData.getFirstDirectoryOfType( ExifSubIFDDirectory.class ),
		    metaData.getFirstDirectoryOfType( GpsDirectory.class ),
		    metaData.getFirstDirectoryOfType( IptcDirectory.class )
		)
		    .filter( dir -> dir != null )
		    .forEach( dir -> {
			    dir.getTags().stream().forEach( tag -> exifData.put( tag.getTagName(), tag.getDescription() ) );
		    } );

		if ( exifData.get( "Date/Time" ) == null ) {
			exifData.put( "Date/Time", exifData.get( "Date/Time Original" ) );
		}

		return exifData;
	}

	public static IStruct readIPTCMetaData( String path ) throws ImageProcessingException, FileNotFoundException, IOException {
		IStruct		iptcData	= new Struct();
		Metadata	metaData	= ImageMetadataReader.readMetadata( new FileInputStream( path ) );

		Stream.of(
		    metaData.getFirstDirectoryOfType( IptcDirectory.class )
		)
		    .filter( dir -> dir != null )
		    .forEach( dir -> {
			    dir.getTags().stream().forEach( tag -> iptcData.put( tag.getTagName(), tag.getDescription() ) );
		    } );

		if ( iptcData.get( "Date/Time" ) == null ) {
			iptcData.put( "Date/Time", iptcData.get( "Date/Time Original" ) );
		}

		return iptcData;
	}

	public static Object getExifMetaDataTag( String path, String tagName ) throws ImageProcessingException, FileNotFoundException, IOException {
		return readExifMetaData( path ).get( tagName );
	}

	public static Object getIPTCMetaDataTag( String path, String tagName ) throws ImageProcessingException, FileNotFoundException, IOException {
		return readIPTCMetaData( path ).get( tagName );
	}

	// TODO handle imageType
	public BoxImage( int width, int height, ImageType imageType, String color ) {
		this.image		= new Image( width, height );
		this.graphics	= this.image.getBufferedImage().createGraphics();

		this.setDrawingColor( color );
		this.fillRect( 0, 0, width, height );
		this.setDrawingColor( "black" );
	}

	public BoxImage( String imageURI ) {
		this.sourcePath = imageURI;
		try {
			if ( imageURI.toString().toLowerCase().startsWith( "http" ) ) {

				this.image = new Image( new URL( imageURI ).openStream() );

			} else {
				this.image = new Image( imageURI.toString() );
			}
		} catch ( MalformedURLException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.cacheGraphics();
	}

	public BoxImage( URI imageURI ) {
		this.sourcePath = imageURI.toString();
		try {
			if ( imageURI.toString().toLowerCase().startsWith( "http" ) ) {

				this.image = new Image( imageURI.toURL().openStream() );

			} else {
				this.image = new Image( new FileInputStream( imageURI.toString() ) );
			}
		} catch ( MalformedURLException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.cacheGraphics();
	}

	public BoxImage( BufferedImage imageData ) {
		this.image = new Image( imageData );
		this.cacheGraphics();
	}

	private void cacheGraphics() {
		if ( this.graphics != null ) {
			this.graphics.dispose();
		}

		this.graphics = this.image.getBufferedImage().createGraphics();

		this.setDrawingColor( this.drawingColor );
		this.setBackgroundColor( this.backgroundColor );
	}

	public String getSourcePath() {
		return this.sourcePath;
	}

	public IStruct getImageInfo() {
		IStruct info = new Struct();

		info.put( "height", this.image.getHeight() );
		info.put( "width", this.image.getWidth() );

		IStruct		colorModel	= new Struct();
		ColorModel	cm			= this.image.getBufferedImage().getColorModel();

		int			index		= 1;
		for ( int size : cm.getComponentSize() ) {
			colorModel.put( "bits_component_" + index, size );
			index += 1;
		}

		colorModel.put( "alpha_channel_support", cm.hasAlpha() );
		colorModel.put( "alpha_premultiplied", cm.isAlphaPremultiplied() );
		colorModel.put( "colormodel_type", cm.getClass().getSimpleName() );
		// pulled from https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/awt/color/ColorSpace.html
		colorModel.put( "colorspace", getColorSpaceDescription( cm.getColorSpace().getType() ) );
		colorModel.put( "num_color_components", cm.getNumColorComponents() );
		colorModel.put( "num_components", cm.getNumComponents() );
		colorModel.put( "pixel_size", cm.getPixelSize() );
		colorModel.put( "transparency", getTransparencyDescription( cm.getTransparency() ) );
		info.put( "colormodel", colorModel );
		info.put( "source", this.getSourcePath() );

		return info;
	}

	public BoxImage setAntiAliasing( boolean useAntiAliasing ) {
		this.graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
		    useAntiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );

		return this;
	}

	public BoxImage scaleToFit( int size, Dimension dimension, String interpolation ) {
		double scaleFactor = dimension == Dimension.WIDTH ? ( double ) size / ( double ) this.getWidth() : ( double ) size / ( double ) this.getHeight();

		this.resize(
		    Double.valueOf( this.getWidth() * scaleFactor ).intValue(),
		    Double.valueOf( this.getHeight() * scaleFactor ).intValue(),
		    interpolation,
		    0
		);

		return this;
	}

	public BoxImage rotateDrawingAxis( double angle, int x, int y ) {
		this.graphics.setTransform( AffineTransform.getRotateInstance( Math.toRadians( angle ), x, y ) );

		return this;
	}

	public BoxImage rotate( int angle ) {
		int				oldWidth		= this.image.getWidth();
		int				oldHeight		= this.image.getHeight();
		Rectangle		rect			= new Rectangle( 0, 0, this.image.getWidth(), this.image.getHeight() );
		AffineTransform	rotate			= AffineTransform.getRotateInstance( Math.toRadians( angle ), 0, 0 );
		Shape			rotated			= rotate.createTransformedShape( rect );
		Rectangle		bounds			= rotated.getBounds();
		int				newWidth		= Double.valueOf( bounds.getWidth() ).intValue();
		int				newHeight		= Double.valueOf( bounds.getHeight() ).intValue();

		BufferedImage	resizedImage	= new BufferedImage( newWidth, newHeight,
		    this.image.getBufferedImage().getType() );
		Graphics2D		resizedGraphics	= resizedImage.createGraphics();

		resizedGraphics.fillRect( 0, 0, newWidth, newHeight );
		resizedGraphics.setTransform( rotate );
		resizedGraphics.setTransform( AffineTransform.getRotateInstance( Math.toRadians( angle ), newWidth / 2, newHeight / 2 ) );
		resizedGraphics.drawImage( this.image.getBufferedImage(), ( newWidth - oldWidth ) / 2, ( newHeight - oldHeight ) / 2, null );
		resizedGraphics.dispose();

		this.image = new Image( resizedImage );
		this.cacheGraphics();

		return this;
	}

	public BoxImage overlay( BoxImage toOverlay, String overlayRule, double transparency ) {
		AlphaComposite	overlayComposite	= AlphaComposite.getInstance( getOveralyRule( overlayRule ), ( float ) transparency );
		Composite		original			= this.graphics.getComposite();

		this.graphics.setComposite( overlayComposite );
		this.graphics.drawImage( toOverlay.getBufferedImage(), 0, 0, null );

		this.graphics.setComposite( original );

		return this;
	}

	public BoxImage grayScale() {
		BufferedImage	grayImage	= new BufferedImage( this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_BYTE_GRAY );
		Graphics2D		g			= grayImage.createGraphics();
		g.drawImage( this.image.getBufferedImage(), 0, 0, null );

		this.image		= new Image( grayImage );
		this.graphics	= g;
		this.setDrawingColor( this.drawingColor );
		this.setBackgroundColor( this.backgroundColor );

		return this;
	}

	public IStruct getExifMetaData() throws ImageProcessingException, FileNotFoundException, IOException {
		if ( exifData == null ) {
			exifData = readExifMetaData( getSourcePath() );
		}

		return exifData;
	}

	public IStruct getIPTCMetaData() throws ImageProcessingException, FileNotFoundException, IOException {
		if ( iptcData == null ) {
			iptcData = readExifMetaData( getSourcePath() );
		}

		return iptcData;
	}

	public Object getExifMetaDataTag( String tagName ) throws ImageProcessingException, FileNotFoundException, IOException {
		return getExifMetaData().get( tagName );
	}

	public Object getIPTCMetaDataTag( String tagName ) throws ImageProcessingException, FileNotFoundException, IOException {
		return getIPTCMetaData().get( tagName );
	}

	public BoxImage copy() {
		BoxImage newImage = new BoxImage( getWidth(), getHeight(), ImageType.ARGB, "black" );

		newImage.drawImage( this, 0, 0 );

		return newImage;
	}

	public BoxImage drawArc( int x, int y, int width, int height, int startAngle, int archAngle, boolean filled ) {

		if ( filled ) {
			this.graphics.fillArc( x, y, width, height, startAngle, archAngle );
		} else {
			this.graphics.drawArc( x, y, width, height, startAngle, archAngle );
		}

		return this;
	}

	public BoxImage drawCubicCurve( int x1, int y1, int ctrlx1, int ctrly1, int ctrlx2, int ctrly2, int x2, int y2 ) {
		this.graphics.draw( new CubicCurve2D.Double( x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2 ) );

		return this;
	}

	public BoxImage drawLine( int x1, int y1, int x2, int y2 ) {
		this.graphics.drawLine( x1, y1, x2, y2 );

		return this;
	}

	public BoxImage drawLines( Array xCoords, Array yCoords, boolean isPolygon, boolean filled ) {
		int[]	xPoints	= xCoords.stream().mapToInt( IntegerCaster::cast ).toArray();
		int[]	yPoints	= yCoords.stream().mapToInt( IntegerCaster::cast ).toArray();

		if ( !isPolygon ) {
			this.graphics.drawPolyline( xPoints, yPoints, xCoords.size() );
			return this;
		}

		if ( filled ) {
			this.graphics.fillPolygon( xPoints, yPoints, xCoords.size() );
		} else {
			this.graphics.drawPolygon( xPoints, yPoints, xCoords.size() );
		}

		return this;
	}

	public BoxImage drawQuadraticCurve( int ctrlx1, int ctrly1, int x1, int y1, int x2, int y2 ) {
		this.graphics.draw( new QuadCurve2D.Double( ctrlx1, ctrly1, x1, y1, x2, y2 ) );

		return this;
	}

	public BoxImage drawText( String str, int x, int y ) {
		this.graphics.drawString( str, x, y );
		return this;
	}

	public BoxImage setDrawingStroke( IStruct strokeConfig ) {
		StrokeBuilder builder = new StrokeBuilder();

		if ( strokeConfig.containsKey( ImageKeys.width ) ) {
			builder.width( FloatCaster.cast( strokeConfig.get( ImageKeys.width ) ) );
		}

		if ( strokeConfig.containsKey( ImageKeys.endCaps ) ) {
			builder.endCaps( BoxImage.getEndCapInt( StringCaster.cast( strokeConfig.get( ImageKeys.endCaps ) ) ) );
		}

		if ( strokeConfig.containsKey( ImageKeys.lineJoins ) ) {
			builder.lineJoins( BoxImage.getLineJoinsInt( StringCaster.cast( strokeConfig.get( ImageKeys.lineJoins ) ) ) );
		}

		if ( strokeConfig.containsKey( ImageKeys.miterLimit ) ) {
			builder.miterLimit( FloatCaster.cast( strokeConfig.get( ImageKeys.miterLimit ) ) );
		}

		if ( strokeConfig.containsKey( ImageKeys.dashArray ) ) {
			Float[]	floats			= ArrayCaster.cast( strokeConfig.get( ImageKeys.dashArray ) ).toArray( new Float[] {} );
			float[]	actualFloats	= new float[ floats.length ];

			// yuck
			for ( int i = 0; i < floats.length; i++ ) {
				actualFloats[ i ] = floats[ i ].floatValue();
			}

			builder.dashArray( actualFloats );
		}

		if ( strokeConfig.containsKey( ImageKeys.dashPhase ) ) {
			builder.dashPhase( FloatCaster.cast( strokeConfig.get( ImageKeys.dashPhase ) ) );
		}

		this.graphics.setStroke( builder.build() );

		return this;
	}

	public BoxImage setDrawingTransparency( double transparency ) {
		AlphaComposite composite = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, ( float ) ( transparency / 100.0 ) );
		this.graphics.setComposite( composite );
		return this;
	}

	public BoxImage drawText( String str, int x, int y, IStruct fontConfig ) {
		Map<TextAttribute, Object> attr = new HashMap<TextAttribute, Object>();

		attr.put( TextAttribute.FONT, DEFAULT_FONT_FAMILY );
		attr.put( TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR );
		attr.put( TextAttribute.STRIKETHROUGH, false );
		attr.put( TextAttribute.UNDERLINE, false );

		String family = fontConfig.getAsString( ImageKeys.font );
		if ( family != null ) {
			attr.put( TextAttribute.FAMILY, family );
		}

		String style = fontConfig.getAsString( ImageKeys.style );
		if ( style != null && !style.equalsIgnoreCase( "plain" ) ) {
			if ( style.equalsIgnoreCase( "bold" ) ) {
				attr.put( TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD );
			} else if ( style.equalsIgnoreCase( "italic" ) ) {
				attr.put( TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE );
			} else if ( style.equalsIgnoreCase( "bolditalic" ) ) {
				attr.put( TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD );
				attr.put( TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE );
			} else {
				throw new BoxRuntimeException( "You must supply one of bold, italic, bolditalic, or plain as a font style" );
			}
		}

		CastAttempt<Integer> size = IntegerCaster.attempt( fontConfig.get( ImageKeys.size ) );
		if ( size.wasSuccessful() ) {
			attr.put( TextAttribute.SIZE, size.get() );
		}

		CastAttempt<Boolean> strikeThrough = BooleanCaster.attempt( fontConfig.get( ImageKeys.strikeThrough ) );
		if ( strikeThrough.wasSuccessful() ) {
			attr.put( TextAttribute.STRIKETHROUGH, strikeThrough.get() );
		}

		CastAttempt<Boolean> underline = BooleanCaster.attempt( fontConfig.get( ImageKeys.underline ) );
		if ( underline.wasSuccessful() && underline.get() ) {
			attr.put( TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON );
		}

		Font currentFont = new Font( attr );

		this.graphics.setFont( currentFont );

		this.drawText( str, x, y );

		this.graphics.setFont( null );

		return this;
	}

	public BoxImage resize( int width, int height, String interpolcation, int blurFactor ) {
		BufferedImage	resizedImage	= new BufferedImage( width, height, this.image.getBufferedImage().getType() );
		Graphics2D		resizedGraphics	= resizedImage.createGraphics();

		resizedGraphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, getInterpolation( interpolcation ) );

		resizedGraphics.drawImage( resizedImage, 0, 0, width, height, null );
		resizedGraphics.dispose();

		this.image = new Image( resizedImage );
		this.cacheGraphics();

		return this;
	}

	public BoxImage drawOval( int x, int y, int width, int height, boolean filled ) {
		if ( filled ) {
			this.graphics.fillOval( x, y, width, height );
		} else {
			this.graphics.drawOval( x, y, width, height );
		}

		return this;
	}

	public BoxImage drawBeveledRect( int x, int y, int width, int height, boolean raised, boolean filled ) {

		this.drawRect( x, y, width, height, filled );

		Color	originalColor	= this.graphics.getColor();
		Color	highlight		= originalColor.brighter().brighter();
		Color	shadow			= originalColor.darker().darker();

		this.graphics.setColor( raised ? highlight : shadow );
		// top
		this.graphics.drawLine( x, y, x + width, y );
		// left
		this.graphics.drawLine( x, y, x, y + height );

		this.graphics.setColor( raised ? shadow : highlight );

		// right
		this.graphics.drawLine( x + width, y, x + width, y + height );
		// bottom
		this.graphics.drawLine( x, y + height, x + width, y + height );

		this.graphics.setColor( originalColor );

		return this;
	}

	public BoxImage copy( int x, int y, int width, int height, int dx, int dy ) {
		BoxImage newImage = new BoxImage( width, height, ImageType.ARGB, "black" );

		newImage.drawImage( this, -x, -y );

		if ( dx != 0 || dy != 0 ) {
			newImage.drawImage( this, dx, dy );
		}

		return newImage;
	}

	public BoxImage drawImage( BoxImage image, int x, int y ) {
		this.graphics.drawImage( image.getBufferedImage(), new AffineTransform( 1f, 0f, 0f, 1f, x, y ), null );

		return this;
	}

	public BufferedImage getBufferedImage() {
		return this.image.getBufferedImage();
	}

	public BoxImage write( String path ) {
		// TODO determine format
		try {
			Imaging.writeImage( this.image.getBufferedImage(), new File( path ), ImageFormats.PNG );
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to save image", e );
		}

		return this;
	}

	public BoxImage crop( int x, int y, int width, int height ) {
		this.image.crop( x, y, width, height );

		return this;
	}

	public BoxImage negative() {
		BufferedImage bufferedImage = this.image.getBufferedImage();
		// Convert to negative
		for ( int y = 0; y < this.image.getHeight(); y++ ) {
			for ( int x = 0; x < this.image.getWidth(); x++ ) {
				int	p	= bufferedImage.getRGB( x, y );
				int	a	= ( p >> 24 ) & 0xff;
				int	r	= ( p >> 16 ) & 0xff;
				int	g	= ( p >> 8 ) & 0xff;
				int	b	= p & 0xff;

				// subtract RGB from 255
				r	= 255 - r;
				g	= 255 - g;
				b	= 255 - b;

				// set new RGB value
				p	= ( a << 24 ) | ( r << 16 ) | ( g << 8 ) | b;
				bufferedImage.setRGB( x, y, p );
			}
		}

		return this;
	}

	public BoxImage addBorder( int thickness, String color ) {
		Image			next	= new Image( this.image.getWidth() + ( thickness * 2 ), this.image.getHeight() + ( thickness * 2 ) );
		BufferedImage	bfImage	= next.getBufferedImage();
		Graphics2D		g		= bfImage.createGraphics();

		g.setColor( COLORS.get( color.toLowerCase() ) );
		g.fillRect( 0, 0, bfImage.getWidth(), bfImage.getHeight() );

		g.drawImage( this.image.getBufferedImage(), thickness, thickness, null );

		this.image		= next;
		this.graphics	= g;

		return this;
	}

	public BoxImage transpose( String transpose ) {
		// TODO transfer colors and strokes
		if ( transpose.equalsIgnoreCase( "vertical" ) ) {
			AffineTransform tx = AffineTransform.getScaleInstance( 1, -1 );
			tx.translate( 0, -this.image.getHeight() );
			AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
			this.image = new Image( op.filter( this.image.getBufferedImage(), null ) );
			this.cacheGraphics();
		} else if ( transpose.equalsIgnoreCase( "horizontal" ) ) {
			AffineTransform tx = AffineTransform.getScaleInstance( -1, 1 );
			tx.translate( -this.image.getWidth(), 0 );
			AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
			this.image = new Image( op.filter( this.image.getBufferedImage(), null ) );
			this.cacheGraphics();
		} else if ( transpose.equalsIgnoreCase( "diagonal" ) ) {
			transpose( "90" ).transpose( "horizontal" );
		} else if ( transpose.equalsIgnoreCase( "antidiagonal" ) ) {
			transpose( "270" ).transpose( "horizontal" );
		} else if ( transpose.equalsIgnoreCase( "90" ) ) {
			this.image.rotate( 90 );
		} else if ( transpose.equalsIgnoreCase( "180" ) ) {
			this.image.rotate( 180 );
		} else if ( transpose.equalsIgnoreCase( "270" ) ) {
			this.image.rotate( 270 );
		}

		return this;
	}

	public byte[] getBytes() {
		return this.image.getByteArray();
	}

	public BoxImage clearRect( int x, int y, int width, int height ) {
		graphics.clearRect( x, y, width, height );

		return this;
	}

	public BoxImage drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight, boolean filled ) {
		if ( filled ) {
			graphics.fillRoundRect( x, y, width, height, arcWidth, arcHeight );
		} else {
			graphics.drawRoundRect( x, y, width, height, arcWidth, arcHeight );
		}

		return this;
	}

	public BoxImage drawRect( int x, int y, int width, int height, boolean filled ) {
		if ( filled ) {
			graphics.fillRect( x, y, width, height );
		} else {
			graphics.drawRect( x, y, width, height );
		}

		return this;
	}

	public BoxImage fillRect( int x, int y, int width, int height ) {
		graphics.fillRect( x, y, width, height );
		return this;
	}

	public String getDrawingColor() {
		return this.drawingColor;
	}

	public BoxImage setDrawingColor( String color ) {
		this.drawingColor = color;

		if ( COLORS.containsKey( color.toLowerCase() ) ) {
			graphics.setColor( COLORS.get( color.toLowerCase() ) );
		}

		return this;
	}

	public BoxImage setBackgroundColor( String color ) {
		this.backgroundColor = color;

		if ( COLORS.containsKey( color.toLowerCase() ) ) {
			graphics.setBackground( COLORS.get( color.toLowerCase() ) );
		}

		return this;
	}

	public int getWidth() {
		return this.image.getWidth();
	}

	public int getHeight() {
		return this.image.getHeight();
	}

	public BoxImage blur( Integer radius ) {

		this.image.blur( radius.floatValue() );

		return this;
	}
}
