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

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.logging.BoxLangLogger;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.services.BaseService;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

/**
 * Service for managing image operations and caching within the BoxLang Image module.
 * This service provides functionality for writing images to the browser, caching images
 * for later retrieval, and managing the lifecycle of image resources.
 *
 * <p>
 * The service maintains an in-memory cache of processed images stored as Base64-encoded
 * strings, allowing for efficient serving of images through URLs or direct data URIs.
 * </p>
 *
 * <p>
 * Key features:
 * </p>
 * <ul>
 * <li>Image caching with UUID-based identifiers for URL-based serving</li>
 * <li>Multiple output formats: URL references or inline Base64 data URIs</li>
 * <li>HTML generation for img tags with custom attributes</li>
 * <li>Thread-safe concurrent access to the image cache</li>
 * </ul>
 *
 * <p>
 * This service is registered with the BoxLang runtime and is accessible via the
 * global service registry using the key "imageService".
 * </p>
 *
 * @see BoxImage
 * @see BaseService
 */
public class ImageService extends BaseService {

	BoxLangLogger								logger;

	/**
	 * A cache for images that have been processed and are ready to be served.
	 * The key is the image ID, and the value is the image data in Base64 format.
	 */
	private final ConcurrentMap<String, String>	cachedImages	= new ConcurrentHashMap<>();

	/**
	 * Creates a new ImageService instance using the singleton BoxRuntime instance.
	 * This constructor is typically used when the service is auto-instantiated by the runtime.
	 */
	public ImageService() {
		this( BoxRuntime.getInstance() );
	}

	/**
	 * Creates a new ImageService instance with the specified BoxRuntime.
	 * This constructor allows for dependency injection and testing with custom runtime instances.
	 *
	 * @param runtime The BoxRuntime instance to use for this service
	 */
	public ImageService( BoxRuntime runtime ) {
		super( runtime, KeyDictionary.imageService );
	}

	/**
	 * Called when the service configuration is loaded.
	 * Currently unused by the ImageService as no additional configuration is required.
	 */
	@Override
	public void onConfigurationLoad() {
		// not used
	}

	/**
	 * Called when the BoxLang runtime is shutting down.
	 * Logs the shutdown request for monitoring and debugging purposes.
	 *
	 * @param arg0 Shutdown parameter (currently unused)
	 */
	@Override
	public void onShutdown( Boolean arg0 ) {
		getLogger().info( "+ Image Service shutdown requested" );
	}

	/**
	 * Called when the BoxLang runtime has started and the service is being initialized.
	 * Logs the startup event for monitoring and debugging purposes.
	 */
	@Override
	public void onStartup() {
		getLogger().info( "+ Image Service started" );
	}

	/**
	 * Writes an image to the browser by generating HTML img tag markup.
	 * The image can be output as either a URL reference (cached) or an inline Base64 data URI.
	 *
	 * <p>
	 * The method supports two write types controlled by the "writeType" attribute:
	 * </p>
	 * <ul>
	 * <li><b>url</b> (default) - Caches the image and generates a URL to retrieve it via the module endpoint</li>
	 * <li><b>base64</b> - Embeds the image directly in the HTML as a Base64-encoded data URI</li>
	 * </ul>
	 *
	 * <p>
	 * Additional attributes in the IStruct are passed through as HTML attributes on the img tag,
	 * except for reserved attributes used for internal processing (writeType, name, image, source, etc.).
	 * </p>
	 *
	 * @param context    The BoxLang execution context where the HTML will be written
	 * @param image      The BoxImage to write to the browser
	 * @param attributes A struct containing:
	 *                   <ul>
	 *                   <li>writeType - "url" or "base64" (default: "url")</li>
	 *                   <li>Any additional HTML attributes for the img tag (alt, width, height, class, etc.)</li>
	 *                   </ul>
	 *
	 * @throws BoxRuntimeException if an invalid writeType is specified or if an error occurs during image encoding
	 */
	public void writeToBrowser( IBoxContext context, BoxImage image, IStruct attributes ) {
		getLogger().debug( "Writing image to browser with attributes: {}", attributes );

		String	writeType	= StringCaster.attempt( attributes.get( KeyDictionary.writeType ) )
		    .orElse( "url" );

		String	src			= "";

		if ( writeType.equals( "url" ) ) {
			String imageId = cachceImage( image );
			src = "/bxModules/bximage/index.bxm?id=" + imageId;
		} else if ( writeType.equals( "base64" ) ) {
			try {
				src = "data:image/png;base64," + image.toBase64String( "png" );
			} catch ( IOException e ) {
				getLogger().error( "Error writing image to browser", e );
			}
		} else {
			throw new BoxRuntimeException( "Invalid write type: " + writeType );
		}

		String htmlAttributes = attributes.entrySet().stream()
		    .filter( entry -> !isExcludedAttribute( entry.getKey() ) )
		    .map( entry -> entry.getKey() + "=\"" + entry.getValue() + "\"" )
		    .collect( Collectors.joining( " " ) );

		context.getBuffer().append( "<img src=\"" + src + "\" " + htmlAttributes + ">" );
	}

	/**
	 * Determines whether an attribute key should be excluded from the HTML img tag output.
	 * Excluded attributes are those used internally by the image module for processing
	 * and should not be passed through as HTML attributes.
	 *
	 * @param key The attribute key to check
	 *
	 * @return true if the attribute should be excluded from HTML output, false if it should be included
	 */
	private boolean isExcludedAttribute( Key key ) {
		return key.equals( KeyDictionary.writeType )
		    || key.equals( KeyDictionary.name )
		    || key.equals( KeyDictionary.image )
		    || key.equals( KeyDictionary.image1 )
		    || key.equals( KeyDictionary.image2 )
		    || key.equals( KeyDictionary.source )
		    || key.equals( KeyDictionary.overwrite )
		    || key.equals( KeyDictionary.action );
	}

	/**
	 * Retrieves a previously cached image by its unique identifier.
	 * This method is used by the module's endpoint to serve cached images when accessed via URL.
	 *
	 * @param context The BoxLang execution context (currently unused but available for future enhancements)
	 * @param id      The unique identifier (UUID) of the cached image
	 *
	 * @return The BoxImage instance reconstructed from the cached Base64 data
	 *
	 * @throws BoxRuntimeException if the image is not found in the cache or if an error occurs
	 *                             during Base64 decoding
	 */
	public BoxImage getCachedImage( IBoxContext context, String id ) {
		getLogger().debug( "Streaming image with ID: {}", id );

		String imageData = this.cachedImages.get( id );
		if ( imageData == null ) {
			throw new BoxRuntimeException( "Image not found in cache for ID: " + id );
		}

		try {
			return BoxImage.fromBase64( imageData );
		} catch ( IOException e ) {
			throw new BoxRuntimeException( "Error retrieving image from cache: " + e.getMessage(), e );
		}
	}

	/**
	 * Remove an image from the cache by its unique identifier.
	 *
	 * @param id The unique identifier (UUID) of the cached image to remove
	 *
	 * @return true if the image was found and removed, false if it was not found
	 */
	public boolean removeCachedImage( String id ) {
		getLogger().info( "Removing cached image with ID: {}", id );
		return this.cachedImages.remove( id ) != null;
	}

	/**
	 * Verify if an image with the given ID exists in the cache.
	 *
	 * @param id The unique identifier (UUID) of the cached image
	 *
	 * @return true if the image exists in the cache, false otherwise
	 */
	public boolean hasCachedImage( String id ) {
		return this.cachedImages.containsKey( id );
	}

	/**
	 * Remove all cached images from memory.
	 */
	public void clearCache() {
		getLogger().info( "Clearing all cached images from memory. Total images cleared: {}", this.cachedImages.size() );
		this.cachedImages.clear();
	}

	/**
	 * How many images are currently cached.
	 *
	 * @return The number of cached images
	 */
	public int getCachedImageCount() {
		return this.cachedImages.size();
	}

	/**
	 * Caches an image in memory and returns a unique identifier for later retrieval.
	 * The image is encoded as a Base64 PNG string and stored in a thread-safe concurrent map
	 * with a UUID as the key.
	 *
	 * <p>
	 * Note: The method name contains a typo ("cachce" instead of "cache") but is kept
	 * for consistency with the existing codebase.
	 * </p>
	 *
	 * @param image The BoxImage to cache
	 *
	 * @return A unique identifier (UUID) that can be used to retrieve the cached image
	 *
	 * @throws BoxRuntimeException if an error occurs during Base64 encoding of the image
	 */
	private String cachceImage( BoxImage image ) {
		String imageId = UUID.randomUUID().toString();
		try {
			this.cachedImages.put( imageId, image.toBase64String( "png" ) );
		} catch ( IOException e ) {
			getLogger().error( "Error caching image", e );
			throw new BoxRuntimeException( "Error caching image: " + e.getMessage(), e );
		}
		return imageId;
	}

	/**
	 * Gets or creates the logger instance for the ImageService.
	 * The logger is lazily initialized using double-checked locking for thread safety
	 * and logs to the "image" category.
	 *
	 * <p>
	 * All image service operations are logged through this logger, including:
	 * </p>
	 * <ul>
	 * <li>Service startup and shutdown events</li>
	 * <li>Image caching operations</li>
	 * <li>Browser write operations</li>
	 * <li>Errors and exceptions</li>
	 * </ul>
	 *
	 * @return The BoxLangLogger instance configured for the "image" logging category
	 */
	public BoxLangLogger getLogger() {
		if ( this.logger == null ) {
			synchronized ( ImageService.class ) {
				if ( this.logger == null ) {
					this.logger = runtime.getLoggingService().getLogger( "image" );
				}
			}
		}
		return this.logger;
	}

}
