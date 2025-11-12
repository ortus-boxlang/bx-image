package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageGetExifTagTest extends BaseIntegrationTest {

	@DisplayName( "It should read the exif data from a path and return the tag value" )
	@Test
	public void testReadFromPath() throws IOException {
		runtime.executeSource(
		    """
		    result = ImageGetExifTag( "src/test/resources/test-images/exif-test.jpg", "software" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isEqualTo( "GIMP 2.10.34" );
	}

	@DisplayName( "It should read the exif data from an image and return the tag value" )
	@Test
	public void testReadFromImage() throws IOException {
		runtime.executeSource(
		    """
		    result = ImageGetExifTag( imageRead( "src/test/resources/test-images/exif-test.jpg" ), "software" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isEqualTo( "GIMP 2.10.34" );
	}

	@DisplayName( "It be callable as a member function" )
	@Test
	public void testMemberInvocation() throws IOException {
		runtime.executeSource(
		    """
		    result = imageRead( "src/test/resources/test-images/exif-test.jpg" ).GetExifTag( "software" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isEqualTo( "GIMP 2.10.34" );
	}

}
