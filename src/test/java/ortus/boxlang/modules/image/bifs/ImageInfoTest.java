package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.context.ScriptingRequestBoxContext;
import ortus.boxlang.runtime.scopes.IScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.scopes.VariablesScope;
import ortus.boxlang.runtime.types.IStruct;

public class ImageInfoTest {

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

	@DisplayName( "It should return a struct" )
	@Test
	public void testStructReturn() throws IOException {
		instance.executeSource( """
		                                          image = ImageRead( "src/test/resources/logo.png" );
		                        result = ImageInfo( image );
		                                          """, context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );
	}

	@DisplayName( "It should be callable as a member function" )
	@Test
	public void testMemberInfocation() throws IOException {
		instance.executeSource( """
		                                          image = ImageRead( "src/test/resources/logo.png" );
		                        result = image.info();
		                                          """, context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );
	}

	@DisplayName( "It should be able to read image info from a URL" )
	@Test
	public void testRemoteImage() throws IOException {
		instance.executeSource(
		    """
		                   image = ImageRead( "https://communitycdn.ortussolutions.com/original/2X/1/1459cdd448100319697645d3eb15894396f042df.png" );


		    result = image.info();
		                   """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );
	}

	@DisplayName( "It should return the correct data" )
	@Test
	public void testKeys() throws IOException {
		instance.executeSource( """
		                                          image = ImageRead( "src/test/resources/logo.png" );
		                        result = ImageInfo( image );
		                                          """, context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );

		IStruct info = variables.getAsStruct( result );
		assertThat( info.get( "width" ) ).isEqualTo( 256 );
		assertThat( info.get( "height" ) ).isEqualTo( 256 );
		assertThat( info.get( "source" ) ).isEqualTo( ( ( BoxImage ) variables.get( "image" ) ).getSourcePath() );

		IStruct colorModel = info.getAsStruct( Key.of( "colormodel" ) );

		assertThat( colorModel.get( "alpha_channel_support" ) ).isEqualTo( true );
		assertThat( colorModel.get( "alpha_premultiplied" ) ).isEqualTo( false );
		assertThat( colorModel.get( "bits_component_1" ) ).isEqualTo( 8 );
		assertThat( colorModel.get( "bits_component_2" ) ).isEqualTo( 8 );
		assertThat( colorModel.get( "bits_component_3" ) ).isEqualTo( 8 );
		assertThat( colorModel.get( "bits_component_4" ) ).isEqualTo( 8 );
		assertThat( colorModel.get( "colormodel_type" ) ).isEqualTo( "ComponentColorModel" );
		assertThat( colorModel.get( "colorspace" ) ).isEqualTo( "Any of the family of RGB color spaces." );
		assertThat( colorModel.get( "num_color_components" ) ).isEqualTo( 3 );
		assertThat( colorModel.get( "num_components" ) ).isEqualTo( 4 );
		assertThat( colorModel.get( "pixel_size" ) ).isEqualTo( 32 );
		assertThat( colorModel.get( "transparency" ) ).isEqualTo( "TRANSLUCENT" );

	}
}
