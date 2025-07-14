package ortus.boxlang.modules.image.services;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageKeys;
import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.logging.BoxLangLogger;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.services.BaseService;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;

public class ImageService extends BaseService {

	BoxLangLogger								logger;

	/**
	 * A cache for images that have been processed and are ready to be served.
	 * The key is the image ID, and the value is the image data in Base64 format.
	 */
	private final ConcurrentMap<String, String>	cachedImages	= new ConcurrentHashMap<>();

	public ImageService() {
		this( BoxRuntime.getInstance() );
	}

	public ImageService( BoxRuntime runtime ) {
		super( runtime, ImageKeys.imageService );
	}

	@Override
	public void onConfigurationLoad() {
		// not used
	}

	@Override
	public void onShutdown( Boolean arg0 ) {
		getLogger().info( "+ Image Service shutdown requested" );
	}

	@Override
	public void onStartup() {
		getLogger().info( "+ Image Service started" );
	}

	public void writeToBrowser( IBoxContext context, BoxImage image, IStruct attributes ) {
		getLogger().debug( "Writing image to browser with attributes: {}", attributes );

		String	writeType	= StringCaster.attempt( attributes.get( ImageKeys.writeType ) )
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

	private boolean isExcludedAttribute( Key key ) {
		return key.equals( ImageKeys.writeType.getName() )
		    || key.equals( ImageKeys.name.getName() )
		    || key.equals( ImageKeys.image.getName() )
		    || key.equals( ImageKeys.image1.getName() )
		    || key.equals( ImageKeys.image2.getName() )
		    || key.equals( ImageKeys.source.getName() )
		    || key.equals( ImageKeys.overwrite.getName() )
		    || key.equals( ImageKeys.action.getName() );
	}

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
	 * Get the ORM logger that logs to the "orm" category.
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
