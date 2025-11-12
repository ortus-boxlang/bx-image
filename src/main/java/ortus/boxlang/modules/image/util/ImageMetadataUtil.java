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
package ortus.boxlang.modules.image.util;

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

/**
 * Utility class for extracting and reading image metadata from various image sources.
 * This class leverages the Drew Noakes metadata-extractor library to parse EXIF and IPTC
 * metadata from image files and streams.
 *
 * <p>
 * EXIF (Exchangeable Image File Format) metadata contains information about the camera settings,
 * date/time, GPS location, and other technical details embedded in the image by the camera or
 * editing software.
 * </p>
 *
 * <p>
 * IPTC (International Press Telecommunications Council) metadata contains descriptive information
 * such as captions, keywords, credits, and copyright information typically added by photo management
 * software or photographers.
 * </p>
 *
 * <p>
 * All methods support reading from both local file paths and HTTP/HTTPS URLs.
 * </p>
 *
 * @see <a href="https://github.com/drewnoakes/metadata-extractor">Drew Noakes metadata-extractor</a>
 */
public class ImageMetadataUtil {

	/**
	 * Reads EXIF metadata from an image file or URL specified by a string path.
	 * This is a convenience method that converts the string path to an InputStream
	 * before extracting metadata.
	 *
	 * @param inputStream The file path or URL to the image as a string.
	 *                    Supports local file paths and HTTP/HTTPS URLs.
	 *
	 * @return An IStruct containing all EXIF metadata tags as key-value pairs.
	 *         Tag names are the keys and their descriptions are the values.
	 *         If "Date/Time" is not present, it will be set from "Date/Time Original" if available.
	 *
	 * @throws ImageProcessingException If the image format is not supported or the metadata cannot be read
	 * @throws FileNotFoundException    If the specified file path does not exist
	 * @throws IOException              If an I/O error occurs while reading the image
	 */
	public static IStruct readExifMetaData( String inputStream ) throws ImageProcessingException, FileNotFoundException, IOException {
		return readExifMetaData( getInputStream( inputStream ) );
	}

	/**
	 * Reads EXIF metadata from an image InputStream.
	 * This method extracts EXIF data from multiple directory types including:
	 * <ul>
	 * <li>ExifImageDirectory - Core image EXIF data</li>
	 * <li>ExifInteropDirectory - Interoperability data</li>
	 * <li>ExifIFD0Directory - Primary image metadata</li>
	 * <li>ExifThumbnailDirectory - Thumbnail metadata</li>
	 * <li>ExifSubIFDDirectory - Additional camera and capture settings</li>
	 * <li>GpsDirectory - GPS location data</li>
	 * <li>IptcDirectory - IPTC tags (when present in EXIF)</li>
	 * </ul>
	 *
	 * @param inputStream An InputStream containing the image data to read metadata from.
	 *                    The stream should be positioned at the beginning of the image data.
	 *
	 * @return An IStruct containing all EXIF metadata tags as key-value pairs.
	 *         Tag names are the keys and their descriptions are the values.
	 *         If "Date/Time" is not present, it will be set from "Date/Time Original" if available.
	 *
	 * @throws ImageProcessingException If the image format is not supported or the metadata cannot be read
	 * @throws FileNotFoundException    If the underlying source cannot be found
	 * @throws IOException              If an I/O error occurs while reading the image
	 */
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

	/**
	 * Reads IPTC metadata from an image file or URL specified by a string path.
	 * This is a convenience method that converts the string path to an InputStream
	 * before extracting metadata.
	 *
	 * @param inputStream The file path or URL to the image as a string.
	 *                    Supports local file paths and HTTP/HTTPS URLs.
	 *
	 * @return An IStruct containing all IPTC metadata tags as key-value pairs.
	 *         Tag names are the keys and their descriptions are the values.
	 *         If "Date/Time" is not present, it will be set from "Date/Time Original" if available.
	 *
	 * @throws ImageProcessingException If the image format is not supported or the metadata cannot be read
	 * @throws FileNotFoundException    If the specified file path does not exist
	 * @throws IOException              If an I/O error occurs while reading the image
	 */
	public static IStruct readIPTCMetaData( String inputStream ) throws ImageProcessingException, FileNotFoundException, IOException {
		return readIPTCMetaData( getInputStream( inputStream ) );
	}

	/**
	 * Reads IPTC metadata from an image InputStream.
	 * IPTC metadata typically includes descriptive information such as:
	 * <ul>
	 * <li>Caption/Description</li>
	 * <li>Keywords</li>
	 * <li>Credit/Copyright</li>
	 * <li>Author/Creator</li>
	 * <li>Location information</li>
	 * <li>Source and usage rights</li>
	 * </ul>
	 *
	 * @param inputStream An InputStream containing the image data to read metadata from.
	 *                    The stream should be positioned at the beginning of the image data.
	 *
	 * @return An IStruct containing all IPTC metadata tags as key-value pairs.
	 *         Tag names are the keys and their descriptions are the values.
	 *         If "Date/Time" is not present, it will be set from "Date/Time Original" if available.
	 *
	 * @throws ImageProcessingException If the image format is not supported or the metadata cannot be read
	 * @throws FileNotFoundException    If the underlying source cannot be found
	 * @throws IOException              If an I/O error occurs while reading the image
	 */
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

	/**
	 * Retrieves a specific EXIF metadata tag value from an image.
	 * This is a convenience method that reads all EXIF metadata and returns
	 * only the requested tag value.
	 *
	 * @param path    The file path or URL to the image as a string.
	 *                Supports local file paths and HTTP/HTTPS URLs.
	 * @param tagName The name of the EXIF tag to retrieve (e.g., "Date/Time", "Image Width",
	 *                "Camera Model", "GPS Latitude"). Tag names are case-sensitive.
	 *
	 * @return The value/description of the requested EXIF tag, or null if the tag is not present.
	 *
	 * @throws ImageProcessingException If the image format is not supported or the metadata cannot be read
	 * @throws FileNotFoundException    If the specified file path does not exist
	 * @throws IOException              If an I/O error occurs while reading the image
	 */
	public static Object getExifMetaDataTag( String path, String tagName ) throws ImageProcessingException, FileNotFoundException, IOException {
		return readExifMetaData( getInputStream( path ) ).get( tagName );
	}

	/**
	 * Retrieves a specific IPTC metadata tag value from an image.
	 * This is a convenience method that reads all IPTC metadata and returns
	 * only the requested tag value.
	 *
	 * @param path    The file path or URL to the image as a string.
	 *                Supports local file paths and HTTP/HTTPS URLs.
	 * @param tagName The name of the IPTC tag to retrieve (e.g., "Caption/Abstract", "Keywords",
	 *                "Credit", "Copyright Notice", "By-line"). Tag names are case-sensitive.
	 *
	 * @return The value/description of the requested IPTC tag, or null if the tag is not present.
	 *
	 * @throws ImageProcessingException If the image format is not supported or the metadata cannot be read
	 * @throws FileNotFoundException    If the specified file path does not exist
	 * @throws IOException              If an I/O error occurs while reading the image
	 */
	public static Object getIPTCMetaDataTag( String path, String tagName ) throws ImageProcessingException, FileNotFoundException, IOException {
		return readIPTCMetaData( getInputStream( path ) ).get( tagName );
	}

	/**
	 * Converts a string path or URL to an InputStream.
	 * This is a convenience method that creates a URI from the string and delegates
	 * to the URI-based getInputStream method.
	 *
	 * @param imageInput The file path or URL as a string
	 *
	 * @return An InputStream for reading the image data
	 *
	 * @throws MalformedURLException If the string represents a malformed URL
	 * @throws IOException           If an I/O error occurs while opening the stream
	 */
	private static InputStream getInputStream( String imageInput ) throws MalformedURLException, IOException {

		return getInputStream( URI.create( imageInput ) );
	}

	/**
	 * Converts a URI to an InputStream for reading image data.
	 * Automatically detects whether the URI represents a remote HTTP/HTTPS resource
	 * or a local file and opens the appropriate stream type.
	 *
	 * @param imageInput The URI pointing to the image. Can be:
	 *                   <ul>
	 *                   <li>An HTTP or HTTPS URL - Opens a network stream</li>
	 *                   <li>A local file path - Opens a FileInputStream</li>
	 *                   </ul>
	 *
	 * @return An InputStream for reading the image data
	 *
	 * @throws MalformedURLException If the URI represents a malformed URL
	 * @throws IOException           If an I/O error occurs while opening the stream
	 */
	private static InputStream getInputStream( URI imageInput ) throws MalformedURLException, IOException {

		if ( imageInput.toString().toLowerCase().startsWith( "http" ) ) {

			return imageInput.toURL().openStream();

		}

		return new FileInputStream( imageInput.toString() );
	}
}
