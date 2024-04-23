package ortus.boxlang.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;

import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class BoxImage {

	public static final Map<String, Color> COLORS;

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

	private BufferedImage	imageData;
	private Graphics		graphics;
	private String			drawingColor	= "white";

	public BoxImage( BufferedImage imageData ) {
		this.imageData	= imageData;
		this.graphics	= imageData.getGraphics();
	}

	public BoxImage write( String path ) {
		// TODO determine format
		try {
			Imaging.writeImage( imageData, new File( path ), ImageFormats.PNG );
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to save image", e );
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

	public int getWidth() {
		return this.imageData.getWidth();
	}

	public int getHeight() {
		return this.imageData.getHeight();
	}
}
