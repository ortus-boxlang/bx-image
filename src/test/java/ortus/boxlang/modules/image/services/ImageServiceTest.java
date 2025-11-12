/**
 * [BoxLang]
 *
 * Copyright [2023] [Ortus Solutions, Corp]
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
package ortus.boxlang.modules.image.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;

public class ImageServiceTest extends BaseIntegrationTest {

	@DisplayName( "It can write an image to browser with URL type" )
	@Test
	public void testWriteToBrowserWithURL() {
		runtime.executeSource( """
		                       img = ImageNew( "", 100, 100, "argb", "blue" );

		                       // Get the image service
		                       service = getBoxRuntime().getGlobalService( "imageService" );

		                       // Create attributes struct
		                       attrs = {
		                           "writeType": "url",
		                           "alt": "Test Image",
		                           "width": "100",
		                           "height": "100"
		                       };

		                       // Write to browser
		                       service.writeToBrowser( getBoxContext(), img, attrs );

		                       // Get the output
		                       result = getBoxContext().getBuffer().toString();
		                       """, context );

		String output = ( String ) variables.get( result );

		// Verify the output contains img tag with URL
		assertNotNull( output, "Output should not be null" );
		assertTrue( output.contains( "<img src=\"/bxModules/bximage/index.bxm?id=" ), "Output should contain module URL" );
		assertTrue( output.contains( "alt=\"Test Image\"" ), "Output should contain alt attribute" );
		assertTrue( output.contains( "width=\"100\"" ), "Output should contain width attribute" );
		assertTrue( output.contains( "height=\"100\"" ), "Output should contain height attribute" );
	}

	@DisplayName( "It can write an image to browser with Base64 type" )
	@Test
	public void testWriteToBrowserWithBase64() {
		runtime.executeSource( """
		                       img = ImageNew( "", 50, 50, "argb", "red" );

		                       // Get the image service
		                       service = getBoxRuntime().getGlobalService( "imageService" );

		                       // Create attributes struct
		                       attrs = {
		                           "writeType": "base64",
		                           "alt": "Base64 Image",
		                           "class": "test-class"
		                       };

		                       // Write to browser
		                       service.writeToBrowser( getBoxContext(), img, attrs );

		                       // Get the output
		                       result = getBoxContext().getBuffer().toString();
		                       """, context );

		String output = ( String ) variables.get( result );

		// Verify the output contains img tag with Base64 data URI
		assertNotNull( output, "Output should not be null" );
		assertTrue( output.contains( "<img src=\"data:image/png;base64," ), "Output should contain Base64 data URI" );
		assertTrue( output.contains( "alt=\"Base64 Image\"" ), "Output should contain alt attribute" );
		assertTrue( output.contains( "class=\"test-class\"" ), "Output should contain class attribute" );
	}

	@DisplayName( "It excludes internal attributes from HTML output" )
	@Test
	public void testExcludesInternalAttributes() {
		runtime.executeSource( """
		                       img = ImageNew( "", 50, 50, "argb", "green" );

		                       // Get the image service
		                       service = getBoxRuntime().getGlobalService( "imageService" );

		                       // Create attributes struct with internal attributes
		                       attrs = {
		                           "writeType": "base64",
		                           "name": "shouldNotAppear",
		                           "image": "shouldNotAppear",
		                           "source": "shouldNotAppear",
		                           "action": "shouldNotAppear",
		                           "alt": "Test"
		                       };

		                       // Write to browser
		                       service.writeToBrowser( getBoxContext(), img, attrs );

		                       // Get the output
		                       result = getBoxContext().getBuffer().toString();
		                       """, context );

		String output = ( String ) variables.get( result );

		// Verify internal attributes are excluded
		assertNotNull( output, "Output should not be null" );
		assertTrue( !output.contains( "name=" ) || output.contains( "name=\"" ) && !output.contains( "name=\"shouldNotAppear\"" ),
		    "Output should not contain name attribute with internal value" );
		assertTrue( !output.contains( "image=" ), "Output should not contain image attribute" );
		assertTrue( !output.contains( "source=" ), "Output should not contain source attribute" );
		assertTrue( !output.contains( "action=" ), "Output should not contain action attribute" );
		assertTrue( !output.contains( "writeType=" ), "Output should not contain writeType attribute" );
		assertTrue( output.contains( "alt=\"Test\"" ), "Output should contain alt attribute" );
	}

	@DisplayName( "It throws exception for invalid write type" )
	@Test
	public void testInvalidWriteType() {
		runtime.executeSource( """
		                       img = ImageNew( "", 50, 50, "argb", "yellow" );

		                       // Get the image service
		                       service = getBoxRuntime().getGlobalService( "imageService" );

		                       // Create attributes struct with invalid writeType
		                       attrs = {
		                           "writeType": "invalid"
		                       };

		                       // Try to write to browser - should throw exception
		                       try {
		                           service.writeToBrowser( getBoxContext(), img, attrs );
		                           result = "NO_EXCEPTION";
		                       } catch( any e ) {
		                           result = e.message;
		                       }
		                       """, context );

		String resultStr = ( String ) variables.get( result );

		// Should have thrown exception
		assertNotNull( resultStr, "Result should not be null" );
		assertTrue( resultStr.contains( "Invalid write type" ), "Should throw exception for invalid write type" );
	}

	@DisplayName( "It can cache and retrieve images" )
	@Test
	public void testCacheAndRetrieveImage() {
		runtime.executeSource( """
		                       img = ImageNew( "", 75, 75, "argb", "cyan" );

		                       // Get the image service
		                       service = getBoxRuntime().getGlobalService( "imageService" );

		                       // Create attributes struct
		                       attrs = {
		                           "writeType": "url"
		                       };

		                       // Write to browser (which caches the image)
		                       service.writeToBrowser( getBoxContext(), img, attrs );

		                       // Get the output and extract image ID
		                       output = getBoxContext().getBuffer().toString();
		                       idStart = output.indexOf( "?id=" ) + 4;
		                       idEnd = output.indexOf( '"', idStart );
		                       imageId = output.substring( idStart, idEnd );

		                       // Retrieve the cached image
		                       cachedImage = service.getCachedImage( getBoxContext(), imageId );

		                       // Store results for verification
		                       result = {
		                           "width": cachedImage.getWidth(),
		                           "height": cachedImage.getHeight(),
		                           "isNull": isNull( cachedImage )
		                       };
		                       """, context );

		Object resultObj = variables.get( result );

		// Verify the image was retrieved
		assertNotNull( resultObj, "Result should not be null" );
	}

	@DisplayName( "It throws exception when retrieving non-existent cached image" )
	@Test
	public void testRetrieveNonExistentCachedImage() {
		runtime.executeSource( """
		                       // Get the image service
		                       service = getBoxRuntime().getGlobalService( "imageService" );

		                       nonExistentId = "00000000-0000-0000-0000-000000000000";

		                       // Try to retrieve non-existent image
		                       try {
		                           service.getCachedImage( getBoxContext(), nonExistentId );
		                           result = "NO_EXCEPTION";
		                       } catch( any e ) {
		                           result = e.message;
		                       }
		                       """, context );

		String resultStr = ( String ) variables.get( result );

		// Should have thrown exception
		assertNotNull( resultStr, "Result should not be null" );
		assertTrue( resultStr.contains( "Image not found in cache" ), "Exception message should indicate image not found" );
	}

	@DisplayName( "It uses URL write type by default" )
	@Test
	public void testDefaultWriteType() {
		runtime.executeSource( """
		                       img = ImageNew( "", 60, 60, "argb", "magenta" );

		                       // Get the image service
		                       service = getBoxRuntime().getGlobalService( "imageService" );

		                       // Create attributes struct WITHOUT writeType
		                       attrs = {
		                           "alt": "Default Type Test"
		                       };

		                       // Write to browser
		                       service.writeToBrowser( getBoxContext(), img, attrs );

		                       // Get the output
		                       result = getBoxContext().getBuffer().toString();
		                       """, context );

		String output = ( String ) variables.get( result );

		// Verify it defaults to URL type
		assertNotNull( output, "Output should not be null" );
		assertTrue( output.contains( "/bxModules/bximage/index.bxm?id=" ), "Should default to URL write type" );
	}

	@DisplayName( "It has a valid logger instance" )
	@Test
	public void testGetLogger() {
		runtime.executeSource( """
		                       // Get the image service
		                       service = getBoxRuntime().getGlobalService( "imageService" );

		                       // Get the logger
		                       logger = service.getLogger();

		                       result = {
		                           "isNull": isNull( logger ),
		                           "name": logger.getName()
		                       };
		                       """, context );

		Object resultObj = variables.get( result );

		// Verify logger exists
		assertNotNull( resultObj, "Result should not be null" );
	}
}
