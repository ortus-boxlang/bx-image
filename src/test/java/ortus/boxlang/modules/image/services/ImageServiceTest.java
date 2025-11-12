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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.modules.image.BaseIntegrationTest;
import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Struct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class ImageServiceTest extends BaseIntegrationTest {

	private ImageService imageService;

	@BeforeEach
	public void setupService() {
		// Create a new ImageService instance for testing
		// We can't cast from runtime.getGlobalService() due to classloader differences
		imageService = new ImageService( runtime );
		assertNotNull( imageService, "ImageService should not be null" );
	}

	@DisplayName( "It can write an image to browser with URL type" )
	@Test
	public void testWriteToBrowserWithURL() {
		// Create a test image
		runtime.executeSource( """
		                       result = ImageNew( "", 100, 100, "argb", "blue" );
		                       """, context );

		BoxImage	testImage	= ( BoxImage ) variables.get( result );
		Struct		attributes	= new Struct();
		attributes.put( KeyDictionary.writeType, "url" );
		attributes.put( Key.of( "alt" ), "Test Image" );
		attributes.put( Key.of( "width" ), "100" );
		attributes.put( Key.of( "height" ), "100" );

		// Write to browser
		imageService.writeToBrowser( context, testImage, attributes );

		// Get the output from the context buffer
		String output = context.getBuffer().toString();

		// Verify the output contains img tag with URL
		assertTrue( output.contains( "<img src=\"/bxModules/bximage/index.bxm?id=" ), "Output should contain module URL" );
		assertTrue( output.contains( "alt=\"Test Image\"" ), "Output should contain alt attribute" );
		assertTrue( output.contains( "width=\"100\"" ), "Output should contain width attribute" );
		assertTrue( output.contains( "height=\"100\"" ), "Output should contain height attribute" );
	}

	@DisplayName( "It can write an image to browser with Base64 type" )
	@Test
	public void testWriteToBrowserWithBase64() {
		// Create a test image
		runtime.executeSource( """
		                       result = ImageNew( "", 50, 50, "argb", "red" );
		                       """, context );

		BoxImage	testImage	= ( BoxImage ) variables.get( result );
		Struct		attributes	= new Struct();
		attributes.put( KeyDictionary.writeType, "base64" );
		attributes.put( Key.of( "alt" ), "Base64 Image" );
		attributes.put( Key.of( "class" ), "test-class" );

		// Write to browser
		imageService.writeToBrowser( context, testImage, attributes );

		// Get the output from the context buffer
		String output = context.getBuffer().toString();

		// Verify the output contains img tag with Base64 data URI
		assertTrue( output.contains( "<img src=\"data:image/png;base64," ), "Output should contain Base64 data URI" );
		assertTrue( output.contains( "alt=\"Base64 Image\"" ), "Output should contain alt attribute" );
		assertTrue( output.contains( "class=\"test-class\"" ), "Output should contain class attribute" );
	}

	@DisplayName( "It excludes internal attributes from HTML output" )
	@Test
	public void testExcludesInternalAttributes() {
		// Create a test image
		runtime.executeSource( """
		                       result = ImageNew( "", 50, 50, "argb", "green" );
		                       """, context );

		BoxImage	testImage	= ( BoxImage ) variables.get( result );
		Struct		attributes	= new Struct();
		attributes.put( KeyDictionary.writeType, "base64" );
		attributes.put( KeyDictionary.name, "shouldNotAppear" );
		attributes.put( KeyDictionary.image, "shouldNotAppear" );
		attributes.put( KeyDictionary.source, "shouldNotAppear" );
		attributes.put( KeyDictionary.action, "shouldNotAppear" );
		attributes.put( Key.of( "alt" ), "Test" );

		// Write to browser
		imageService.writeToBrowser( context, testImage, attributes );

		// Get the output from the context buffer
		String output = context.getBuffer().toString();

		// Verify internal attributes are excluded
		assertTrue( !output.contains( "name=" ), "Output should not contain name attribute" );
		assertTrue( !output.contains( "image=" ), "Output should not contain image attribute" );
		assertTrue( !output.contains( "source=" ), "Output should not contain source attribute" );
		assertTrue( !output.contains( "action=" ), "Output should not contain action attribute" );
		assertTrue( !output.contains( "writeType=" ), "Output should not contain writeType attribute" );
		assertTrue( output.contains( "alt=\"Test\"" ), "Output should contain alt attribute" );
	}

	@DisplayName( "It throws exception for invalid write type" )
	@Test
	public void testInvalidWriteType() {
		// Create a test image
		runtime.executeSource( """
		                       result = ImageNew( "", 50, 50, "argb", "yellow" );
		                       """, context );

		BoxImage	testImage	= ( BoxImage ) variables.get( result );
		Struct		attributes	= new Struct();
		attributes.put( KeyDictionary.writeType, "invalid" );

		// Should throw exception
		assertThrows( BoxRuntimeException.class, () -> {
			imageService.writeToBrowser( context, testImage, attributes );
		}, "Should throw exception for invalid write type" );
	}

	@DisplayName( "It can cache and retrieve images" )
	@Test
	public void testCacheAndRetrieveImage() {
		// Create a test image with distinct characteristics
		runtime.executeSource( """
		                       result = ImageNew( "", 75, 75, "argb", "cyan" );
		                       """, context );

		BoxImage	testImage	= ( BoxImage ) variables.get( result );
		Struct		attributes	= new Struct();
		attributes.put( KeyDictionary.writeType, "url" );

		// Write to browser (which caches the image)
		imageService.writeToBrowser( context, testImage, attributes );

		// Extract the image ID from the output
		String		output		= context.getBuffer().toString();
		int			idStart		= output.indexOf( "?id=" ) + 4;
		int			idEnd		= output.indexOf( "\"", idStart );
		String		imageId		= output.substring( idStart, idEnd );

		// Retrieve the cached image
		BoxImage	cachedImage	= imageService.getCachedImage( context, imageId );

		// Verify the image was retrieved
		assertNotNull( cachedImage, "Cached image should not be null" );
		assertEquals( 75, cachedImage.getWidth(), "Cached image should have correct width" );
		assertEquals( 75, cachedImage.getHeight(), "Cached image should have correct height" );
	}

	@DisplayName( "It throws exception when retrieving non-existent cached image" )
	@Test
	public void testRetrieveNonExistentCachedImage() {
		String				nonExistentId	= "00000000-0000-0000-0000-000000000000";

		// Should throw exception for non-existent ID
		BoxRuntimeException	exception		= assertThrows( BoxRuntimeException.class, () -> {
												imageService.getCachedImage( context, nonExistentId );
											}, "Should throw exception for non-existent image ID" );

		assertTrue( exception.getMessage().contains( "Image not found in cache" ),
		    "Exception message should indicate image not found" );
	}

	@DisplayName( "It uses URL write type by default" )
	@Test
	public void testDefaultWriteType() {
		// Create a test image
		runtime.executeSource( """
		                       result = ImageNew( "", 60, 60, "argb", "magenta" );
		                       """, context );

		BoxImage	testImage	= ( BoxImage ) variables.get( result );
		Struct		attributes	= new Struct();
		// Don't set writeType - should default to "url"
		attributes.put( Key.of( "alt" ), "Default Type Test" );

		// Write to browser
		imageService.writeToBrowser( context, testImage, attributes );

		// Get the output from the context buffer
		String output = context.getBuffer().toString();

		// Verify it defaults to URL type
		assertTrue( output.contains( "/bxModules/bximage/index.bxm?id=" ), "Should default to URL write type" );
	}

	@DisplayName( "It has a valid logger instance" )
	@Test
	public void testGetLogger() {
		assertNotNull( imageService.getLogger(), "Logger should not be null" );
		assertEquals( "image", imageService.getLogger().getName(), "Logger should use 'image' category" );
	}
}
