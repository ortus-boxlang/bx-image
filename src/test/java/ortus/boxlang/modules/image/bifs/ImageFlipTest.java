package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageFlipTest extends BaseIntegrationTest {

	@DisplayName( "It should flip the image vertically" )
	@Test
	public void testFlipVertical() throws IOException {
		String fileName = "logo-flip-vertical.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageFlip( result, "vertical" );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should flip the image horizontally" )
	@Test
	public void testFlipHorizontal() throws IOException {
		String fileName = "logo-flip-horizontal.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageFlip( result, "horizontal" );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should rotate the image 90deg" )
	@Test
	public void testRotate90() throws IOException {
		String fileName = "logo-flip-rotate-90.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageFlip( result, "90" );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should rotate the image 180deg" )
	@Test
	public void testRotate180() throws IOException {
		String fileName = "logo-flip-rotate-180.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageFlip( result, "180" );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should rotate the image 270deg" )
	@Test
	public void testRotate270() throws IOException {
		String fileName = "logo-flip-rotate-270.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageFlip( result, "270" );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should flip the image anti diagonally" )
	@Test
	public void testFlipAntiDiagonal() throws IOException {
		String fileName = "logo-flip-antidiagonal.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageFlip( result, "antidiagonal" );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should flip the image diagonally" )
	@Test
	public void testFlipDiagonal() throws IOException {
		String fileName = "logo-flip-diagonal.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageFlip( result, "diagonal" );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should be callable as a member function" )
	@Test
	public void testFlipMember() throws IOException {
		String fileName = "logo-flip-diagonal.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       result.flip( "diagonal" );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                       // ImageWrite( result, "src/test/resources/test-images/%s" );
		                                         """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
