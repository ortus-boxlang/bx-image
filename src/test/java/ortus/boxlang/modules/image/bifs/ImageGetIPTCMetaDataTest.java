package ortus.boxlang.modules.image.bifs;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

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
import ortus.boxlang.runtime.types.IStruct;

public class ImageGetIPTCMetaDataTest {

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
	public void testReturnsAStruct() throws IOException {
		instance.executeSource(
		    """
		    result = ImageGetIPTCMetaData( "src/test/resources/test-images/exif-test.jpg" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );
	}

	@DisplayName( "It should be callable as a member function" )
	@Test
	public void testMemberInvocation() throws IOException {
		instance.executeSource(
		    """
		    result = imageRead( "src/test/resources/test-images/exif-test.jpg" ).getIPTCMetaData();
		    """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );
	}

	@DisplayName( "It should contain various IPTC meta data tags" )
	@Test
	public void testGetsIPTCTags() throws IOException {
		instance.executeSource(
		    """
		    result = ImageGetIPTCMetaData( "src/test/resources/test-images/exif-test.jpg" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );

		IStruct iptcData = variables.getAsStruct( result );
		assertThat( iptcData.get( "City" ) ).isEqualTo( "test city" );
		assertThat( iptcData.get( "Content Location Name" ) ).isEqualTo( "test sublocation" );
		assertThat( iptcData.get( "Country/Primary Location Code" ) ).isEqualTo( "test iso country code" );
		assertThat( iptcData.get( "Country/Primary Location Name" ) ).isEqualTo( "test country" );
		assertThat( iptcData.get( "Credit" ) ).isEqualTo( "test credit line" );
		assertThat( iptcData.get( "Date Created" ) ).isEqualTo( "2024:06:05" );
		assertThat( iptcData.get( "Headline" ) ).isEqualTo( "test headline" );
		assertThat( iptcData.get( "Original Transmission Reference" ) ).isEqualTo( "test job identifier" );
		assertThat( iptcData.get( "Province/State" ) ).isEqualTo( "test state" );
		assertThat( iptcData.get( "Source" ) ).isEqualTo( "test source" );
		assertThat( iptcData.get( "Special Instructions" ) ).isEqualTo( "test instructions" );
	}

	@DisplayName( "It should contain various IPTC meta data tags" )
	@Test
	public void testGetsIPTCTagsFromImage() throws IOException {
		instance.executeSource(
		    """
		    result = ImageGetIPTCMetaData( imageRead( "src/test/resources/test-images/exif-test.jpg" ) );
		    """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );

		IStruct iptcData = variables.getAsStruct( result );
		assertThat( iptcData.get( "City" ) ).isEqualTo( "test city" );
		assertThat( iptcData.get( "Content Location Name" ) ).isEqualTo( "test sublocation" );
		assertThat( iptcData.get( "Country/Primary Location Code" ) ).isEqualTo( "test iso country code" );
		assertThat( iptcData.get( "Country/Primary Location Name" ) ).isEqualTo( "test country" );
		assertThat( iptcData.get( "Credit" ) ).isEqualTo( "test credit line" );
		assertThat( iptcData.get( "Date Created" ) ).isEqualTo( "2024:06:05" );
		assertThat( iptcData.get( "Headline" ) ).isEqualTo( "test headline" );
		assertThat( iptcData.get( "Original Transmission Reference" ) ).isEqualTo( "test job identifier" );
		assertThat( iptcData.get( "Province/State" ) ).isEqualTo( "test state" );
		assertThat( iptcData.get( "Source" ) ).isEqualTo( "test source" );
		assertThat( iptcData.get( "Special Instructions" ) ).isEqualTo( "test instructions" );
	}

}
