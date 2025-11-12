package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;

public class ImageLoadAndGetBlobTest extends BaseIntegrationTest {

	@Test
	@DisplayName( "Can load image and immediately get blob without calling info()" )
	public void testLoadAndImmediateGetBlob() {
		runtime.executeSource(
		    """
		    img = ImageNew("src/test/resources/logo.png");
		    result = ImageGetBlob(img);
		    """,
		    context
		);

		byte[] blob = ( byte[] ) variables.get( result );
		assertThat( blob ).isNotNull();
		assertThat( blob.length ).isGreaterThan( 0 );
	}

	@Test
	@DisplayName( "Can load image via member function and immediately get blob" )
	public void testLoadAndImmediateGetBlobMember() {
		runtime.executeSource(
		    """
		    result = ImageNew("src/test/resources/logo.png").getBlob();
		    """,
		    context
		);

		byte[] blob = ( byte[] ) variables.get( result );
		assertThat( blob ).isNotNull();
		assertThat( blob.length ).isGreaterThan( 0 );
	}

	@Test
	@DisplayName( "Blob size should be consistent regardless of whether info() was called" )
	public void testBlobConsistencyWithAndWithoutInfo() {
		runtime.executeSource(
		    """
		    // Load and get blob immediately
		    img1 = ImageNew("src/test/resources/logo.png");
		    blob1 = ImageGetBlob(img1);
		    		       // Load, call info(), then get blob
		    img2 = ImageNew("src/test/resources/logo.png");
		    info = img2.info();
		    blob2 = ImageGetBlob(img2);
		    		       // Compare sizes
		    size1 = arrayLen(blob1);
		    size2 = arrayLen(blob2);
		    """,
		    context
		);

		int	size1	= ( int ) variables.get( Key.of( "size1" ) );
		int	size2	= ( int ) variables.get( Key.of( "size2" ) );

		assertThat( size1 ).isEqualTo( size2 );
		assertThat( size1 ).isGreaterThan( 0 );
	}

}
