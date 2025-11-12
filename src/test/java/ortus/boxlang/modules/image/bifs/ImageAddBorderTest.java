package ortus.boxlang.modules.image.bifs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageAddBorderTest extends BaseIntegrationTest {

	@DisplayName( "It can draw the outline of a rectangle" )
	@Test
	public void testDrawRect() {
		runtime.executeSource( """
		                                                           result = ImageRead( "src/test/resources/logo.png" );
		                                         ImageAddBorder( result, 20, "yellow" );
		                       ImageWrite( result, "src/test/resources/generated/logo-add-border.png" );
		                                                           """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

	@DisplayName( "It can be called as a member method" )
	@Test
	public void testDrawRectFilledMemberInvocation() {
		runtime.executeSource( """
		                                                                          result = ImageRead( "src/test/resources/logo.png" );
		                       result.setDrawingColor( "magenta" );
		                                                        result.drawRect( 20, 20, 50, 50, true );
		                                      ImageWrite( result, "src/test/resources/generated/logo-test-member.png" );
		                                                                          """, context );

		// assertInstanceOf( ortus.boxlang.modules.image.BoxImage.class, variables.get( result ) );
	}

}
