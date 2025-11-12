package ortus.boxlang.modules.image.bifs;

import ortus.boxlang.modules.image.BaseIntegrationTest;

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

public class ImagePasteTest extends BaseIntegrationTest {

	@DisplayName( "It can draw an Image" )
	@Test
	public void testDrawImage() {
		runtime.executeSource(
		    """
		                                                    result = ImageRead( "src/test/resources/logo.png" );
		    			realLogo = ImageNew( "https://communitycdn.ortussolutions.com/original/2X/1/1459cdd448100319697645d3eb15894396f042df.png" );
		                                  ImagePaste( result, realLogo, 50, 50 );
		    result.write( "src/test/resources/generated/logo-paste.png" );
		                                                    """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

}
