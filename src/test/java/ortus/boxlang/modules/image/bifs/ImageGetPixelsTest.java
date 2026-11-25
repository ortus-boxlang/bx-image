package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.types.Array;

public class ImageGetPixelsTest extends BaseIntegrationTest {

	@DisplayName( "It returns rows of luma values by default" )
	@Test
	public void testLuma() {
		runtime.executeSource( """
		                       img = imageRead( "src/test/resources/native/quadrants.png" );
		                       result = imageGetPixels( img );
		                       """, context );

		Array rows = variables.getAsArray( result );
		assertThat( rows.size() ).isEqualTo( 8 );
		assertThat( ( ( Array ) rows.get( 0 ) ).size() ).isEqualTo( 8 );
		assertThat( ( ( Array ) rows.get( 0 ) ).get( 0 ) ).isEqualTo( 77 );
		assertThat( ( ( Array ) rows.get( 0 ) ).get( 7 ) ).isEqualTo( 149 );
		assertThat( ( ( Array ) rows.get( 7 ) ).get( 0 ) ).isEqualTo( 29 );
		assertThat( ( ( Array ) rows.get( 7 ) ).get( 7 ) ).isEqualTo( 255 );
	}

	@DisplayName( "It returns [r,g,b,a] arrays in rgba mode, via the member function" )
	@Test
	public void testRgbaMember() {
		runtime.executeSource( """
		                       img = imageRead( "src/test/resources/native/quadrants.png" );
		                       result = img.getPixels( "rgba" )[ 1 ][ 8 ];
		                       """, context );

		assertThat( variables.getAsArray( result ).toArray() ).asList().containsExactly( 0, 255, 0, 255 ).inOrder();
	}

	@DisplayName( "Grayscale images report their stored gray level" )
	@Test
	public void testGrayscale() {
		runtime.executeSource( """
		                       img = imageRead( "src/test/resources/native/quadrants.png" ).grayScale();
		                       result = [ img.getPixels( "rgba" )[ 1 ][ 1 ], img.getPixels()[ 1 ][ 8 ] ];
		                       """, context );

		Array values = variables.getAsArray( result );
		assertThat( ( ( Array ) values.get( 0 ) ).toArray() ).asList().containsExactly( 77, 77, 77, 255 ).inOrder();
		assertThat( values.get( 1 ) ).isEqualTo( 149 );
	}

	@DisplayName( "An unknown mode fails" )
	@Test
	public void testUnknownMode() {
		assertThrows( Exception.class, () -> runtime.executeSource( """
		                                                            img = imageNew( "", 2, 2, "rgb", "black" );
		                                                            imageGetPixels( img, "cmyk" );
		                                                            """, context ) );
	}

}
