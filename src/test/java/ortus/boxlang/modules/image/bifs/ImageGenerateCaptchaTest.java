/**
 * [BoxLang]
 *
 * Copyright [2024] [Ortus Solutions, Corp]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.compiler.parser.BoxSourceType;
import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.runtime.scopes.Key;

/**
 * Tests for ImageGenerateCaptcha BIF.
 * Argument order matches ColdFusion: height, width, text [, difficulty [, fonts [, fontSize]]]
 */
public class ImageGenerateCaptchaTest extends BaseIntegrationTest {

	private static final String GENERATED_DIR = "src/test/resources/generated/";

	@BeforeAll
	public static void ensureGeneratedDir() {
		new File( GENERATED_DIR ).mkdirs();
	}

	@DisplayName( "It generates a captcha and returns a BoxImage" )
	@Test
	public void testGenerateCaptchaReturnType() {
		runtime.executeSource( """
		    result = ImageGenerateCaptcha( 75, 200, "ABCD" );
		""", context );

		assertThat( variables.get( result ) ).isInstanceOf( BoxImage.class );
	}

	@DisplayName( "It generates a captcha with the specified dimensions" )
	@Test
	public void testGenerateCaptchaDimensions() {
		runtime.executeSource( """
		    result = ImageGenerateCaptcha( 100, 300, "HELLO" );
		    w = result.getWidth();
		    h = result.getHeight();
		""", context );

		assertThat( ( int ) variables.get( Key.of( "w" ) ) ).isEqualTo( 300 );
		assertThat( ( int ) variables.get( Key.of( "h" ) ) ).isEqualTo( 100 );
	}

	@DisplayName( "It generates a captcha with difficulty=low and writes to file" )
	@Test
	public void testGenerateCaptchaLow() throws IOException {
		String filePath = GENERATED_DIR + "captcha-low.png";
		runtime.executeSource( """
		    result = ImageGenerateCaptcha( 75, 200, "WXYZ", "low" );
		    ImageWrite( result, "%s" );
		""".formatted( filePath ), context );

		Path p = Paths.get( filePath );
		assertThat( Files.exists( p ) ).isTrue();
		assertThat( Files.size( p ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It generates a captcha with difficulty=medium" )
	@Test
	public void testGenerateCaptchaMedium() throws IOException {
		String filePath = GENERATED_DIR + "captcha-medium.png";
		runtime.executeSource( """
		    result = ImageGenerateCaptcha( 75, 200, "WXYZ", "medium" );
		    ImageWrite( result, "%s" );
		""".formatted( filePath ), context );

		Path p = Paths.get( filePath );
		assertThat( Files.exists( p ) ).isTrue();
		assertThat( Files.size( p ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It generates a captcha with difficulty=high" )
	@Test
	public void testGenerateCaptchaHigh() throws IOException {
		String filePath = GENERATED_DIR + "captcha-high.png";
		runtime.executeSource( """
		    result = ImageGenerateCaptcha( 75, 200, "WXYZ", "high" );
		    ImageWrite( result, "%s" );
		""".formatted( filePath ), context );

		Path p = Paths.get( filePath );
		assertThat( Files.exists( p ) ).isTrue();
		assertThat( Files.size( p ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It generates a captcha with custom fonts" )
	@Test
	public void testGenerateCaptchaWithFonts() throws IOException {
		String filePath = GENERATED_DIR + "captcha-fonts.png";
		runtime.executeSource( """
		    result = ImageGenerateCaptcha( 75, 200, "CODE", "low", "SansSerif,Serif" );
		    ImageWrite( result, "%s" );
		""".formatted( filePath ), context );

		Path p = Paths.get( filePath );
		assertThat( Files.exists( p ) ).isTrue();
		assertThat( Files.size( p ) ).isGreaterThan( 0L );
	}

	@DisplayName( "It generates a captcha with custom fontSize" )
	@Test
	public void testGenerateCaptchaWithFontSize() throws IOException {
		String filePath = GENERATED_DIR + "captcha-fontsize.png";
		runtime.executeSource( """
		    result = ImageGenerateCaptcha( 100, 400, "BIG", "low", "", 36 );
		    ImageWrite( result, "%s" );
		""".formatted( filePath ), context );

		Path p = Paths.get( filePath );
		assertThat( Files.exists( p ) ).isTrue();
		assertThat( Files.size( p ) ).isGreaterThan( 0L );
		BoxImage img = ( BoxImage ) variables.get( result );
		assertThat( img.getWidth() ).isEqualTo( 400 );
		assertThat( img.getHeight() ).isEqualTo( 100 );
	}

	@DisplayName( "It matches the ColdFusion 3-arg form: ImageGenerateCaptcha(height, width, text)" )
	@Test
	public void testColdFusionCompatThreeArgs() {
		runtime.executeSource( """
		    result = ImageGenerateCaptcha( 35, 400, "loner" );
		    w = result.getWidth();
		    h = result.getHeight();
		""", context );

		assertThat( ( int ) variables.get( Key.of( "w" ) ) ).isEqualTo( 400 );
		assertThat( ( int ) variables.get( Key.of( "h" ) ) ).isEqualTo( 35 );
	}

	@DisplayName( "It matches the ColdFusion 4-arg form: ImageGenerateCaptcha(height, width, text, difficulty)" )
	@Test
	public void testColdFusionCompatFourArgs() {
		runtime.executeSource( """
		    result = ImageGenerateCaptcha( 35, 400, "loner", "high" );
		""", context );

		assertThat( variables.get( result ) ).isInstanceOf( BoxImage.class );
	}

	@DisplayName( "It matches the ColdFusion 6-arg form: ImageGenerateCaptcha(height, width, text, difficulty, fonts, fontSize)" )
	@Test
	public void testColdFusionCompatSixArgs() {
		runtime.executeSource( """
		    result = ImageGenerateCaptcha( 35, 400, "loner", "high", "serif,sansserif", 24 );
		""", context );

		assertThat( variables.get( result ) ).isInstanceOf( BoxImage.class );
	}

	@DisplayName( "It generates a captcha via bx:image component with name" )
	@Test
	public void testCaptchaComponentWithName() {
		runtime.executeSource( """
		    <bx:image action="captcha" text="COMP" width="250" height="80" name="myCaptcha" />
		""", context, BoxSourceType.BOXTEMPLATE );

		BoxImage img = ( BoxImage ) variables.get( Key.of( "myCaptcha" ) );
		assertThat( img ).isNotNull();
		assertThat( img.getWidth() ).isEqualTo( 250 );
		assertThat( img.getHeight() ).isEqualTo( 80 );
	}

	@DisplayName( "It generates a captcha via bx:image component with destination" )
	@Test
	public void testCaptchaComponentWithDestination() throws IOException {
		String filePath = GENERATED_DIR + "captcha-component.png";
		Files.deleteIfExists( Paths.get( filePath ) );

		runtime.executeSource( """
		    <bx:image action="captcha" text="COMP" width="200" height="75"
		              difficulty="medium" destination="%s" overwrite=true name="myCaptcha" />
		""".formatted( filePath ), context, BoxSourceType.BOXTEMPLATE );

		assertThat( Files.exists( Paths.get( filePath ) ) ).isTrue();
		assertThat( Files.size( Paths.get( filePath ) ) ).isGreaterThan( 0L );

		BoxImage img = ( BoxImage ) variables.get( Key.of( "myCaptcha" ) );
		assertThat( img ).isNotNull();
	}

}
