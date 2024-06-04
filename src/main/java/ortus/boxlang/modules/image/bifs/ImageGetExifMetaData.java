package ortus.boxlang.modules.image.bifs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Stream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifImageDirectory;
import com.drew.metadata.exif.ExifInteropDirectory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.iptc.IptcDirectory;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.ImageKeys;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.Struct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "getExifMetaData" )
public class ImageGetExifMetaData extends BIF {

	/**
	 * Constructor
	 */
	public ImageGetExifMetaData() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", ImageKeys.name, Set.of( Validator.REQUIRED ) )
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public IStruct _invoke( IBoxContext context, ArgumentsScope arguments ) {
		Struct exifData = new Struct();

		try {
			InputStream	imageInputStream	= null;

			Object		arg					= arguments.get( ImageKeys.name );

			if ( arg instanceof BoxImage image ) {
				imageInputStream = new FileInputStream( image.getSourcePath() );
			} else if ( arg instanceof String imageString ) {
				imageInputStream = getInputStreamFromString( imageString );
			}

			if ( imageInputStream == null ) {
				throw new BoxRuntimeException( "Unable to read Exif metadata" );
			}

			Metadata metaData = ImageMetadataReader.readMetadata( imageInputStream );

			Stream.of(
			    metaData.getFirstDirectoryOfType( ExifImageDirectory.class ),
			    metaData.getFirstDirectoryOfType( ExifInteropDirectory.class ),
			    metaData.getFirstDirectoryOfType( ExifIFD0Directory.class ),
			    metaData.getFirstDirectoryOfType( ExifThumbnailDirectory.class ),
			    metaData.getFirstDirectoryOfType( ExifSubIFDDirectory.class ),
			    metaData.getFirstDirectoryOfType( GpsDirectory.class ),
			    metaData.getFirstDirectoryOfType( IptcDirectory.class )
			)
			    .filter( dir -> dir != null )
			    .forEach( dir -> {
				    dir.getTags().stream().forEach( tag -> exifData.put( tag.getTagName(), tag.getDescription() ) );
			    } );

			if ( exifData.get( "Date/Time" ) == null ) {
				exifData.put( "Date/Time", exifData.get( "Date/Time Original" ) );
			}
		} catch ( ImageProcessingException e ) {
			throw new BoxRuntimeException( "Unable to process image metadata", e );
		} catch ( IOException e ) {
			throw new BoxRuntimeException( "Unable to read image", e );
		}

		return exifData;
	}

	private InputStream getInputStreamFromString( String imageURI ) throws IOException {

		return new FileInputStream( imageURI );
	}

}
