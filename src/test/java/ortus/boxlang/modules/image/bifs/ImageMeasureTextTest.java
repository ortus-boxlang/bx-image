package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class ImageMeasureTextTest extends BaseIntegrationTest {

	private double num( IStruct s, String key ) {
		return ( ( Number ) s.get( Key.of( key ) ) ).doubleValue();
	}

	private IStruct measure( String call ) {
		runtime.executeSource( "img = ImageNew( '', 400, 200, 'rgb', 'white' ); result = " + call + ";", context );
		return ( IStruct ) variables.get( result );
	}

	@DisplayName( "It returns width, height, ascent, descent and leading" )
	@Test
	public void testShape() {
		IStruct m = measure( "img.measureText( 'Hello', { font: 'sansserif', size: 24 } )" );
		assertThat( m.keySet().stream().map( Key::getName ).toList() )
		    .containsAtLeast( "width", "height", "ascent", "descent", "leading" );
		assertThat( num( m, "width" ) ).isGreaterThan( 0 );
		assertThat( num( m, "ascent" ) ).isGreaterThan( 0 );
		assertThat( num( m, "descent" ) ).isGreaterThan( 0 );
		assertThat( num( m, "height" ) ).isWithin( 0.001 ).of( num( m, "ascent" ) + num( m, "descent" ) );
	}

	@DisplayName( "Width grows with size and with text length" )
	@Test
	public void testScaling() {
		double	small	= num( measure( "img.measureText( 'Hello', { size: 20 } )" ), "width" );
		double	big		= num( measure( "img.measureText( 'Hello', { size: 40 } )" ), "width" );
		double	longer	= num( measure( "img.measureText( 'Hello Hello', { size: 20 } )" ), "width" );
		assertThat( big ).isWithin( small * 0.1 ).of( small * 2 );
		assertThat( longer ).isGreaterThan( small );
	}

	@DisplayName( "It works as a BIF, by variable name, and with no attributes" )
	@Test
	public void testBifForms() {
		runtime.executeSource( """
		                       img = ImageNew( "", 100, 100, "rgb", "white" );
		                       a = ImageMeasureText( img, "Hi", { size: 30 } );
		                       b = ImageMeasureText( "img", "Hi", { size: 30 } );
		                       c = img.measureText( "Hi" );
		                       result = [ a.width, b.width, c.width ];
		                       """, context );
		var values = ( ortus.boxlang.runtime.types.Array ) variables.get( result );
		assertThat( ( ( Number ) values.get( 0 ) ).doubleValue() ).isEqualTo( ( ( Number ) values.get( 1 ) ).doubleValue() );
		assertThat( ( ( Number ) values.get( 2 ) ).doubleValue() ).isGreaterThan( 0 );
	}

	@DisplayName( "An empty string has zero width" )
	@Test
	public void testEmpty() {
		assertThat( num( measure( "img.measureText( '', { size: 20 } )" ), "width" ) ).isEqualTo( 0.0 );
	}

	@DisplayName( "The measured width matches the ink that drawText renders" )
	@Test
	public void testMatchesDrawText() {
		runtime.executeSource( """
		                       img = ImageNew( "", 600, 120, "rgb", "white" );
		                       attrs = { font: "sansserif", size: 48, style: "bold" };
		                       img.setDrawingColor( "black" );
		                       img.drawText( "MMMM", 20, 80, attrs );
		                       bi = img.getBufferedImage();
		                       minX = 9999; maxX = -1;
		                       for ( x = 0; x < 600; x++ ) {
		                           for ( y = 0; y < 120; y++ ) {
		                               if ( bi.getRGB( x, y ) != -1 ) { minX = min( minX, x ); maxX = max( maxX, x ); }
		                           }
		                       }
		                       result = { minX: minX, maxX: maxX, width: img.measureText( "MMMM", attrs ).width };
		                       """, context );
		IStruct	r		= ( IStruct ) variables.get( result );
		double	ink		= num( r, "maxX" ) - num( r, "minX" ) + 1;
		double	width	= num( r, "width" );
		assertThat( num( r, "minX" ) ).isAtLeast( 20.0 );
		assertThat( num( r, "maxX" ) ).isAtMost( 20 + width + 1 );
		assertThat( ink ).isGreaterThan( width * 0.8 );
	}

	@DisplayName( "It rejects an invalid style like drawText does" )
	@Test
	public void testInvalidStyle() {
		assertThrows( BoxRuntimeException.class, () -> measure( "img.measureText( 'Hi', { style: 'wobbly' } )" ) );
	}

	@DisplayName( "It rejects a missing string" )
	@Test
	public void testMissingString() {
		assertThrows( Exception.class, () -> measure( "ImageMeasureText( img )" ) );
	}

	@DisplayName( "The documented wrap-and-shrink recipe works" )
	@Test
	public void testWrapRecipe() {
		runtime.executeSource( """
		                       img = ImageNew( "", 1200, 630, "rgb", "black" );
		                       function wrapLines( img, text, attrs, maxWidth ) {
		                           var lines = [];
		                           var line  = "";
		                           for ( var word in text.listToArray( " " ) ) {
		                               var candidate = line.len() ? line & " " & word : word;
		                               if ( line.len() && img.measureText( candidate, attrs ).width > maxWidth ) {
		                                   lines.append( line );
		                                   line = word;
		                               } else {
		                                   line = candidate;
		                               }
		                           }
		                           if ( line.len() ) lines.append( line );
		                           return lines;
		                       }
		                       title = "A fairly long blog post title that must wrap onto several lines to fit the card";
		                       attrs = { font: "SansSerif", size: 72, style: "bold" };
		                       lines = wrapLines( img, title, attrs, 1000 );
		                       while ( lines.len() > 3 && attrs.size > 24 ) {
		                           attrs.size -= 4;
		                           lines = wrapLines( img, title, attrs, 1000 );
		                       }
		                       widest = 0;
		                       for ( l in lines ) { widest = max( widest, img.measureText( l, attrs ).width ); }
		                       result = { count: lines.len(), widest: widest, joined: lines.toList( " " ) };
		                       """, context );
		IStruct r = ( IStruct ) variables.get( result );
		assertThat( num( r, "count" ) ).isAtMost( 3.0 );
		assertThat( num( r, "count" ) ).isAtLeast( 2.0 );
		assertThat( num( r, "widest" ) ).isAtMost( 1000.0 );
		assertThat( r.getAsString( Key.of( "joined" ) ) ).startsWith( "A fairly long" );
	}
}
