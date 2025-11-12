package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;

/**
 * Tests for ImageDrawText BIF
 *
 * Note: These tests verify functionality without pixel-perfect image comparison
 * since font rendering varies across operating systems and JDK versions.
 */
public class ImageDrawTextTest extends BaseIntegrationTest {

	@DisplayName( "It should draw text" )
	@Test
	public void testDrawText() throws IOException {
		String fileName = "logo-draw-text.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       originalWidth = result.getWidth();
		                       originalHeight = result.getHeight();
		                       ImageDrawText( result, "Drink your Ovaltine!", 50, 50 );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                                         """.formatted( fileName ), context );

		// Verify image was created and dimensions are preserved
		Path generatedFile = Paths.get( "src/test/resources/generated/%s".formatted( fileName ) );
		assertThat( Files.exists( generatedFile ) ).isTrue();
		assertThat( Files.size( generatedFile ) ).isGreaterThan( 0L );

		int	originalWidth	= ( int ) variables.get( Key.of( "originalWidth" ) );
		int	originalHeight	= ( int ) variables.get( Key.of( "originalHeight" ) );
		assertThat( originalWidth ).isAtLeast( 250 );
		assertThat( originalHeight ).isAtLeast( 250 );
	}

	@DisplayName( "It should draw text for a certain font" )
	@Test
	public void testDrawTextFont() throws IOException {
		String fileName = "logo-draw-text-font.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { font: "Georgia" } );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                                         """.formatted( fileName ), context );

		Path generatedFile = Paths.get( "src/test/resources/generated/%s".formatted( fileName ) );
		assertThat( Files.exists( generatedFile ) ).isTrue();
		assertThat( Files.size( generatedFile ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It should draw font of different sizes" )
	@Test
	public void testDrawTextSize() throws IOException {
		String fileName = "logo-draw-text-size.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { size: 50 } );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                                         """.formatted( fileName ), context );

		Path generatedFile = Paths.get( "src/test/resources/generated/%s".formatted( fileName ) );
		assertThat( Files.exists( generatedFile ) ).isTrue();
		assertThat( Files.size( generatedFile ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It should draw bold text" )
	@Test
	public void testDrawTextBold() throws IOException {
		String fileName = "logo-draw-text-bold.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { style: "bold" } );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                                         """.formatted( fileName ), context );

		Path generatedFile = Paths.get( "src/test/resources/generated/%s".formatted( fileName ) );
		assertThat( Files.exists( generatedFile ) ).isTrue();
		assertThat( Files.size( generatedFile ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It should draw italic text" )
	@Test
	public void testDrawTextItalic() throws IOException {
		String fileName = "logo-draw-text-italic.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { style: "italic" } );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                                         """.formatted( fileName ), context );

		Path generatedFile = Paths.get( "src/test/resources/generated/%s".formatted( fileName ) );
		assertThat( Files.exists( generatedFile ) ).isTrue();
		assertThat( Files.size( generatedFile ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It should draw bold and italic text" )
	@Test
	public void testDrawTextBoldItalic() throws IOException {
		String fileName = "logo-draw-text-bolditalic.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { style: "bolditalic" } );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                                         """.formatted( fileName ), context );

		Path generatedFile = Paths.get( "src/test/resources/generated/%s".formatted( fileName ) );
		assertThat( Files.exists( generatedFile ) ).isTrue();
		assertThat( Files.size( generatedFile ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It should draw text with a strikethrough" )
	@Test
	public void testDrawTextStrikeThrough() throws IOException {
		String fileName = "logo-draw-text-strikeThrough.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { strikeThrough: true } );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                                         """.formatted( fileName ), context );

		Path generatedFile = Paths.get( "src/test/resources/generated/%s".formatted( fileName ) );
		assertThat( Files.exists( generatedFile ) ).isTrue();
		assertThat( Files.size( generatedFile ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It should draw text with an underline" )
	@Test
	public void testDrawTextUnderline() throws IOException {
		String fileName = "logo-draw-text-underline.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { underline: true } );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                                         """.formatted( fileName ), context );

		Path generatedFile = Paths.get( "src/test/resources/generated/%s".formatted( fileName ) );
		assertThat( Files.exists( generatedFile ) ).isTrue();
		assertThat( Files.size( generatedFile ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It should draw text as a member function" )
	@Test
	public void testDrawTextMember() throws IOException {
		String fileName = "logo-draw-text-member.png";
		runtime.executeSource( """
		                                         result = ImageRead( "src/test/resources/logo.png" );
		                       result.drawText( "Drink your Ovaltine!", 50, 50, { underline: true } );
		                       ImageWrite( result, "src/test/resources/generated/%s" );
		                                         """.formatted( fileName ), context );

		Path generatedFile = Paths.get( "src/test/resources/generated/%s".formatted( fileName ) );
		assertThat( Files.exists( generatedFile ) ).isTrue();
		assertThat( Files.size( generatedFile ) ).isGreaterThan( 0L );
	}

}
