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
package ortus.boxlang.modules.image;

import java.awt.AlphaComposite;
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
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageProcessingException;

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
	private IStruct		exifData		= new Struct();
	private IStruct		iptcData		= new Struct();
	private FileType	fileType;

	public enum Dimension {
		WIDTH,
		HEIGHT
	}

	public static BoxImage fromBase64( String base64String ) throws IOException {
		return new BoxImage( ImageIO.read( new ByteArrayInputStream( Base64.getDecoder().decode( base64String ) ) ) );
	}

	// TODO handle imageType
	public BoxImage( int width, int height, ImageType imageType, String color ) {
		this.image		= new Image( width, height );
		this.graphics	= this.image.getBufferedImage().createGraphics();

		this.setDrawingColor( color );
		this.fillRect( 0, 0, width, height );
		this.setDrawingColor( "black" );
	}

	public BoxImage( String imageURI ) throws MalformedURLException, IOException, ImageProcessingException {
		this( URI.create( imageURI ) );
	}

	public BoxImage( URI imageURI ) throws MalformedURLException, IOException, ImageProcessingException {
		this.sourcePath = imageURI.toString();
		InputStream				dataStream	= getInputStream( imageURI );
		byte[]					data		= dataStream.readAllBytes();
		ByteArrayInputStream	bas			= new ByteArrayInputStream( data );
		this.fileType = FileTypeDetector.detectFileType( bas );
		bas.reset();
		exifData = ImageMetadataUtil.readExifMetaData( bas );
		bas.reset();
		iptcData = ImageMetadataUtil.readIPTCMetaData( bas );
		bas.reset();
		this.image = new Image( bas );

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
		colorModel.put( "colorspace", EnumConverterUtil.getColorSpaceDescription( cm.getColorSpace().getType() ) );
		colorModel.put( "num_color_components", cm.getNumColorComponents() );
		colorModel.put( "num_components", cm.getNumComponents() );
		colorModel.put( "pixel_size", cm.getPixelSize() );
		colorModel.put( "transparency", EnumConverterUtil.getTransparencyDescription( cm.getTransparency() ) );
		info.put( "colormodel", colorModel );
		info.put( "source", this.getSourcePath() );

		return info;
	}

	public String toBase64String( String format ) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		ImageIO.write( this.image.getBufferedImage(), format, output );

		return Base64.getEncoder().encodeToString( output.toByteArray() );
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

	public BoxImage translateDrawingAxis( int x, int y ) {
		this.graphics.setTransform( AffineTransform.getTranslateInstance( x, y ) );

		return this;
	}

	public BoxImage shearDrawingAxis( double x, double y ) {
		this.graphics.setTransform( AffineTransform.getShearInstance( x, y ) );

		return this;
	}

	public BoxImage rotateDrawingAxis( double angle, int x, int y ) {
		this.graphics.setTransform( AffineTransform.getRotateInstance( Math.toRadians( angle ), x, y ) );

		return this;
	}

	public BoxImage translate( int x, int y ) {
		BufferedImage	resizedImage	= new BufferedImage( this.image.getWidth(), this.image.getHeight(),
		    this.image.getBufferedImage().getType() );
		Graphics2D		resizedGraphics	= resizedImage.createGraphics();

		resizedGraphics.setColor( Color.BLACK );
		resizedGraphics.fillRect( 0, 0, this.getWidth(), this.getHeight() );
		resizedGraphics.drawImage( this.image.getBufferedImage(), x, y, null );
		resizedGraphics.dispose();

		this.image = new Image( resizedImage );
		this.cacheGraphics();

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
		AlphaComposite	overlayComposite	= AlphaComposite.getInstance( EnumConverterUtil.getOveralyRule( overlayRule ), ( float ) transparency );
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

	public IStruct getExifMetaData() {
		return exifData;
	}

	public IStruct getIPTCMetaData() throws ImageProcessingException, FileNotFoundException, IOException {
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
			builder.endCaps( EnumConverterUtil.getEndCapInt( StringCaster.cast( strokeConfig.get( ImageKeys.endCaps ) ) ) );
		}

		if ( strokeConfig.containsKey( ImageKeys.lineJoins ) ) {
			builder.lineJoins( EnumConverterUtil.getLineJoinsInt( StringCaster.cast( strokeConfig.get( ImageKeys.lineJoins ) ) ) );
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

	public BoxImage shear( double amount, Dimension dim ) {
		Rectangle		rect	= new Rectangle( 0, 0, this.image.getWidth(), this.image.getHeight() );
		AffineTransform	shear;
		if ( dim == Dimension.HEIGHT ) {
			shear = AffineTransform.getShearInstance( 0, amount );
		} else {
			shear = AffineTransform.getShearInstance( amount, 0 );
		}
		Shape			sheared			= shear.createTransformedShape( rect );
		Rectangle		bounds			= sheared.getBounds();
		int				newWidth		= Double.valueOf( bounds.getWidth() ).intValue();
		int				newHeight		= Double.valueOf( bounds.getHeight() ).intValue();

		BufferedImage	resizedImage	= new BufferedImage( newWidth, newHeight,
		    this.image.getBufferedImage().getType() );
		Graphics2D		resizedGraphics	= resizedImage.createGraphics();

		resizedGraphics.fillRect( 0, 0, newWidth, newHeight );
		resizedGraphics.setTransform( shear );
		resizedGraphics.drawImage( this.image.getBufferedImage(), 0, 0, null );
		resizedGraphics.dispose();

		this.image = new Image( resizedImage );
		this.cacheGraphics();
		if ( dim == Dimension.HEIGHT ) {
			this.graphics.shear( 0, amount );
		} else {
			this.graphics.shear( amount, 0 );
		}

		return this;
	}

	public BoxImage sharpen( double gain ) {
		float	step	= 0.1f;
		float	num		= step * ( float ) gain;
		float	center	= 1f + ( float ) ( num * 8f );
		Kernel	kernel	= new Kernel( 3, 3,
		    new float[] {
		        -num, -num, -num,
		        -num, center, -num,
		        -num, -num, -num } );

		this.image = new Image( new ConvolveOp( kernel ).filter( this.image.getBufferedImage(), null ) );
		this.cacheGraphics();

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

		resizedGraphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, EnumConverterUtil.getInterpolation( interpolcation ) );

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

	private static InputStream getInputStream( String imageInput ) throws MalformedURLException, IOException {

		return getInputStream( URI.create( imageInput ) );
	}

	private static InputStream getInputStream( URI imageInput ) throws MalformedURLException, IOException {

		if ( imageInput.toString().toLowerCase().startsWith( "http" ) ) {

			return imageInput.toURL().openStream();

		}

		return new FileInputStream( imageInput.toString() );
	}
}
