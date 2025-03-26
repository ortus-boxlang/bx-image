package ortus.boxlang.modules.image.components;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.compiler.parser.BoxSourceType;
import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.context.ScriptingRequestBoxContext;
import ortus.boxlang.runtime.scopes.IScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.scopes.VariablesScope;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;

public class ImageTest {

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

	@DisplayName( "It should add a border" )
	@Test
	public void testDrawBeveledRect() throws IOException {
		// @formatter:off
		instance.executeSource( """
			<bx:image action="border" source="src/test/resources/logo.png" thickness=5 color="red" name="theImage" />
			<bx:image action="write" source="#theImage#" destination="src/test/resources/generated/logo-component-border.png" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-component-border.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-component-border.png" ) );

		// assertThat( true ).isTrue();
		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testResize() throws IOException {
		// @formatter:off
		instance.executeSource( """
			<bx:image action="resize" source="src/test/resources/logo.png" width="1024" height="385" name="theImage" />
			<bx:image action="write" source="#theImage#" destination="src/test/resources/generated/logo-component-resize.png" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-component-resize.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-component-resize.png" ) );

		// assertThat( true ).isTrue();
		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testConvert() throws IOException {
		try {
			FileSystemUtil.deleteFile( "src/test/resources/generated/logo.jpg" );
		} catch ( Exception e ) {
			// pass
		}
		// @formatter:off
		instance.executeSource( """
			<bx:image action="convert" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo.jpg" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		assertThat( FileSystemUtil.exists( "src/test/resources/generated/logo.jpg" ) ).isTrue();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testConvertWithOverwrite() throws IOException {
		try {
			FileSystemUtil.deleteFile( "src/test/resources/generated/logo-overwrite.jpg" );
		} catch ( Exception e ) {
			// pass
		}
		// @formatter:off
		instance.executeSource( """
			<bx:image action="convert" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo-overwrite.jpg" />
			<bx:image action="convert" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo-overwrite.jpg" overwrite=true />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		assertThat( FileSystemUtil.exists( "src/test/resources/generated/logo-overwrite.jpg" ) ).isTrue();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testConvertWithNoOverwrite() throws IOException {
		try {
			FileSystemUtil.deleteFile( "src/test/resources/generated/logo-overwrite-fail.jpg" );
		} catch ( Exception e ) {
			// pass
		}

		assertThrows( BoxRuntimeException.class, () -> {
			// @formatter:off
			instance.executeSource( """
				<bx:image action="convert" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo-overwrite-fail.jpg" />
				<bx:image action="convert" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo-overwrite-fail.jpg" />
			""", context, BoxSourceType.BOXTEMPLATE );
			// @formatter:on
		} );
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testInfo() throws IOException {
		// @formatter:off
		instance.executeSource( """
			<bx:image action="info" source="src/test/resources/logo.png" structName="imageInfo" />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		IStruct info = variables.getAsStruct( Key.of( "imageInfo" ) );

		assertThat( info ).isNotNull();
	}

	@DisplayName( "It should resize the image" )
	@Test
	public void testRotate() throws IOException {
		// @formatter:off
		instance.executeSource( """
			<bx:image action="rotate" source="src/test/resources/logo.png" destination="src/test/resources/generated/logo-component-rotated.png" angle=45 overwrite=true />
		""", context, BoxSourceType.BOXTEMPLATE );
		// @formatter:on

		var	actual		= Files.readAllBytes( Paths.get( "src/test/resources/generated/logo-component-rotated.png" ) );
		var	expected	= Files.readAllBytes( Paths.get( "src/test/resources/test-images/logo-component-rotated.png" ) );

		// assertThat( true ).isTrue();
		assertThat( Arrays.equals( actual, expected ) ).isTrue();
	}
}
