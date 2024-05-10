package ortus.boxlang.modules.image.bifs;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.context.ScriptingRequestBoxContext;
import ortus.boxlang.runtime.scopes.IScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.scopes.VariablesScope;

public class ImageNewTest {

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

	@DisplayName( "It can create a new image" )
	@Test
	public void testNewImage() {
		instance.executeSource( """
		                                                            result = ImageNew( "", 256, 256, "argb", "green" );
		                        ImageWrite( result, "src/test/resources/new-image.png" );
		                                                            """, context );

		assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can create a new image from a url" )
	@Test
	public void testImageFromURL() {
		instance.executeSource(
		    """
		                                        result = ImageNew( "https://communitycdn.ortussolutions.com/original/2X/1/1459cdd448100319697645d3eb15894396f042df.png" );
		    ImageWrite( result, "src/test/resources/new-image-url.png" );
		                                        """,
		    context );

		assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can create a new image from a file" )
	@Test
	public void testImageFromFile() {
		instance.executeSource(
		    """
		       result = ImageNew( "src/test/resources/logo.png" );
		    ImageWrite( result, "src/test/resources/new-image-file.png" );
		       """,
		    context );

		assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can create a new image from an existing BoxImage" )
	@Test
	public void testImageFromBoxImage() {
		instance.executeSource(
		    """
		            a = ImageNew( "src/test/resources/logo.png" );
		          result = ImageNew( a );
		       a.setDrawingColor( "green" );
		       a.drawRect( 20, 20, 50, 50, true );
		    result.setDrawingColor( "blue" );
		       result.drawRect( 20, 20, 50, 50, true );

		    a.write( "src/test/resources/new-image-copy-orig.png" );
		       result.write( "src/test/resources/new-image-copy-copy.png" );
		            """,
		    context );

		assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

}
