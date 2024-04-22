package ortus.boxlang.image;

import java.awt.image.BufferedImage;

public class BoxImage {

	private BufferedImage imageData;

	public BoxImage( BufferedImage imageData ) {
		this.imageData = imageData;
	}

	public int getWidth() {
		return this.imageData.getWidth();
	}

	public int getHeight() {
		return this.imageData.getHeight();
	}
}
