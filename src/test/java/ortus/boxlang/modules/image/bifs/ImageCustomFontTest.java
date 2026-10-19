package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.types.Array;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class ImageCustomFontTest extends BaseIntegrationTest {

	private static final String	FONT	= "src/test/resources/fonts/Caladea-Regular.ttf";

	/** Draws "MMMM" and returns the ink bounds as [ minX, maxX, minY, maxY, inkPixels ]. */
	private int[] ink( String attrs ) {
		runtime.executeSource( """
		                       img = ImageNew( "", 400, 150, "rgb", "white" );
		                       img.setDrawingColor( "black" );
		                       img.drawText( "MMMM", 10, 100, %s );
		                       bi = img.getBufferedImage();
		                       minX = 9999; maxX = -1; minY = 9999; maxY = -1; count = 0;
		                       for ( x = 0; x < 400; x++ ) {
		                           for ( y = 0; y < 150; y++ ) {
		                               if ( bi.getRGB( x, y ) != -1 ) {
		                                   minX = min( minX, x ); maxX = max( maxX, x ); minY = min( minY, y ); maxY = max( maxY, y ); count++;
		                               }
		                           }
		                       }
		                       result = [ minX, maxX, minY, maxY, count ];
		                       """.formatted( attrs ), context );
		Array	values	= ( Array ) variables.get( result );
		int[]	out		= new int[ 5 ];
		for ( int i = 0; i < 5; i++ ) {
			out[ i ] = ( ( Number ) values.get( i ) ).intValue();
		}
		return out;
	}

	@DisplayName( "drawText renders with the font file" )
	@Test
	public void testDrawWithFile() {
		int[]	custom	= ink( "{ file: '" + FONT + "', size: 60 }" );
		int[]	sans	= ink( "{ font: 'sansserif', size: 60 }" );
		assertThat( custom[ 4 ] ).isGreaterThan( 0 );
		// Caladea is a serif face: different glyphs and widths than the default sans-serif
		assertThat( custom[ 4 ] ).isNotEqualTo( sans[ 4 ] );
		// the baseline at y=100 puts the capitals above it
		assertThat( custom[ 3 ] ).isAtMost( 101 );
	}

	@DisplayName( "Size and style from the struct apply to the font file" )
	@Test
	public void testSizeAndStyle() {
		int[]	small	= ink( "{ file: '" + FONT + "', size: 30 }" );
		int[]	big		= ink( "{ file: '" + FONT + "', size: 60 }" );
		int[]	bold	= ink( "{ file: '" + FONT + "', size: 60, style: 'bold' }" );
		assertThat( big[ 1 ] - big[ 0 ] ).isGreaterThan( ( small[ 1 ] - small[ 0 ] ) * 3 / 2 );
		assertThat( bold[ 4 ] ).isGreaterThan( big[ 4 ] );
	}

	@DisplayName( "A file without a size uses 12 points, not 1" )
	@Test
	public void testDefaultSize() {
		int[] noSize = ink( "{ file: '" + FONT + "' }" );
		assertThat( noSize[ 3 ] - noSize[ 2 ] ).isGreaterThan( 4 );
	}

	@DisplayName( "ImageRegisterFont returns the family and makes it usable by name" )
	@Test
	public void testRegister() {
		runtime.executeSource( "result = ImageRegisterFont( '" + FONT + "' );", context );
		assertThat( variables.get( result ) ).isEqualTo( "Caladea" );

		int[]	byName	= ink( "{ font: 'Caladea', size: 60 }" );
		int[]	byFile	= ink( "{ file: '" + FONT + "', size: 60 }" );
		assertThat( byName[ 4 ] ).isEqualTo( byFile[ 4 ] );
	}

	@DisplayName( "Registering twice is harmless" )
	@Test
	public void testRegisterTwice() {
		runtime.executeSource( "ImageRegisterFont( '" + FONT + "' ); result = ImageRegisterFont( '" + FONT + "' );", context );
		assertThat( variables.get( result ) ).isEqualTo( "Caladea" );
	}

	@DisplayName( "A missing font file throws a clear error" )
	@Test
	public void testMissing() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class, () -> ink( "{ file: 'src/test/resources/fonts/nope.ttf' }" ) );
		assertThat( e.getMessage() ).contains( "does not exist" );
		e = assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "ImageRegisterFont( 'nope.ttf' );", context ) );
		assertThat( e.getMessage() ).contains( "does not exist" );
	}

	@DisplayName( "An invalid font file throws a clear error" )
	@Test
	public void testInvalid() {
		BoxRuntimeException e = assertThrows( BoxRuntimeException.class, () -> ink( "{ file: 'src/test/resources/fonts/not-a-font.ttf' }" ) );
		assertThat( e.getMessage() ).contains( "not a valid TrueType or OpenType font" );
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "ImageRegisterFont( 'src/test/resources/fonts/not-a-font.ttf' );", context ) );
	}

	@DisplayName( "An empty file path or a directory throws" )
	@Test
	public void testEmptyAndDirectory() {
		assertThrows( BoxRuntimeException.class, () -> ink( "{ file: ' ' }" ) );
		assertThrows( BoxRuntimeException.class, () -> ink( "{ file: 'src/test/resources/fonts' }" ) );
		assertThrows( Exception.class, () -> runtime.executeSource( "ImageRegisterFont( '' );", context ) );
	}

	@DisplayName( "measureText uses the same font file resolution as drawText" )
	@Test
	public void testMeasureWithFile() {
		runtime.executeSource( """
		                       img = ImageNew( "", 600, 120, "rgb", "white" );
		                       attrs = { file: "%s", size: 48 };
		                       img.setDrawingColor( "black" );
		                       img.drawText( "MMMM", 20, 80, attrs );
		                       bi = img.getBufferedImage();
		                       maxX = -1;
		                       for ( x = 0; x < 600; x++ ) {
		                           for ( y = 0; y < 120; y++ ) { if ( bi.getRGB( x, y ) != -1 ) maxX = max( maxX, x ); }
		                       }
		                       result = [ maxX, img.measureText( "MMMM", attrs ).width, img.measureText( "MMMM", { font: "sansserif", size: 48 } ).width ];
		                       """.formatted( FONT ), context );
		Array values = ( Array ) variables.get( result );
		double	maxX	= ( ( Number ) values.get( 0 ) ).doubleValue();
		double	custom	= ( ( Number ) values.get( 1 ) ).doubleValue();
		double	sans	= ( ( Number ) values.get( 2 ) ).doubleValue();
		assertThat( maxX ).isAtMost( 20 + custom + 1 );
		assertThat( maxX ).isAtLeast( 20 + custom * 0.8 );
		assertThat( custom ).isNotEqualTo( sans );
		assertThrows( BoxRuntimeException.class, () -> runtime.executeSource( "ImageNew('',10,10,'rgb','white').measureText('x',{file:'nope.ttf'});", context ) );
	}
}
