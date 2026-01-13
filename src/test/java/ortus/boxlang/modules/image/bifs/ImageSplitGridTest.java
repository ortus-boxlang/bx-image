package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Array;

public class ImageSplitGridTest extends BaseIntegrationTest {

	@DisplayName( "It should create tiles with correct dimensions" )
	@Test
	public void testSplitGridTileDimensions() {
		try {
			runtime.executeSource( """
			                                         theSource = ImageRead( "src/test/resources/logo.png" );
			                                         sourceWidth = ImageGetWidth( theSource );
			                                         sourceHeight = ImageGetHeight( theSource );
			                                         result = ImageSplitGrid( theSource, 3, 2 );
			                       width = result[1][1].getWidth();
			                       height = result[1][1].getHeight();
			                                         """, context );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		int	sourceWidth			= ( int ) variables.get( Key.of( "sourceWidth" ) );
		int	sourceHeight		= ( int ) variables.get( Key.of( "sourceHeight" ) );
		int	expectedTileWidth	= sourceWidth / 3;
		int	expectedTileHeight	= sourceHeight / 2;

		assertThat( ( int ) variables.get( Key.of( "width" ) ) ).isEqualTo( expectedTileWidth );
		assertThat( ( int ) variables.get( Key.of( "height" ) ) ).isEqualTo( expectedTileHeight );
	}

	@DisplayName( "It should be invocable as member function" )
	@Test
	public void testSplitGridMemberFunction() {
		runtime.executeSource( """
		                       theSource = ImageRead( "src/test/resources/logo.png" );
		                       result = theSource.splitGrid( 2, 3 );
		                       """, context );

		assertInstanceOf( Array.class, variables.get( result ) );
		Array tiles = variables.getAsArray( result );

		// Should have 3 rows
		assertThat( tiles.size() ).isEqualTo( 3 );

		// Each row should have 2 columns
		for ( int i = 0; i < 3; i++ ) {
			Array row = ( Array ) tiles.get( i );
			assertThat( row.size() ).isEqualTo( 2 );
		}
	}

	@DisplayName( "It should create correct sub images" )
	@Test
	public void testImagesMatch() {
		runtime.executeSource( """
		                                         theSource = ImageRead( "src/test/resources/logo.png" );
		                                         result = theSource.splitGrid( 2, 3 );
		                       result.each( (row,rowId ) => {
		                                         	row.each( ( cellImage, colId) => {
		                                         		cellImage.write("src/test/resources/generated/grid_#rowId#_#colId#.png" )
		                                         	})
		                                         })
		                                         """, context );

		assertInstanceOf( Array.class, variables.get( result ) );
		Array tiles = variables.getAsArray( result );

		// Should have 3 rows
		assertThat( tiles.size() ).isEqualTo( 3 );

		// Each row should have 2 columns
		for ( int i = 0; i < tiles.size(); i++ ) {
			Array row = ( Array ) tiles.get( i );
			for ( int j = 0; j < row.size(); j++ ) {
				try {
					var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/grid_%d_%d.png".formatted( i + 1, j + 1 ) ) );
					var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/grid_%d_%d.png".formatted( i + 1, j + 1 ) ) );
					assertThat( Arrays.equals( actual, expected ) ).isTrue();
				} catch ( Exception e ) {
					throw new RuntimeException( "Error reading image files for comparison: " + e.getMessage(), e );
				}

			}
		}
	}

	@DisplayName( "It should split into a single row" )
	@Test
	public void testSplitGridSingleRow() {
		runtime.executeSource( """
		                       theSource = ImageRead( "src/test/resources/logo.png" );
		                       result = ImageSplitGrid( theSource, 4, 1 );
		                       """, context );

		Array tiles = variables.getAsArray( result );

		// Should have 1 row with 4 columns
		assertThat( tiles.size() ).isEqualTo( 1 );
		Array firstRow = ( Array ) tiles.get( 0 );
		assertThat( firstRow.size() ).isEqualTo( 4 );
	}

	@DisplayName( "It should split into a single column" )
	@Test
	public void testSplitGridSingleColumn() {
		runtime.executeSource( """
		                       theSource = ImageRead( "src/test/resources/logo.png" );
		                       result = ImageSplitGrid( theSource, 1, 4 );
		                       """, context );

		Array tiles = variables.getAsArray( result );

		// Should have 4 rows with 1 column each
		assertThat( tiles.size() ).isEqualTo( 4 );
		for ( int i = 0; i < 4; i++ ) {
			Array row = ( Array ) tiles.get( i );
			assertThat( row.size() ).isEqualTo( 1 );
		}
	}

}
