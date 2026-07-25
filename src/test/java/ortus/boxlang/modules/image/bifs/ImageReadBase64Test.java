package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BaseIntegrationTest;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ImageReadBase64Test extends BaseIntegrationTest {

	@DisplayName( "It should return an image from a raw base64 string" )
	@Test
	public void testReadFromBase64() throws IOException {
		runtime.executeSource(
		    """
		    result = ImageReadBase64( "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR4nGP4z8AAAAMBAQDJ/pLvAAAAAElFTkSuQmCC" );
		    result = result.$bx.$class.name
		    """,
		    context );

		assertThat( variables.get( result ) ).isEqualTo( "ortus.boxlang.modules.image.BoxImage" );
	}

	@DisplayName( "It should return an image from a data:image/png;base64 URI" )
	@Test
	public void testReadFromBase64PngDataUri() throws IOException {
		runtime.executeSource(
		    """
		    result = ImageReadBase64( "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR4nGP4z8AAAAMBAQDJ/pLvAAAAAElFTkSuQmCC" );
		    result = result.$bx.$class.name
		    """,
		    context );

		assertThat( variables.get( result ) ).isEqualTo( "ortus.boxlang.modules.image.BoxImage" );
	}

	@DisplayName( "It should return an image from a data:image/gif;base64 URI" )
	@Test
	public void testReadFromBase64GifDataUri() throws IOException {
		runtime.executeSource(
		    """
		    result = ImageReadBase64( "data:image/gif;base64,R0lGODlhAQABAPAAAP///wAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" );
		    result = result.$bx.$class.name
		    """,
		    context );

		assertThat( variables.get( result ) ).isEqualTo( "ortus.boxlang.modules.image.BoxImage" );
	}

	@DisplayName( "It should return an image from a data:image/jpeg;base64 URI" )
	@Test
	public void testReadFromBase64JpegDataUri() throws IOException {
		runtime.executeSource(
		    """
		    result = ImageReadBase64( "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDi6KKK+ZP3E//Z" );
		    result = result.$bx.$class.name
		    """,
		    context );

		assertThat( variables.get( result ) ).isEqualTo( "ortus.boxlang.modules.image.BoxImage" );
	}
}
