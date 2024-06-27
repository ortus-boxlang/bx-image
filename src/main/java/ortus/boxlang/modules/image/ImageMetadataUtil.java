package ortus.boxlang.modules.image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
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

import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.Struct;

public class ImageMetadataUtil {

	public static IStruct readExifMetaData( String inputStream ) throws ImageProcessingException, FileNotFoundException, IOException {
		return readExifMetaData( getInputStream( inputStream ) );
	}

	public static IStruct readExifMetaData( InputStream inputStream ) throws ImageProcessingException, FileNotFoundException, IOException {
		IStruct		exifData	= new Struct();
		Metadata	metaData	= ImageMetadataReader.readMetadata( inputStream );

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

		return exifData;
	}

	public static IStruct readIPTCMetaData( String inputStream ) throws ImageProcessingException, FileNotFoundException, IOException {
		return readIPTCMetaData( getInputStream( inputStream ) );
	}

	public static IStruct readIPTCMetaData( InputStream inputStream ) throws ImageProcessingException, FileNotFoundException, IOException {
		IStruct		iptcData	= new Struct();
		Metadata	metaData	= ImageMetadataReader.readMetadata( inputStream );

		Stream.of(
		    metaData.getFirstDirectoryOfType( IptcDirectory.class )
		)
		    .filter( dir -> dir != null )
		    .forEach( dir -> {
			    dir.getTags().stream().forEach( tag -> iptcData.put( tag.getTagName(), tag.getDescription() ) );
		    } );

		if ( iptcData.get( "Date/Time" ) == null ) {
			iptcData.put( "Date/Time", iptcData.get( "Date/Time Original" ) );
		}

		return iptcData;
	}

	public static Object getExifMetaDataTag( String path, String tagName ) throws ImageProcessingException, FileNotFoundException, IOException {
		return readExifMetaData( getInputStream( path ) ).get( tagName );
	}

	public static Object getIPTCMetaDataTag( String path, String tagName ) throws ImageProcessingException, FileNotFoundException, IOException {
		return readIPTCMetaData( getInputStream( path ) ).get( tagName );
	}

	private static InputStream getInputStream( String imageInput ) throws MalformedURLException, IOException {

		return getInputStream( URI.create( imageInput ) );
	}

	private static InputStream getInputStream( URI imageInput ) throws MalformedURLException, IOException {

		if ( imageInput.toString().toLowerCase().startsWith( "http" ) ) {

			return imageInput.toURL().openStream();

		}

		return new FileInputStream( imageInput.toString() );
	}
}
