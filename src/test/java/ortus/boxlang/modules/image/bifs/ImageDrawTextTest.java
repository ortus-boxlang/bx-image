package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.context.ScriptingRequestBoxContext;
import ortus.boxlang.runtime.scopes.IScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.scopes.VariablesScope;

@DisabledOnOs( OS.LINUX )
public class ImageDrawTextTest {

	static BoxRuntime	instance;
	IBoxContext			context;
	IScope				variables;
	static Key			result	= new Key( "result" );

	@BeforeAll
	public static void setUp() {
		instance = BoxRuntime.getInstance( true );
	}

	@BeforeEach
	public void setupEach() {
		context		= new ScriptingRequestBoxContext( instance.getRuntimeContext() );
		variables	= context.getScopeNearby( VariablesScope.name );
	}

	@DisplayName( "It should draw text" )
	@Test
	public void testDrawText() throws IOException {
		String fileName = "logo-draw-text.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageDrawText( result, "Drink your Ovaltine!", 50, 50 );
		                        ImageWrite( result, "src/test/resources/%s" );
		                        //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw text for a certain font" )
	@Test
	public void testDrawTextFont() throws IOException {
		String fileName = "logo-draw-text-font.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { font: "Georgia" } );
		                        ImageWrite( result, "src/test/resources/%s" );
		                        //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw font of different sizes" )
	@Test
	public void testDrawTextSize() throws IOException {
		String fileName = "logo-draw-text-size.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { size: 50 } );
		                        ImageWrite( result, "src/test/resources/%s" );
		                        //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw bold text" )
	@Test
	public void testDrawTextBold() throws IOException {
		String fileName = "logo-draw-text-bold.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { style: "bold" } );
		                        ImageWrite( result, "src/test/resources/%s" );
		                        //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw italic text" )
	@Test
	public void testDrawTextItalic() throws IOException {
		String fileName = "logo-draw-text-italic.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { style: "italic" } );
		                        ImageWrite( result, "src/test/resources/%s" );
		                        //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw bold and italic text" )
	@Test
	public void testDrawTextBoldItalic() throws IOException {
		String fileName = "logo-draw-text-bolditalic.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { style: "bolditalic" } );
		                        ImageWrite( result, "src/test/resources/%s" );
		                        //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw text with a strikethrough" )
	@Test
	public void testDrawTextStrikeThrough() throws IOException {
		String fileName = "logo-draw-text-strikeThrough.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { strikeThrough: true } );
		                        ImageWrite( result, "src/test/resources/%s" );
		                        //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw text with an underline" )
	@Test
	public void testDrawTextUnderline() throws IOException {
		String fileName = "logo-draw-text-underline.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        ImageDrawText( result, "Drink your Ovaltine!", 50, 50, { underline: true } );
		                        ImageWrite( result, "src/test/resources/%s" );
		                        //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should draw text as a member function" )
	@Test
	public void testDrawTextMember() throws IOException {
		String fileName = "logo-draw-text-underline.png";
		instance.executeSource( """
		                                          result = ImageRead( "src/test/resources/logo.png" );
		                        result.drawText( "Drink your Ovaltine!", 50, 50, { underline: true } );
		                        ImageWrite( result, "src/test/resources/%s" );
		                        //ImageWrite( result, "src/test/resources/test-images/%s" );
		                                          """.formatted( fileName, fileName ), context );

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/%s".formatted( fileName ) ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/%s".formatted( fileName ) ) );

		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

}
