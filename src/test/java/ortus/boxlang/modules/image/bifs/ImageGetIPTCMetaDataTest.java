package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BaseIntegrationTest;

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

public class ImageGetIPTCMetaDataTest extends BaseIntegrationTest {

	@DisplayName( "It should return a struct" )
	@Test
	public void testReturnsAStruct() throws IOException {
		runtime.executeSource(
		    """
		    result = ImageGetIPTCMetaData( "src/test/resources/test-images/exif-test.jpg" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );
	}

	@DisplayName( "It should be callable as a member function" )
	@Test
	public void testMemberInvocation() throws IOException {
		runtime.executeSource(
		    """
		    result = imageRead( "src/test/resources/test-images/exif-test.jpg" ).getIPTCMetaData();
		    """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );
	}

	@DisplayName( "It should contain various IPTC meta data tags" )
	@Test
	public void testGetsIPTCTags() throws IOException {
		runtime.executeSource(
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
		runtime.executeSource(
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

	@DisplayName( "It should be able to read metadata info from a URL" )
	@Test
	public void testRemoteImage() throws IOException {
		runtime.executeSource(
		    """
		    result = ImageGetIPTCMetaData( "https://communitycdn.ortussolutions.com/original/2X/1/1459cdd448100319697645d3eb15894396f042df.png" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );
	}

}
