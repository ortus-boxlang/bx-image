package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BaseIntegrationTest;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.context.ScriptingRequestBoxContext;
import ortus.boxlang.runtime.scopes.IScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.scopes.VariablesScope;

public class ImageSetDrawingColorTest extends BaseIntegrationTest {
	// TODO tests for color by name green/white/black/red etc
	// TODO tests for rgb values

	@DisplayName( "It should default to white" )
	@Test
	public void testDefaultDrawingColor() {
		// @formatter:off
		runtime.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" ).getDrawingColor();
		""", context );
		// @formatter:on

		assertThat( variables.get( result ) ).isEqualTo( "white" );
	}

	@DisplayName( "It should let you set the drawing color" )
	@Test
	public void testSetDrawingColor() {
		// @formatter:off
		runtime.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			ImageSetDrawingColor( result, "green" );
			result = result.getDrawingColor();
		""", context );
		// @formatter:on

		assertThat( variables.get( result ) ).isEqualTo( "green" );
	}

	@DisplayName( "It should let you set the drawing color of an image by name" )
	@Test
	public void testSetDrawingColorByName() {
		// @formatter:off
		runtime.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			ImageSetDrawingColor( "result", "green" );
			result = result.getDrawingColor();
		""", context );
		// @formatter:on

		assertThat( variables.get( result ) ).isEqualTo( "green" );
	}

	// TODO test member functionality
	@DisplayName( "It should let you set the drawing color as a member function" )
	@Test
	public void testSetDrawingColorMember() {
		// @formatter:off
		runtime.executeSource( """
			result = ImageRead( "src/test/resources/logo.png" );
			result.setDrawingColor( "green" );
			result = result.getDrawingColor();
		""", context );
		// @formatter:on

		assertThat( variables.get( result ) ).isEqualTo( "green" );
	}

	private int pixelAfterFill( String color ) {
		// @formatter:off
		runtime.executeSource( """
			img = ImageNew( "", 10, 10, "argb", "black" );
			img.setDrawingColor( "%s" );
			img.drawRect( 0, 0, 10, 10, true );
			result = img.getBufferedImage().getRGB( 5, 5 );
		""".formatted( color.replace( "#", "##" ) ), context );
		// @formatter:on
		return ( ( Number ) variables.get( result ) ).intValue();
	}

	@DisplayName( "It parses #RRGGBB" )
	@Test
	public void testHexSixDigits() {
		assertThat( pixelAfterFill( "#FF00FF" ) ).isEqualTo( 0xFFFF00FF );
	}

	@DisplayName( "It parses hex without the leading hash" )
	@Test
	public void testHexNoHash() {
		assertThat( pixelAfterFill( "0b161b" ) ).isEqualTo( 0xFF0b161b );
	}

	@DisplayName( "It parses #RGB" )
	@Test
	public void testHexThreeDigits() {
		assertThat( pixelAfterFill( "#f80" ) ).isEqualTo( 0xFFFF8800 );
	}

	@DisplayName( "It parses #RRGGBBAA with alpha last" )
	@Test
	public void testHexEightDigits() {
		// 0x80 alpha over opaque black background
		int pixel = pixelAfterFill( "#FF000080" );
		assertThat( ( pixel >>> 24 ) ).isEqualTo( 0xFF );
		assertThat( ( pixel >> 16 ) & 0xFF ).isAtLeast( 0x7F );
		assertThat( ( pixel >> 16 ) & 0xFF ).isAtMost( 0x81 );
	}

	@DisplayName( "It throws on an unparseable color instead of drawing black" )
	@Test
	public void testInvalidColor() {
		for ( String bad : new String[] { "#GGGGGG", "#12345", "notacolor" } ) {
			assertThrows( BoxRuntimeException.class, () -> pixelAfterFill( bad ) );
		}
	}

	@DisplayName( "It keeps the previous color when the new one is invalid" )
	@Test
	public void testInvalidKeepsPrevious() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageNew( "", 10, 10, "rgb", "black" );
			img.setDrawingColor( "red" );
			try { img.setDrawingColor( "bogus" ); } catch( any e ) {}
			result = img.getDrawingColor();
		""", context );
		// @formatter:on
		assertThat( variables.get( result ) ).isEqualTo( "red" );
	}

	@DisplayName( "It parses hex for setBackgroundColor, ImageNew and addBorder" )
	@Test
	public void testHexElsewhere() {
		// @formatter:off
		runtime.executeSource( """
			img = ImageNew( "", 10, 10, "rgb", "##0b161b" );
			bg = img.getBufferedImage().getRGB( 5, 5 );
			img.setBackgroundColor( "##102030" );
			img.clearRect( 0, 0, 10, 10 );
			cleared = img.getBufferedImage().getRGB( 5, 5 );
			img.addBorder( 2, "##ABCDEF" );
			border = img.getBufferedImage().getRGB( 0, 0 );
			result = [ bg, cleared, border ];
		""", context );
		// @formatter:on
		var pixels = ( ortus.boxlang.runtime.types.Array ) variables.get( result );
		assertThat( ( ( Number ) pixels.get( 0 ) ).intValue() ).isEqualTo( 0xFF0b161b );
		assertThat( ( ( Number ) pixels.get( 1 ) ).intValue() ).isEqualTo( 0xFF102030 );
		assertThat( ( ( Number ) pixels.get( 2 ) ).intValue() ).isEqualTo( 0xFFABCDEF );
	}

	@DisplayName( "It throws for an invalid ImageNew color" )
	@Test
	public void testImageNewInvalid() {
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( """
		                                                                      	ImageNew( "", 10, 10, "rgb", "##zzz" );
		                                                                      """, context ) );
	}

}
