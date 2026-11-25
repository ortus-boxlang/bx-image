package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.modules.image.util.SvgUtil;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Array;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class ImageSvgTest extends BaseIntegrationTest {

	private static final String SVG = "src/test/resources/svg/";

	@DisplayName( "It should read an SVG at its intrinsic width and height" )
	@Test
	public void testReadSvgIntrinsicSize() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "%slogo.svg" );
			width = img.getWidth();
			height = img.getHeight();
			format = img.getFormat();
		""".formatted( SVG ), context );
		// @formatter:on

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 120 );
		assertThat( variables.get( Key.of( "height" ) ) ).isEqualTo( 60 );
		assertThat( variables.get( Key.of( "format" ) ) ).isEqualTo( "png" );
	}

	@DisplayName( "It should render an SVG with an alpha channel and its shapes in place" )
	@Test
	public void testReadSvgPixels() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageNew( "%slogo.svg" );
			buffered = ImageGetBufferedImage( img );
		""".formatted( SVG ), context );
		// @formatter:on

		BufferedImage buffered = ( BufferedImage ) variables.get( Key.of( "buffered" ) );
		assertThat( buffered.getColorModel().hasAlpha() ).isTrue();
		// The corner is outside the rounded rect: fully transparent
		assertThat( buffered.getRGB( 0, 0 ) >>> 24 ).isEqualTo( 0 );
		// Inside the rect, outside the circle: opaque #e63946
		assertThat( buffered.getRGB( 20, 30 ) ).isEqualTo( 0xFFE63946 );
		// The circle's centre: opaque #1d3557
		assertThat( buffered.getRGB( 60, 30 ) ).isEqualTo( 0xFF1D3557 );
	}

	@DisplayName( "It should size an SVG from its viewBox when it has no width and height" )
	@Test
	public void testReadSvgViewBoxOnly() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "%sviewbox-only.svg" );
			width = img.getWidth();
			height = img.getHeight();
		""".formatted( SVG ), context );
		// @formatter:on

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 40 );
		assertThat( variables.get( Key.of( "height" ) ) ).isEqualTo( 20 );
	}

	@DisplayName( "It should default to 512 x 512 when an SVG has no viewBox, width or height" )
	@Test
	public void testReadSvgDefaultSize() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "%sno-size.svg" );
			width = img.getWidth();
			height = img.getHeight();
		""".formatted( SVG ), context );
		// @formatter:on

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( SvgUtil.DEFAULT_SIZE );
		assertThat( variables.get( Key.of( "height" ) ) ).isEqualTo( SvgUtil.DEFAULT_SIZE );
	}

	@DisplayName( "It should detect SVG by content when the extension is not svg" )
	@Test
	public void testReadSvgByContent() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "%slogo-svg-content.txt" );
			width = img.getWidth();
			height = img.getHeight();
		""".formatted( SVG ), context );
		// @formatter:on

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 16 );
		assertThat( variables.get( Key.of( "height" ) ) ).isEqualTo( 8 );
	}

	@DisplayName( "It should render an SVG straight at a target width, keeping the aspect ratio" )
	@Test
	public void testReadSvgAtWidth() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( path = "%slogo.svg", width = 480 );
			buffered = ImageGetBufferedImage( img );
			width = img.getWidth();
			height = img.getHeight();
		""".formatted( SVG ), context );
		// @formatter:on

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 480 );
		assertThat( variables.get( Key.of( "height" ) ) ).isEqualTo( 240 );
		// Rendered, not upscaled: the circle edge is still crisp at the new scale
		BufferedImage buffered = ( BufferedImage ) variables.get( Key.of( "buffered" ) );
		assertThat( buffered.getRGB( 240, 120 ) ).isEqualTo( 0xFF1D3557 );
	}

	@DisplayName( "It should render an SVG at a target height and at an exact width and height" )
	@Test
	public void testReadSvgAtHeightAndBox() {
		// @formatter:off
		runtime.executeSource( """
			byHeight = ImageRead( path = "%1$slogo.svg", height = 30 );
			box = ImageRead( "%1$slogo.svg", 200, 200 );
			heightWidth = byHeight.getWidth();
			heightHeight = byHeight.getHeight();
			boxWidth = box.getWidth();
			boxHeight = box.getHeight();
		""".formatted( SVG ), context );
		// @formatter:on

		assertThat( variables.get( Key.of( "heightWidth" ) ) ).isEqualTo( 60 );
		assertThat( variables.get( Key.of( "heightHeight" ) ) ).isEqualTo( 30 );
		assertThat( variables.get( Key.of( "boxWidth" ) ) ).isEqualTo( 200 );
		assertThat( variables.get( Key.of( "boxHeight" ) ) ).isEqualTo( 200 );
	}

	@DisplayName( "It should resize a raster image when ImageRead is given a width" )
	@Test
	public void testReadRasterAtWidth() {
		// @formatter:off
		runtime.executeSource( """
			original = ImageRead( "src/test/resources/logo.png" );
			img = ImageRead( path = "src/test/resources/logo.png", width = 50 );
			width = img.getWidth();
			height = img.getHeight();
			expectedHeight = round( 50 * original.getHeight() / original.getWidth() );
		""", context );
		// @formatter:on

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 50 );
		assertThat( ( ( Number ) variables.get( Key.of( "height" ) ) ).intValue() )
		    .isEqualTo( ( ( Number ) variables.get( Key.of( "expectedHeight" ) ) ).intValue() );
	}

	@DisplayName( "It should resize a rendered SVG" )
	@Test
	public void testResizeSvg() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "%slogo.svg" ).resize( 60, 30 );
			width = img.getWidth();
			height = img.getHeight();
		""".formatted( SVG ), context );
		// @formatter:on

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 60 );
		assertThat( variables.get( Key.of( "height" ) ) ).isEqualTo( 30 );
	}

	@DisplayName( "It should grayscale a rendered SVG" )
	@Test
	public void testGrayScaleSvg() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageRead( "%slogo.svg" ).grayScale();
			buffered = ImageGetBufferedImage( img );
		""".formatted( SVG ), context );
		// @formatter:on

		BufferedImage	buffered	= ( BufferedImage ) variables.get( Key.of( "buffered" ) );
		int				rgb			= buffered.getRGB( 20, 30 );
		int				red			= ( rgb >> 16 ) & 0xFF;
		int				green		= ( rgb >> 8 ) & 0xFF;
		int				blue		= rgb & 0xFF;
		assertThat( buffered.getType() ).isEqualTo( BufferedImage.TYPE_BYTE_GRAY );
		assertThat( green ).isEqualTo( red );
		assertThat( blue ).isEqualTo( red );
	}

	@DisplayName( "It should write a rendered SVG as PNG" )
	@Test
	public void testWriteSvgAsPng() throws IOException {
		String outputFile = "src/test/resources/generated/svg-logo.png";

		// @formatter:off
		runtime.executeSource( """
			ImageRead( "%s" ).write( "%s" );
			reloaded = ImageRead( "%s" );
			width = reloaded.getWidth();
		""".formatted( SVG + "logo.svg", outputFile, outputFile ), context );
		// @formatter:on

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 120 );
		byte[] written = Files.readAllBytes( Path.of( outputFile ) );
		// PNG signature
		assertThat( written[ 1 ] ).isEqualTo( ( byte ) 'P' );
		assertThat( written[ 2 ] ).isEqualTo( ( byte ) 'N' );
		assertThat( written[ 3 ] ).isEqualTo( ( byte ) 'G' );
	}

	@DisplayName( "It should refuse to write back to the source SVG and leave it untouched" )
	@Test
	public void testWriteBackToSvgFails() throws IOException {
		Path copy = Path.of( "src/test/resources/generated/svg-writeback.svg" );
		Files.createDirectories( copy.getParent() );
		Files.copy( Path.of( SVG + "logo.svg" ), copy, java.nio.file.StandardCopyOption.REPLACE_EXISTING );
		byte[]				original	= Files.readAllBytes( copy );

		BoxRuntimeException	e			= assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                                                        ImageRead( "%s" ).write();
		                                                                                                        """
		    .formatted( copy.toString() ), context ) );

		assertThat( e.getMessage() ).contains( "svg" );
		assertThat( Files.readAllBytes( copy ) ).isEqualTo( original );
	}

	@DisplayName( "It should read an SVG from Base64" )
	@Test
	public void testReadSvgBase64() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageReadBase64( toBase64( fileRead( "%slogo.svg" ) ) );
			width = img.getWidth();
			height = img.getHeight();
		""".formatted( SVG ), context );
		// @formatter:on

		assertThat( variables.get( Key.of( "width" ) ) ).isEqualTo( 120 );
		assertThat( variables.get( Key.of( "height" ) ) ).isEqualTo( 60 );
	}

	@DisplayName( "It should fail with a clear error for a malformed SVG" )
	@Test
	public void testMalformedSvg() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                                              img = ImageRead( "%smalformed.svg" );
		                                                                                              """
		    .formatted( SVG ), context ) );

		assertThat( e.getMessage() ).contains( "Unable to load image" );
		assertThat( e.getCause() ).isInstanceOf( BoxRuntimeException.class );
		assertThat( e.getCause().getMessage() ).contains( "not a well-formed SVG document" );
	}

	@DisplayName( "It should reject a non-positive SVG target size" )
	@Test
	public void testInvalidSvgSize() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                                              img = ImageRead( path = "%slogo.svg", width = 0 );
		                                                                                              """
		    .formatted( SVG ), context ) );

		assertThat( e.getCause().getMessage() ).contains( "greater than zero" );
	}

	@DisplayName( "It should include svg in the readable image formats" )
	@Test
	public void testGetReadableFormatsIncludesSvg() {
		// @formatter:off
		runtime.executeSource( """
			result = GetReadableImageFormats();
		""", context );
		// @formatter:on

		Array result = ( Array ) variables.get( Key.of( "result" ) );
		assertThat( result.stream().map( Object::toString ).toList() ).contains( "svg" );
	}

	@DisplayName( "It should detect SVG by extension and by content" )
	@Test
	public void testDetection() {
		assertThat( SvgUtil.hasSvgExtension( "/a/b/Logo.SVG" ) ).isTrue();
		assertThat( SvgUtil.hasSvgExtension( "https://example.com/logo.svg?v=2" ) ).isTrue();
		assertThat( SvgUtil.hasSvgExtension( "/a/b/logo.png" ) ).isFalse();
		assertThat( SvgUtil.hasSvgContent( "<svg xmlns='http://www.w3.org/2000/svg'/>".getBytes() ) ).isTrue();
		assertThat( SvgUtil.hasSvgContent( "﻿<?xml version='1.0'?>\n<!-- c -->\n<!DOCTYPE svg>\n<svg>".getBytes() ) ).isTrue();
		assertThat( SvgUtil.hasSvgContent( "<svgfoo/>".getBytes() ) ).isFalse();
		assertThat( SvgUtil.hasSvgContent( "<html><svg/></html>".getBytes() ) ).isFalse();
		assertThat( SvgUtil.hasSvgContent( new byte[] { ( byte ) 0x89, 'P', 'N', 'G' } ) ).isFalse();
		assertThat( SvgUtil.hasSvgContent( "<?pi a?><?pi b?>\n<svg/>".getBytes() ) ).isTrue();
		assertThat( SvgUtil.hasSvgContent( "<!-- a --><!-- b --><SVG width='1'>".getBytes() ) ).isTrue();
		assertThat( SvgUtil.hasSvgContent( "<!-- never closed <svg>".getBytes() ) ).isFalse();
		assertThat( SvgUtil.hasSvgContent( "<?xml version='1.0'".getBytes() ) ).isFalse();
		// Crafted heads that made the old regular expressions backtrack are rejected in a single scan
		assertThat( SvgUtil.hasSvgContent( ( "<?" + "?><?".repeat( 1000 ) + "x" ).getBytes() ) ).isFalse();
		assertThat( SvgUtil.hasSvgContent( ( "<!--" + "--><!--".repeat( 500 ) + "x" ).getBytes() ) ).isFalse();
		assertThat( SvgUtil.hasSvgContent( ( "<svg width=\"" + " ".repeat( 4000 ) ).getBytes() ) ).isTrue();
	}

	@DisplayName( "It should not take stroke-width as the SVG width when sizing" )
	@Test
	public void testStrokeWidthIsNotWidth() {
		BufferedImage image = SvgUtil.render(
		    "<svg xmlns='http://www.w3.org/2000/svg' stroke-width='3' stroke-height='4'><rect width='5' height='5'/></svg>".getBytes(),
		    null, null, "inline" );
		assertThat( image.getWidth() ).isEqualTo( SvgUtil.DEFAULT_SIZE );
		assertThat( image.getHeight() ).isEqualTo( SvgUtil.DEFAULT_SIZE );
	}

}
