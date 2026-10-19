package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.types.Array;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class ImageSetDrawingGradientTest extends BaseIntegrationTest {

	private static final String SETUP = """
	                                    img = ImageNew( "", 100, 100, "argb", "black" );
	                                    img.setDrawingGradient( 0, 0, 100, 0, [ "red", "blue" ] );
	                                    """;

	/** Runs the setup plus the given drawing code and returns the pixels requested as [ x, y ] pairs. */
	private int[] pixels( String setup, String draw, int[][] points ) {
		StringBuilder reads = new StringBuilder( "result = [" );
		for ( int i = 0; i < points.length; i++ ) {
			reads.append( i > 0 ? "," : "" ).append( "img.getBufferedImage().getRGB(" + points[ i ][ 0 ] + "," + points[ i ][ 1 ] + ")" );
		}
		reads.append( "];" );
		runtime.executeSource( setup + draw + reads, context );
		Array	values	= ( Array ) variables.get( result );
		int[]	out		= new int[ points.length ];
		for ( int i = 0; i < out.length; i++ ) {
			out[ i ] = ( ( Number ) values.get( i ) ).intValue();
		}
		return out;
	}

	private static int red( int argb ) {
		return ( argb >> 16 ) & 0xFF;
	}

	private static int blue( int argb ) {
		return argb & 0xFF;
	}

	@DisplayName( "A filled rect is painted with the gradient" )
	@Test
	public void testFillRect() {
		int[] p = pixels( SETUP, "img.drawRect( 0, 0, 100, 100, true );", new int[][] { { 2, 50 }, { 97, 50 } } );
		assertThat( red( p[ 0 ] ) ).isGreaterThan( 230 );
		assertThat( blue( p[ 0 ] ) ).isLessThan( 25 );
		assertThat( blue( p[ 1 ] ) ).isGreaterThan( 230 );
		assertThat( red( p[ 1 ] ) ).isLessThan( 25 );
	}

	@DisplayName( "fillRect, filled oval, round rect and arc use the gradient" )
	@Test
	public void testOtherFills() {
		int[] p = pixels( SETUP,
		    "img.drawOval( 0, 0, 100, 100, true ); img.drawRoundRect( 0, 0, 100, 100, 10, 10, true ); img.drawArc( 0, 0, 100, 100, 0, 360, true );",
		    new int[][] { { 20, 50 }, { 80, 50 } } );
		assertThat( red( p[ 0 ] ) ).isGreaterThan( blue( p[ 0 ] ) );
		assertThat( blue( p[ 1 ] ) ).isGreaterThan( red( p[ 1 ] ) );
	}

	@DisplayName( "Strokes (a ring) use the gradient" )
	@Test
	public void testStrokeRing() {
		int[] p = pixels( SETUP,
		    "img.setDrawingStroke( { width: 8 } ); img.drawOval( 10, 10, 80, 80, false );",
		    new int[][] { { 10, 50 }, { 90, 50 }, { 50, 50 } } );
		assertThat( red( p[ 0 ] ) ).isGreaterThan( blue( p[ 0 ] ) );
		assertThat( blue( p[ 1 ] ) ).isGreaterThan( red( p[ 1 ] ) );
		// centre of the ring is untouched
		assertThat( p[ 2 ] ).isEqualTo( 0xFF000000 );
	}

	@DisplayName( "Text uses the gradient" )
	@Test
	public void testText() {
		runtime.executeSource( SETUP + """
		                       img.drawText( "MMMMMMMM", 0, 80, { size: 60, style: "bold" } );
		                       bi = img.getBufferedImage();
		                       leftRed = 0; rightBlue = 0;
		                       for ( x = 0; x < 100; x++ ) {
		                           for ( y = 0; y < 100; y++ ) {
		                               px = bi.getRGB( x, y );
		                               if ( px != -16777216 ) {
		                                   r = bitAnd( bitSHRN( px, 16 ), 255 ); b = bitAnd( px, 255 );
		                                   if ( x < 30 && r > b ) leftRed++;
		                                   if ( x > 70 && b > r ) rightBlue++;
		                               }
		                           }
		                       }
		                       result = [ leftRed, rightBlue ];
		                       """, context );
		Array values = ( Array ) variables.get( result );
		assertThat( ( ( Number ) values.get( 0 ) ).intValue() ).isGreaterThan( 0 );
		assertThat( ( ( Number ) values.get( 1 ) ).intValue() ).isGreaterThan( 0 );
	}

	@DisplayName( "Fractions position the color stops" )
	@Test
	public void testFractions() {
		int[] p = pixels( """
		                  img = ImageNew( "", 100, 10, "argb", "black" );
		                  img.setDrawingGradient( 0, 0, 100, 0, [ "red", "green", "blue" ], [ 0, 0.2, 1 ] );
		                  """, "img.drawRect( 0, 0, 100, 10, true );", new int[][] { { 20, 5 } } );
		// at the 0.2 stop the pixel is (almost) pure green
		assertThat( ( p[ 0 ] >> 8 ) & 0xFF ).isGreaterThan( 230 );
	}

	@DisplayName( "A vertical gradient with alpha colors works" )
	@Test
	public void testVerticalAlpha() {
		int[] p = pixels( """
		                  img = ImageNew( "", 10, 100, "argb", "black" );
		                  img.setDrawingGradient( 0, 0, 0, 100, [ "red", "red" ] );
		                  """, "img.drawRect( 0, 0, 10, 100, true );", new int[][] { { 5, 5 }, { 5, 95 } } );
		assertThat( red( p[ 0 ] ) ).isGreaterThan( 230 );
		assertThat( red( p[ 1 ] ) ).isGreaterThan( 230 );
	}

	@DisplayName( "Gradient colors accept hex including #RRGGBBAA" )
	@Test
	public void testHexColors() {
		int[] p = pixels( """
		                  img = ImageNew( "", 100, 10, "argb", "black" );
		                  img.setDrawingGradient( 0, 0, 100, 0, [ "##ff0000ff", "##00f" ] );
		                  """, "img.drawRect( 0, 0, 100, 10, true );", new int[][] { { 2, 5 }, { 97, 5 } } );
		assertThat( red( p[ 0 ] ) ).isGreaterThan( 230 );
		assertThat( blue( p[ 1 ] ) ).isGreaterThan( 230 );
	}

	@DisplayName( "setDrawingColor goes back to a solid color" )
	@Test
	public void testReset() {
		int[] p = pixels( SETUP + "img.setDrawingColor( \"green\" );", "img.drawRect( 0, 0, 100, 100, true );",
		    new int[][] { { 2, 50 }, { 97, 50 } } );
		assertThat( p[ 0 ] ).isEqualTo( 0xFF00FF00 );
		assertThat( p[ 1 ] ).isEqualTo( 0xFF00FF00 );
	}

	@DisplayName( "The gradient survives operations that rebuild the graphics context" )
	@Test
	public void testSurvivesResize() {
		int[] p = pixels( SETUP + "img.resize( 100, 100 );", "img.drawRect( 0, 0, 100, 100, true );",
		    new int[][] { { 2, 50 }, { 97, 50 } } );
		assertThat( red( p[ 0 ] ) ).isGreaterThan( blue( p[ 0 ] ) );
		assertThat( blue( p[ 1 ] ) ).isGreaterThan( red( p[ 1 ] ) );
	}

	@DisplayName( "It works as a BIF by variable name" )
	@Test
	public void testBifByName() {
		runtime.executeSource( """
		                       img = ImageNew( "", 100, 10, "argb", "black" );
		                       ImageSetDrawingGradient( "img", 0, 0, 100, 0, [ "red", "blue" ] );
		                       ImageDrawRect( img, 0, 0, 100, 10, true );
		                       result = img.getBufferedImage().getRGB( 97, 5 );
		                       """, context );
		assertThat( blue( ( ( Number ) variables.get( result ) ).intValue() ) ).isGreaterThan( 230 );
	}

	@DisplayName( "It rejects invalid input" )
	@Test
	public void testInvalid() {
		String[] bad = {
		    "img.setDrawingGradient( 0, 0, 10, 0, [ \"red\" ] )",
		    "img.setDrawingGradient( 0, 0, 10, 0, [] )",
		    "img.setDrawingGradient( 5, 5, 5, 5, [ \"red\", \"blue\" ] )",
		    "img.setDrawingGradient( 0, 0, 10, 0, [ \"red\", \"nope\" ] )",
		    "img.setDrawingGradient( 0, 0, 10, 0, [ \"red\", \"blue\" ], [ 0 ] )",
		    "img.setDrawingGradient( 0, 0, 10, 0, [ \"red\", \"blue\" ], [ 0.5, 0.5 ] )",
		    "img.setDrawingGradient( 0, 0, 10, 0, [ \"red\", \"blue\" ], [ 0, 2 ] )"
		};
		for ( String code : bad ) {
			assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "img = ImageNew( \"\", 10, 10, \"rgb\", \"black\" ); " + code + ";", context ), code );
		}
	}
}
