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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import ortus.boxlang.modules.image.util.EnumConverterUtil;
import ortus.boxlang.modules.image.util.ImageMetadataUtil;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.modules.image.util.StrokeBuilder;
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
import ortus.boxlang.runtime.util.FileSystemUtil;

/**
 * BoxImage is the central wrapper class for image manipulation in the BoxLang Image module.
 * It encapsulates a javaxt.io.Image and Java's BufferedImage, providing a fluent API for
 * image operations including drawing, transformations, filters, and metadata access.
 *
 * <p>
 * This class serves as the primary interface for all image BIFs (Built-In Functions) and
 * supports method chaining for convenient image processing workflows.
 * </p>
 *
 * <h2>Features</h2>
 * <ul>
 * <li>Image creation from files, URLs, URIs, Base64 strings, or blank canvases</li>
 * <li>Drawing operations (shapes, text, lines, curves)</li>
 * <li>Image transformations (rotate, scale, crop, flip, shear)</li>
 * <li>Filters and effects (blur, sharpen, grayscale, negative)</li>
 * <li>EXIF and IPTC metadata extraction and access</li>
 * <li>Color and stroke management with Graphics2D context</li>
 * <li>Base64 encoding/decoding support</li>
 * <li>Overlay and composite operations</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>
 * // Create from file
 * BoxImage img = new BoxImage( "path/to/image.png" );
 *
 * // Create blank canvas
 * BoxImage canvas = new BoxImage( 800, 600, ImageType.ARGB, "white" );
 *
 * // Method chaining
 * img.crop( 50, 50, 200, 200 )
 *     .blur( 5 )
 *     .grayScale()
 *     .write( "output.png" );
 *
 * // Drawing operations
 * canvas.setDrawingColor( "blue" )
 *     .drawRect( 10, 10, 100, 100, true )
 *     .setDrawingColor( "red" )
 *     .drawText( "Hello World", 50, 50 );
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * This class is NOT thread-safe. Each BoxImage instance should be used by a single thread
 * or externally synchronized when shared across threads.
 * </p>
 *
 * @see ortus.boxlang.modules.image.bifs Package containing all image BIF implementations
 * @see javaxt.io.Image The underlying image library
 * @see java.awt.Graphics2D The drawing context used for operations
 */
public class BoxImage {

	/** Map of named colors to java.awt.Color instances */
	public static final Map<String, Color>	COLORS;

	/** Default font family for text drawing operations */
	public static final String				DEFAULT_FONT_FAMILY	= Font.SANS_SERIF;

	/** Default font style (plain, not bold or italic) */
	public static final int					DEFAULT_FONT_STYLE	= Font.PLAIN;

	/** Default font size in points */
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

	/** Path or URL from which the image was loaded, if applicable */
	private String		sourcePath;

	/** Graphics2D context for drawing operations on the image */
	private Graphics2D	graphics;

	/** The underlying javaxt.io.Image wrapped by this BoxImage */
	private Image		image;

	/** Current drawing color (default: white) */
	private String		drawingColor	= "white";

	/** Current background color (default: white) */
	private String		backgroundColor	= "white";

	/** EXIF metadata extracted from the image file */
	private IStruct		exifData		= new Struct();

	/** IPTC metadata extracted from the image file */
	private IStruct		iptcData		= new Struct();

	/** Detected file type from metadata-extractor library */
	private FileType	fileType;

	/**
	 * Enumeration for specifying image dimensions in scaling operations.
	 */
	public enum Dimension {
		/** Width dimension */
		WIDTH,
		/** Height dimension */
		HEIGHT
	}

	/**
	 * Creates a BoxImage from a Base64-encoded string.
	 *
	 * @param base64String The Base64-encoded image data
	 *
	 * @return A new BoxImage instance
	 *
	 * @throws IOException If the Base64 string cannot be decoded or the image format is invalid
	 */
	public static BoxImage fromBase64( String base64String ) throws IOException {
		return new BoxImage( ImageIO.read( new ByteArrayInputStream( Base64.getDecoder().decode( base64String ) ) ) );
	}

	/**
	 * Creates a new blank BoxImage with specified dimensions and background color.
	 *
	 * @param width     The width of the image in pixels
	 * @param height    The height of the image in pixels
	 * @param imageType The image type (ARGB, RGB, etc.)
	 * @param color     The initial background color
	 */
	// TODO handle imageType
	public BoxImage( int width, int height, ImageType imageType, String color ) {
		this.image		= new Image( width, height );
		this.graphics	= this.image.getBufferedImage().createGraphics();

		this.setDrawingColor( color );
		this.fillRect( 0, 0, width, height );
		this.setDrawingColor( "black" );
	}

	/**
	 * Creates a BoxImage from a file path or URL string.
	 * Supports local file paths, http://, https://, and file:// URIs.
	 *
	 * @param imageURI The path or URL to the image file
	 *
	 * @throws MalformedURLException    If the URI is malformed
	 * @throws IOException              If the image cannot be read
	 * @throws ImageProcessingException If metadata extraction fails
	 * @throws URISyntaxException       If the URI syntax is invalid
	 */
	public BoxImage( String imageURI ) throws MalformedURLException, IOException, ImageProcessingException, URISyntaxException {
		this( stringToURI( imageURI ) );
	}

	/**
	 * Creates a BoxImage from a URI.
	 * Automatically extracts EXIF and IPTC metadata during creation.
	 *
	 * @param imageURI The URI to the image file
	 *
	 * @throws MalformedURLException    If the URI is malformed
	 * @throws IOException              If the image cannot be read
	 * @throws ImageProcessingException If metadata extraction fails
	 * @throws URISyntaxException       If the URI syntax is invalid
	 */
	public BoxImage( URI imageURI ) throws MalformedURLException, IOException, ImageProcessingException, URISyntaxException {
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

	/**
	 * Creates a BoxImage from an existing BufferedImage.
	 *
	 * @param imageData The BufferedImage to wrap
	 */
	public BoxImage( BufferedImage imageData ) {
		this.image = new Image( imageData );
		this.cacheGraphics();
	}

	/**
	 * Initializes or refreshes the Graphics2D context for drawing operations.
	 * Disposes of any existing graphics context before creating a new one.
	 * Preserves the current drawing and background colors.
	 */
	private void cacheGraphics() {
		if ( this.graphics != null ) {
			this.graphics.dispose();
		}

		this.graphics = this.image.getBufferedImage().createGraphics();

		this.setDrawingColor( this.drawingColor );
		this.setBackgroundColor( this.backgroundColor );
	}

	/**
	 * Gets the source path or URL from which this image was loaded.
	 *
	 * @return The source path, or null if the image was created from scratch or BufferedImage
	 */
	public String getSourcePath() {
		return this.sourcePath;
	}

	/**
	 * Retrieves comprehensive information about the image including dimensions,
	 * color model details, and source path.
	 *
	 * @return An IStruct containing image metadata with keys:
	 *         - height: Image height in pixels
	 *         - width: Image width in pixels
	 *         - colormodel: Struct with color model details (alpha support, color space, etc.)
	 *         - source: Original source path if available
	 */
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

	/**
	 * Converts the image to a Base64-encoded string in the specified format.
	 *
	 * @param format The image format (e.g., "png", "jpg", "gif")
	 *
	 * @return A Base64-encoded string representing the image
	 *
	 * @throws IOException If encoding fails
	 */
	public String toBase64String( String format ) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		ImageIO.write( this.image.getBufferedImage(), format, output );

		return Base64.getEncoder().encodeToString( output.toByteArray() );
	}

	/**
	 * Enables or disables anti-aliasing for drawing operations.
	 *
	 * @param useAntiAliasing true to enable anti-aliasing, false to disable
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage setAntiAliasing( boolean useAntiAliasing ) {
		this.graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
		    useAntiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );

		return this;
	}

	/**
	 * Scales the image to fit within a specific dimension while maintaining aspect ratio.
	 *
	 * @param size          The target size in pixels
	 * @param dimension     The dimension to fit (WIDTH or HEIGHT)
	 * @param interpolation The interpolation method ("bicubic", "bilinear", "nearest")
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Translates the drawing axis for subsequent drawing operations.
	 *
	 * @param x The x-axis translation offset
	 * @param y The y-axis translation offset
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage translateDrawingAxis( int x, int y ) {
		this.graphics.setTransform( AffineTransform.getTranslateInstance( x, y ) );

		return this;
	}

	/**
	 * Shears the drawing axis for subsequent drawing operations.
	 *
	 * @param x The x-axis shear factor
	 * @param y The y-axis shear factor
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage shearDrawingAxis( double x, double y ) {
		this.graphics.setTransform( AffineTransform.getShearInstance( x, y ) );

		return this;
	}

	/**
	 * Rotates the drawing axis around a specific point for subsequent drawing operations.
	 *
	 * @param angle The rotation angle in degrees
	 * @param x     The x-coordinate of the rotation point
	 * @param y     The y-coordinate of the rotation point
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage rotateDrawingAxis( double angle, int x, int y ) {
		this.graphics.setTransform( AffineTransform.getRotateInstance( Math.toRadians( angle ), x, y ) );

		return this;
	}

	/**
	 * Translates the entire image to a new position on a canvas of the same size.
	 *
	 * @param x The x-axis translation offset
	 * @param y The y-axis translation offset
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Rotates the image by the specified angle in degrees.
	 * The canvas is automatically expanded to fit the rotated image.
	 *
	 * @param angle The rotation angle in degrees (positive for clockwise)
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Overlays another image on top of this image using the specified composite rule and transparency.
	 *
	 * @param toOverlay    The BoxImage to overlay on this image
	 * @param overlayRule  The composite rule (e.g., "normal", "multiply", "screen")
	 * @param transparency The transparency level (0.0 to 1.0, where 1.0 is fully opaque)
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage overlay( BoxImage toOverlay, String overlayRule, double transparency ) {
		AlphaComposite	overlayComposite	= AlphaComposite.getInstance( EnumConverterUtil.getOveralyRule( overlayRule ), ( float ) transparency );
		Composite		original			= this.graphics.getComposite();

		this.graphics.setComposite( overlayComposite );
		this.graphics.drawImage( toOverlay.getBufferedImage(), 0, 0, null );

		this.graphics.setComposite( original );

		return this;
	}

	/**
	 * Converts the image to grayscale.
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Retrieves the EXIF metadata for this image as a struct.
	 *
	 * @return A struct containing EXIF metadata tags and values, or an empty struct if no EXIF data exists
	 */
	public IStruct getExifMetaData() {
		return exifData;
	}

	/**
	 * Retrieves the IPTC metadata for this image as a struct.
	 *
	 * @return A struct containing IPTC metadata tags and values, or an empty struct if no IPTC data exists
	 *
	 * @throws ImageProcessingException If metadata cannot be processed
	 * @throws FileNotFoundException    If the source image file cannot be found
	 * @throws IOException              If an I/O error occurs reading metadata
	 */
	public IStruct getIPTCMetaData() throws ImageProcessingException, FileNotFoundException, IOException {
		return iptcData;
	}

	/**
	 * Retrieves a specific EXIF metadata tag value by name.
	 *
	 * @param tagName The name of the EXIF tag to retrieve (e.g., "Model", "DateTimeOriginal")
	 *
	 * @return The value of the specified EXIF tag, or null if the tag doesn't exist
	 *
	 * @throws ImageProcessingException If metadata cannot be processed
	 * @throws FileNotFoundException    If the source image file cannot be found
	 * @throws IOException              If an I/O error occurs reading metadata
	 */
	public Object getExifMetaDataTag( String tagName ) throws ImageProcessingException, FileNotFoundException, IOException {
		return getExifMetaData().get( tagName );
	}

	/**
	 * Retrieves a specific IPTC metadata tag value by name.
	 *
	 * @param tagName The name of the IPTC tag to retrieve (e.g., "Caption", "Keywords")
	 *
	 * @return The value of the specified IPTC tag, or null if the tag doesn't exist
	 *
	 * @throws ImageProcessingException If metadata cannot be processed
	 * @throws FileNotFoundException    If the source image file cannot be found
	 * @throws IOException              If an I/O error occurs reading metadata
	 */
	public Object getIPTCMetaDataTag( String tagName ) throws ImageProcessingException, FileNotFoundException, IOException {
		return getIPTCMetaData().get( tagName );
	}

	/**
	 * Creates a copy of this image.
	 *
	 * @return A new BoxImage instance containing a copy of this image's data
	 */
	public BoxImage copy() {
		BoxImage newImage = new BoxImage( getWidth(), getHeight(), ImageType.ARGB, "black" );

		newImage.drawImage( this, 0, 0 );

		return newImage;
	}

	/**
	 * Draws an arc on the image.
	 *
	 * @param x          The x-coordinate of the upper-left corner of the bounding rectangle
	 * @param y          The y-coordinate of the upper-left corner of the bounding rectangle
	 * @param width      The width of the bounding rectangle
	 * @param height     The height of the bounding rectangle
	 * @param startAngle The starting angle in degrees (0 = 3 o'clock position)
	 * @param archAngle  The angular extent of the arc in degrees
	 * @param filled     true to fill the arc, false to draw only the outline
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage drawArc( int x, int y, int width, int height, int startAngle, int archAngle, boolean filled ) {

		if ( filled ) {
			this.graphics.fillArc( x, y, width, height, startAngle, archAngle );
		} else {
			this.graphics.drawArc( x, y, width, height, startAngle, archAngle );
		}

		return this;
	}

	/**
	 * Draws a cubic Bézier curve on the image.
	 *
	 * @param x1     The x-coordinate of the starting point
	 * @param y1     The y-coordinate of the starting point
	 * @param ctrlx1 The x-coordinate of the first control point
	 * @param ctrly1 The y-coordinate of the first control point
	 * @param ctrlx2 The x-coordinate of the second control point
	 * @param ctrly2 The y-coordinate of the second control point
	 * @param x2     The x-coordinate of the ending point
	 * @param y2     The y-coordinate of the ending point
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage drawCubicCurve( int x1, int y1, int ctrlx1, int ctrly1, int ctrlx2, int ctrly2, int x2, int y2 ) {
		this.graphics.draw( new CubicCurve2D.Double( x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2 ) );

		return this;
	}

	/**
	 * Draws a line between two points on the image.
	 *
	 * @param x1 The x-coordinate of the starting point
	 * @param y1 The y-coordinate of the starting point
	 * @param x2 The x-coordinate of the ending point
	 * @param y2 The y-coordinate of the ending point
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage drawLine( int x1, int y1, int x2, int y2 ) {
		this.graphics.drawLine( x1, y1, x2, y2 );

		return this;
	}

	/**
	 * Draws multiple connected lines or a polygon on the image.
	 *
	 * @param xCoords   An array of x-coordinates for the points
	 * @param yCoords   An array of y-coordinates for the points
	 * @param isPolygon true to close the shape as a polygon, false for a polyline
	 * @param filled    true to fill the polygon (only applies if isPolygon is true), false to draw only the outline
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Draws a quadratic Bézier curve on the image.
	 *
	 * @param ctrlx1 The x-coordinate of the control point
	 * @param ctrly1 The y-coordinate of the control point
	 * @param x1     The x-coordinate of the starting point
	 * @param y1     The y-coordinate of the starting point
	 * @param x2     The x-coordinate of the ending point
	 * @param y2     The y-coordinate of the ending point
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage drawQuadraticCurve( int ctrlx1, int ctrly1, int x1, int y1, int x2, int y2 ) {
		this.graphics.draw( new QuadCurve2D.Double( ctrlx1, ctrly1, x1, y1, x2, y2 ) );

		return this;
	}

	/**
	 * Draws text on the image at the specified coordinates.
	 *
	 * @param str The text string to draw
	 * @param x   The x-coordinate of the baseline of the text
	 * @param y   The y-coordinate of the baseline of the text
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage drawText( String str, int x, int y ) {
		this.graphics.drawString( str, x, y );
		return this;
	}

	/**
	 * Sets the drawing stroke properties for subsequent drawing operations.
	 *
	 * @param strokeConfig A struct containing stroke properties:
	 *                     - width: The stroke width (float)
	 *                     - endCaps: The end cap style ("butt", "round", "square")
	 *                     - lineJoins: The line join style ("miter", "round", "bevel")
	 *                     - miterLimit: The miter limit (float)
	 *                     - dashArray: An array of dash pattern lengths (array)
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage setDrawingStroke( IStruct strokeConfig ) {
		StrokeBuilder builder = new StrokeBuilder();

		if ( strokeConfig.containsKey( KeyDictionary.width ) ) {
			builder.width( FloatCaster.cast( strokeConfig.get( KeyDictionary.width ) ) );
		}

		if ( strokeConfig.containsKey( KeyDictionary.endCaps ) ) {
			builder.endCaps( EnumConverterUtil.getEndCapInt( StringCaster.cast( strokeConfig.get( KeyDictionary.endCaps ) ) ) );
		}

		if ( strokeConfig.containsKey( KeyDictionary.lineJoins ) ) {
			builder.lineJoins( EnumConverterUtil.getLineJoinsInt( StringCaster.cast( strokeConfig.get( KeyDictionary.lineJoins ) ) ) );
		}

		if ( strokeConfig.containsKey( KeyDictionary.miterLimit ) ) {
			builder.miterLimit( FloatCaster.cast( strokeConfig.get( KeyDictionary.miterLimit ) ) );
		}

		if ( strokeConfig.containsKey( KeyDictionary.dashArray ) ) {
			Float[]	floats			= ArrayCaster.cast( strokeConfig.get( KeyDictionary.dashArray ) ).toArray( new Float[] {} );
			float[]	actualFloats	= new float[ floats.length ];

			// yuck
			for ( int i = 0; i < floats.length; i++ ) {
				actualFloats[ i ] = floats[ i ].floatValue();
			}

			builder.dashArray( actualFloats );
		}

		if ( strokeConfig.containsKey( KeyDictionary.dashPhase ) ) {
			builder.dashPhase( FloatCaster.cast( strokeConfig.get( KeyDictionary.dashPhase ) ) );
		}

		this.graphics.setStroke( builder.build() );

		return this;
	}

	/**
	 * Sets the drawing transparency for subsequent drawing operations.
	 *
	 * @param transparency The transparency percentage (0.0 to 100.0, where 0.0 is fully transparent and 100.0 is fully opaque)
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage setDrawingTransparency( double transparency ) {
		AlphaComposite composite = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, ( float ) ( transparency / 100.0 ) );
		this.graphics.setComposite( composite );
		return this;
	}

	/**
	 * Shears the image along the specified dimension.
	 *
	 * @param amount The amount of shear to apply
	 * @param dim    The dimension to shear along (HEIGHT or WIDTH)
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Sharpens the image by applying a convolution kernel.
	 *
	 * @param gain The sharpening intensity (0.0 to 10.0, where higher values produce more sharpening)
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Draws text on the image with font styling applied.
	 *
	 * @param str        The text string to draw
	 * @param x          The x-coordinate of the baseline of the text
	 * @param y          The y-coordinate of the baseline of the text
	 * @param fontConfig A struct containing font properties:
	 *                   - font: The font family name (string)
	 *                   - style: The font style ("plain", "bold", "italic", "bolditalic")
	 *                   - size: The font size in points (integer)
	 *                   - strikeThrough: Whether to apply strikethrough (boolean)
	 *                   - underline: Whether to underline the text (boolean)
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage drawText( String str, int x, int y, IStruct fontConfig ) {
		Map<TextAttribute, Object> attr = new HashMap<TextAttribute, Object>();

		attr.put( TextAttribute.FONT, DEFAULT_FONT_FAMILY );
		attr.put( TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR );
		attr.put( TextAttribute.STRIKETHROUGH, false );
		attr.put( TextAttribute.UNDERLINE, false );

		String family = fontConfig.getAsString( KeyDictionary.font );
		if ( family != null ) {
			attr.put( TextAttribute.FAMILY, family );
		}

		String style = fontConfig.getAsString( KeyDictionary.style );
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

		CastAttempt<Integer> size = IntegerCaster.attempt( fontConfig.get( KeyDictionary.size ) );
		if ( size.wasSuccessful() ) {
			attr.put( TextAttribute.SIZE, size.get() );
		}

		CastAttempt<Boolean> strikeThrough = BooleanCaster.attempt( fontConfig.get( KeyDictionary.strikeThrough ) );
		if ( strikeThrough.wasSuccessful() ) {
			attr.put( TextAttribute.STRIKETHROUGH, strikeThrough.get() );
		}

		CastAttempt<Boolean> underline = BooleanCaster.attempt( fontConfig.get( KeyDictionary.underline ) );
		if ( underline.wasSuccessful() && underline.get() ) {
			attr.put( TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON );
		}

		Font currentFont = new Font( attr );

		this.graphics.setFont( currentFont );

		this.drawText( str, x, y );

		this.graphics.setFont( null );

		return this;
	}

	/**
	 * Resizes the image to the specified dimensions.
	 *
	 * @param width          The target width in pixels
	 * @param height         The target height in pixels
	 * @param interpolcation The interpolation method ("bicubic", "bilinear", "nearest")
	 * @param blurFactor     The blur factor (currently unused, reserved for future use)
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage resize( int width, int height, String interpolcation, int blurFactor ) {
		BufferedImage	resizedImage	= new BufferedImage( width, height, this.image.getBufferedImage().getType() );
		Graphics2D		resizedGraphics	= resizedImage.createGraphics();

		resizedGraphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, EnumConverterUtil.getInterpolation( interpolcation ) );

		resizedGraphics.drawImage( this.image.getBufferedImage(), 0, 0, width, height, null );
		resizedGraphics.dispose();

		this.image = new Image( resizedImage );
		this.cacheGraphics();

		return this;
	}

	/**
	 * Draws an oval on the image.
	 *
	 * @param x      The x-coordinate of the upper-left corner of the bounding rectangle
	 * @param y      The y-coordinate of the upper-left corner of the bounding rectangle
	 * @param width  The width of the bounding rectangle
	 * @param height The height of the bounding rectangle
	 * @param filled true to fill the oval, false to draw only the outline
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage drawOval( int x, int y, int width, int height, boolean filled ) {
		if ( filled ) {
			this.graphics.fillOval( x, y, width, height );
		} else {
			this.graphics.drawOval( x, y, width, height );
		}

		return this;
	}

	/**
	 * Draws a beveled rectangle with 3D effect on the image.
	 *
	 * @param x      The x-coordinate of the upper-left corner
	 * @param y      The y-coordinate of the upper-left corner
	 * @param width  The width of the rectangle
	 * @param height The height of the rectangle
	 * @param raised true for a raised bevel, false for a lowered bevel
	 * @param filled true to fill the rectangle, false to draw only the outline
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Creates a copy of a rectangular region of this image.
	 *
	 * @param x      The x-coordinate of the region to copy
	 * @param y      The y-coordinate of the region to copy
	 * @param width  The width of the region to copy
	 * @param height The height of the region to copy
	 * @param dx     The x-offset for optional secondary draw (0 for none)
	 * @param dy     The y-offset for optional secondary draw (0 for none)
	 *
	 * @return A new BoxImage containing the copied region
	 */
	public BoxImage copy( int x, int y, int width, int height, int dx, int dy ) {
		BoxImage newImage = new BoxImage( width, height, ImageType.ARGB, "black" );

		newImage.drawImage( this, -x, -y );

		if ( dx != 0 || dy != 0 ) {
			newImage.drawImage( this, dx, dy );
		}

		return newImage;
	}

	/**
	 * Draws another BoxImage onto this image at the specified coordinates.
	 *
	 * @param image The BoxImage to draw
	 * @param x     The x-coordinate where the image should be drawn
	 * @param y     The y-coordinate where the image should be drawn
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage drawImage( BoxImage image, int x, int y ) {
		this.graphics.drawImage( image.getBufferedImage(), new AffineTransform( 1f, 0f, 0f, 1f, x, y ), null );

		return this;
	}

	/**
	 * Gets the underlying Java BufferedImage.
	 *
	 * @return The BufferedImage wrapped by this BoxImage
	 */
	public BufferedImage getBufferedImage() {
		return this.image.getBufferedImage();
	}

	/**
	 * Writes the image to a file at the specified path.
	 * Automatically creates parent directories if they don't exist.
	 * Currently saves as PNG format.
	 *
	 * @param path The file path where the image should be saved
	 *
	 * @return This BoxImage instance for method chaining
	 *
	 * @throws BoxRuntimeException If the image cannot be saved
	 */
	public BoxImage write( String path ) {
		// TODO determine format
		try {
			File	targetFile	= new File( path );
			// Create parent directories if they don't exist using BoxLang FileSystemUtil
			File	parentDir	= targetFile.getParentFile();
			if ( parentDir != null && !parentDir.exists() ) {
				FileSystemUtil.createDirectory( parentDir.getAbsolutePath() );
			}
			Imaging.writeImage( this.image.getBufferedImage(), targetFile, ImageFormats.PNG );
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to save image", e );
		}

		return this;
	}

	/**
	 * Crops the image to the specified rectangular region.
	 *
	 * @param x      The x-coordinate of the upper-left corner of the crop region
	 * @param y      The y-coordinate of the upper-left corner of the crop region
	 * @param width  The width of the crop region
	 * @param height The height of the crop region
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage crop( int x, int y, int width, int height ) {
		this.image.crop( x, y, width, height );

		return this;
	}

	/**
	 * Inverts the colors of the image to create a negative effect.
	 * Each RGB value is subtracted from 255, while preserving alpha transparency.
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Adds a solid color border around the image.
	 *
	 * @param thickness The thickness of the border in pixels
	 * @param color     The color name or hex code for the border
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Transposes (flips or rotates) the image according to the specified operation.
	 *
	 * @param transpose The transpose operation ("90", "180", "270", "flipHorizontal", "flipVertical")
	 *
	 * @return This BoxImage instance for method chaining
	 */
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

	/**
	 * Gets the image data as a byte array.
	 *
	 * @return A byte array containing the image data
	 */
	public byte[] getBytes() {
		return this.image.getByteArray();
	}

	/**
	 * Clears a rectangular region on the image to transparent.
	 *
	 * @param x      The x-coordinate of the upper-left corner of the region
	 * @param y      The y-coordinate of the upper-left corner of the region
	 * @param width  The width of the region to clear
	 * @param height The height of the region to clear
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage clearRect( int x, int y, int width, int height ) {
		graphics.clearRect( x, y, width, height );

		return this;
	}

	/**
	 * Draws a rounded rectangle on the image.
	 *
	 * @param x         The x-coordinate of the upper-left corner
	 * @param y         The y-coordinate of the upper-left corner
	 * @param width     The width of the rectangle
	 * @param height    The height of the rectangle
	 * @param arcWidth  The horizontal diameter of the arc at the corners
	 * @param arcHeight The vertical diameter of the arc at the corners
	 * @param filled    true to fill the rectangle, false to draw only the outline
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight, boolean filled ) {
		if ( filled ) {
			graphics.fillRoundRect( x, y, width, height, arcWidth, arcHeight );
		} else {
			graphics.drawRoundRect( x, y, width, height, arcWidth, arcHeight );
		}

		return this;
	}

	/**
	 * Draws a rectangle on the image.
	 *
	 * @param x      The x-coordinate of the upper-left corner
	 * @param y      The y-coordinate of the upper-left corner
	 * @param width  The width of the rectangle
	 * @param height The height of the rectangle
	 * @param filled true to fill the rectangle, false to draw only the outline
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage drawRect( int x, int y, int width, int height, boolean filled ) {
		if ( filled ) {
			graphics.fillRect( x, y, width, height );
		} else {
			graphics.drawRect( x, y, width, height );
		}

		return this;
	}

	/**
	 * Fills a rectangle with the current drawing color.
	 *
	 * @param x      The x-coordinate of the upper-left corner
	 * @param y      The y-coordinate of the upper-left corner
	 * @param width  The width of the rectangle
	 * @param height The height of the rectangle
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage fillRect( int x, int y, int width, int height ) {
		graphics.fillRect( x, y, width, height );
		return this;
	}

	/**
	 * Gets the current drawing color.
	 *
	 * @return The current drawing color as a string (name or hex code)
	 */
	public String getDrawingColor() {
		return this.drawingColor;
	}

	/**
	 * Sets the color for subsequent drawing operations.
	 *
	 * @param color The color to set (color name like "red", "blue", or hex code like "#FF0000")
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage setDrawingColor( String color ) {
		this.drawingColor = color;

		if ( COLORS.containsKey( color.toLowerCase() ) ) {
			graphics.setColor( COLORS.get( color.toLowerCase() ) );
		}

		return this;
	}

	/**
	 * Sets the background color for subsequent drawing operations.
	 *
	 * @param color The color to set (color name like "white", "black", or hex code)
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage setBackgroundColor( String color ) {
		this.backgroundColor = color;

		if ( COLORS.containsKey( color.toLowerCase() ) ) {
			graphics.setBackground( COLORS.get( color.toLowerCase() ) );
		}

		return this;
	}

	/**
	 * Gets the width of the image in pixels.
	 *
	 * @return The image width
	 */
	public int getWidth() {
		return this.image.getWidth();
	}

	/**
	 * Gets the height of the image in pixels.
	 *
	 * @return The image height
	 */
	public int getHeight() {
		return this.image.getHeight();
	}

	/**
	 * Applies a blur filter to the image.
	 *
	 * @param radius The blur radius (higher values produce more blur)
	 *
	 * @return This BoxImage instance for method chaining
	 */
	public BoxImage blur( Integer radius ) {

		this.image.blur( radius.floatValue() );

		return this;
	}

	/**
	 * Retrieves an InputStream for the given image input URI, which can be a URL or file path.
	 *
	 * @param imageInput The image input as a URI (URL or file path)
	 *
	 * @return An InputStream for the image data
	 *
	 * @throws MalformedURLException If the URL is malformed
	 * @throws IOException           If an I/O error occurs
	 * @throws URISyntaxException    If the URI syntax is incorrect
	 */
	private static InputStream getInputStream( URI imageInput ) throws MalformedURLException, IOException, URISyntaxException {
		if ( imageInput.toString().toLowerCase().startsWith( "http" ) ) {
			return resolveURLToInputStream( imageInput );
		}
		return new FileInputStream( FileSystemUtil.createFileUri( imageInput.toString() ).getPath() );
	}

	/**
	 * Resolves a URL to an InputStream, handling HTTP redirects.
	 *
	 * @param imageInput The image input as a URI
	 *
	 * @return An InputStream for the image data
	 *
	 * @throws MalformedURLException If the URL is malformed
	 * @throws IOException           If an I/O error occurs
	 * @throws URISyntaxException    If the URI syntax is incorrect
	 */
	private static InputStream resolveURLToInputStream( URI imageInput ) throws MalformedURLException, IOException, URISyntaxException {
		URL					url			= imageInput.toURL();
		HttpURLConnection	connection	= null;
		int					responseCode;
		String				newLocation;
		do {
			connection = ( HttpURLConnection ) url.openConnection();
			connection.setInstanceFollowRedirects( false ); // Disable automatic redirects
			responseCode = connection.getResponseCode();

			if ( responseCode >= 300 && responseCode <= 308 && responseCode != 304 ) { // Check for redirect status codes
				newLocation = connection.getHeaderField( "Location" );
				if ( newLocation != null ) {
					url = url.toURI().resolve( newLocation ).toURL(); // Resolve new URL relative to the current one
					connection.disconnect(); // Disconnect previous connection
				} else {
					throw new BoxRuntimeException(
					    "Error retrieving remote image [" + imageInput + "]. A redirect status was received but no Location header found." );
				}
			} else {
				break; // Not a redirect, or an error
			}
		} while ( true );

		return connection.getInputStream();
	}

	/**
	 * Converts a string input to a URI.
	 * If the input is a valid URL, it is converted to a URI.
	 * If not, it is treated as a local file path and converted to a file URI.
	 *
	 * @param input The input string (URL or file path)
	 *
	 * @return The corresponding URI
	 */
	public static URI stringToURI( String input ) {
		try {
			// Try to parse as a URL (e.g., http://, file://, etc.)
			URI uri = URI.create( input );
			return uri;  // Also handles encoding
		} catch ( Exception e ) {
			// Not a valid URL? Treat as a local file path
			Path path = Paths.get( input );
			return path.toUri();  // Converts to file:// URI
		}
	}
}
