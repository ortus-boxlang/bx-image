package ortus.boxlang.modules.image.bifs;

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

public class ImageGetExifMetaDataTest {

	static BoxRuntime	instance;
	IBoxContext			context;
	IScope				variables;
	static Key			result	= new Key( "result" );

	@BeforeAll
	public static void setUp() {
		instance = BoxRuntime.getInstance( true );
	}

	@BeforeEach
	public void setupEach() {
		context		= new ScriptingRequestBoxContext( instance.getRuntimeContext() );
		variables	= context.getScopeNearby( VariablesScope.name );
	}

	@DisplayName( "It should return a struct" )
	@Test
	public void testReturnsAStruct() throws IOException {
		instance.executeSource(
		    """
		                      result = ImageGetExifMetaData( "src/test/resources/test-images/exif-test.jpg" );
		    // result = ImageGetExifMetaData( result );
		                      """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );
	}

	@DisplayName( "It should contain various EXIF meta data tags" )
	@Test
	public void testGetsExifTags() throws IOException {
		instance.executeSource(
		    """
		    result = ImageGetExifMetaData( "src/test/resources/test-images/exif-test.jpg" );
		    """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );

		IStruct exifData = variables.getAsStruct( result );
		assertThat( exifData.get( "Brightness Value" ) ).isEqualTo( "4.67" );
		assertThat( exifData.get( "Aperture Value" ) ).isEqualTo( "f/1.9" );
		assertThat( exifData.get( "Aperture Value" ) ).isEqualTo( "f/1.9" );
		assertThat( exifData.get( "Brightness Value" ) ).isEqualTo( "4.67" );
		assertThat( exifData.get( "Color Space" ) ).isEqualTo( "sRGB" );
		assertThat( exifData.get( "Components Configuration" ) ).isEqualTo( "YCbCr" );
		assertThat( exifData.get( "Contrast" ) ).isEqualTo( "None" );
		assertThat( exifData.get( "Custom Rendered" ) ).isEqualTo( "Custom process" );
		assertThat( exifData.get( "Date/Time" ) ).isEqualTo( "2024:01:18 18:05:30" );
		assertThat( exifData.get( "Date/Time Digitized" ) ).isEqualTo( "2024:01:18 18:05:30" );
		assertThat( exifData.get( "Date/Time Original" ) ).isEqualTo( "2024:01:18 18:05:30" );
		assertThat( exifData.get( "Digital Zoom Ratio" ) ).isEqualTo( "Digital zoom not used" );
		assertThat( exifData.get( "Exif Image Height" ) ).isEqualTo( "4080 pixels" );
		assertThat( exifData.get( "Exif Image Width" ) ).isEqualTo( "3072 pixels" );
		assertThat( exifData.get( "Exif Version" ) ).isEqualTo( "2.32" );
		assertThat( exifData.get( "Exposure Bias Value" ) ).isEqualTo( "0 EV" );
		assertThat( exifData.get( "Exposure Mode" ) ).isEqualTo( "Auto exposure" );
		assertThat( exifData.get( "Exposure Program" ) ).isEqualTo( "Program normal" );
		assertThat( exifData.get( "Exposure Time" ) ).isEqualTo( "0.01 sec" );
		assertThat( exifData.get( "F-Number" ) ).isEqualTo( "f/1.9" );
		assertThat( exifData.get( "Flash" ) ).isEqualTo( "Flash did not fire" );
		assertThat( exifData.get( "FlashPix Version" ) ).isEqualTo( "1.00" );
		assertThat( exifData.get( "Focal Length" ) ).isEqualTo( "6.8 mm" );
		assertThat( exifData.get( "Focal Length 35" ) ).isEqualTo( "24 mm" );
		assertThat( exifData.get( "GPS Altitude" ) ).isEqualTo( "67.25 metres" );
		assertThat( exifData.get( "GPS Altitude Ref" ) ).isEqualTo( "Sea level" );
		assertThat( exifData.get( "GPS Date Stamp" ) ).isEqualTo( "2024:01:18" );
		assertThat( exifData.get( "GPS Img Direction" ) ).isEqualTo( "240 degrees" );
		assertThat( exifData.get( "GPS Img Direction Ref" ) ).isEqualTo( "Magnetic direction" );
		assertThat( exifData.get( "GPS Latitude" ) ).isEqualTo( "36° 42' 31.98\"" );
		assertThat( exifData.get( "GPS Latitude Ref" ) ).isEqualTo( "N" );
		assertThat( exifData.get( "GPS Longitude" ) ).isEqualTo( "-4° 27' 36.36\"" );
		assertThat( exifData.get( "GPS Longitude Ref" ) ).isEqualTo( "W" );
		assertThat( exifData.get( "GPS Time-Stamp" ) ).isEqualTo( "16:56:27.000 UTC" );
		assertThat( exifData.get( "GPS Version ID" ) ).isEqualTo( "2.200" );
		assertThat( exifData.get( "ISO Speed Ratings" ) ).isEqualTo( "42" );
		assertThat( exifData.get( "Interoperability Index" ) ).isEqualTo( "Recommended Exif Interoperability Rules (ExifR98)" );
		assertThat( exifData.get( "Interoperability Version" ) ).isEqualTo( "1.00" );
		assertThat( exifData.get( "Lens Make" ) ).isEqualTo( "Google" );
		assertThat( exifData.get( "Lens Model" ) ).isEqualTo( "Pixel 7 back camera 6.81mm f/1.85" );
		assertThat( exifData.get( "Make" ) ).isEqualTo( "Google" );
		assertThat( exifData.get( "Max Aperture Value" ) ).isEqualTo( "f/1.9" );
		assertThat( exifData.get( "Metering Mode" ) ).isEqualTo( "Center weighted average" );
		assertThat( exifData.get( "Model" ) ).isEqualTo( "Pixel 7" );
		assertThat( exifData.get( "Orientation" ) ).isEqualTo( "Top, left side (Horizontal / normal)" );
		assertThat( exifData.get( "Resolution Unit" ) ).isEqualTo( "Inch" );
		assertThat( exifData.get( "Saturation" ) ).isEqualTo( "None" );
		assertThat( exifData.get( "Scene Capture Type" ) ).isEqualTo( "Standard" );
		assertThat( exifData.get( "Scene Type" ) ).isEqualTo( "Directly photographed image" );
		assertThat( exifData.get( "Sensing Method" ) ).isEqualTo( "One-chip color area sensor" );
		assertThat( exifData.get( "Sharpness" ) ).isEqualTo( "None" );
		assertThat( exifData.get( "Shutter Speed Value" ) ).isEqualTo( "1/99 sec" );
		assertThat( exifData.get( "Software" ) ).isEqualTo( "HDR+ 1.0.585804401zd" );
		assertThat( exifData.get( "Sub-Sec Time" ) ).isEqualTo( "772" );
		assertThat( exifData.get( "Sub-Sec Time Digitized" ) ).isEqualTo( "772" );
		assertThat( exifData.get( "Sub-Sec Time Original" ) ).isEqualTo( "772" );
		assertThat( exifData.get( "Subject Distance" ) ).isEqualTo( "0.391 metres" );
		assertThat( exifData.get( "Subject Distance Range" ) ).isEqualTo( "Macro" );
		// assertThat( exifData.get( "Thumbnail Compression" ) ).isEqualTo( "JPEG (old-style)" );
		assertThat( exifData.get( "Thumbnail Length" ) ).isEqualTo( "5683 bytes" );
		assertThat( exifData.get( "Thumbnail Offset" ) ).isEqualTo( "1312 bytes" );
		// assertThat( exifData.get( "Unknown tag (0x9010)" ) ).isEqualTo( "+01:00" );
		// assertThat( exifData.get( "Unknown tag (0x9011)" ) ).isEqualTo( "+01:00" );
		// assertThat( exifData.get( "Unknown tag (0x9012)" ) ).isEqualTo( "+01:00" );
		assertThat( exifData.get( "Unknown tag (0xa460)" ) ).isEqualTo( "3" );
		assertThat( exifData.get( "White Balance Mode" ) ).isEqualTo( "Auto white balance" );
		assertThat( exifData.get( "X Resolution" ) ).isEqualTo( "72 dots per inch" );
		assertThat( exifData.get( "Y Resolution" ) ).isEqualTo( "72 dots per inch" );
		assertThat( exifData.get( "YCbCr Positioning" ) ).isEqualTo( "Center of pixel array" );
	}

	@DisplayName( "It should image data directly from a BoxImage" )
	@Test
	public void testGetsExifTagsFromBoxImage() throws IOException {
		instance.executeSource(
		    """

		    result = ImageGetExifMetaData( ImageRead( "src/test/resources/test-images/exif-test.jpg" ) );
		    """,
		    context );

		assertThat( variables.get( result ) ).isInstanceOf( IStruct.class );

		IStruct exifData = variables.getAsStruct( result );
		assertThat( exifData.get( "Brightness Value" ) ).isEqualTo( "4.67" );
		assertThat( exifData.get( "Aperture Value" ) ).isEqualTo( "f/1.9" );
		assertThat( exifData.get( "Aperture Value" ) ).isEqualTo( "f/1.9" );
		assertThat( exifData.get( "Brightness Value" ) ).isEqualTo( "4.67" );
		assertThat( exifData.get( "Color Space" ) ).isEqualTo( "sRGB" );
		assertThat( exifData.get( "Components Configuration" ) ).isEqualTo( "YCbCr" );
		assertThat( exifData.get( "Contrast" ) ).isEqualTo( "None" );
		assertThat( exifData.get( "Custom Rendered" ) ).isEqualTo( "Custom process" );
		assertThat( exifData.get( "Date/Time" ) ).isEqualTo( "2024:01:18 18:05:30" );
		assertThat( exifData.get( "Date/Time Digitized" ) ).isEqualTo( "2024:01:18 18:05:30" );
		assertThat( exifData.get( "Date/Time Original" ) ).isEqualTo( "2024:01:18 18:05:30" );
		assertThat( exifData.get( "Digital Zoom Ratio" ) ).isEqualTo( "Digital zoom not used" );
		assertThat( exifData.get( "Exif Image Height" ) ).isEqualTo( "4080 pixels" );
		assertThat( exifData.get( "Exif Image Width" ) ).isEqualTo( "3072 pixels" );
		assertThat( exifData.get( "Exif Version" ) ).isEqualTo( "2.32" );
		assertThat( exifData.get( "Exposure Bias Value" ) ).isEqualTo( "0 EV" );
		assertThat( exifData.get( "Exposure Mode" ) ).isEqualTo( "Auto exposure" );
		assertThat( exifData.get( "Exposure Program" ) ).isEqualTo( "Program normal" );
		assertThat( exifData.get( "Exposure Time" ) ).isEqualTo( "0.01 sec" );
		assertThat( exifData.get( "F-Number" ) ).isEqualTo( "f/1.9" );
		assertThat( exifData.get( "Flash" ) ).isEqualTo( "Flash did not fire" );
		assertThat( exifData.get( "FlashPix Version" ) ).isEqualTo( "1.00" );
		assertThat( exifData.get( "Focal Length" ) ).isEqualTo( "6.8 mm" );
		assertThat( exifData.get( "Focal Length 35" ) ).isEqualTo( "24 mm" );
		assertThat( exifData.get( "GPS Altitude" ) ).isEqualTo( "67.25 metres" );
		assertThat( exifData.get( "GPS Altitude Ref" ) ).isEqualTo( "Sea level" );
		assertThat( exifData.get( "GPS Date Stamp" ) ).isEqualTo( "2024:01:18" );
		assertThat( exifData.get( "GPS Img Direction" ) ).isEqualTo( "240 degrees" );
		assertThat( exifData.get( "GPS Img Direction Ref" ) ).isEqualTo( "Magnetic direction" );
		assertThat( exifData.get( "GPS Latitude" ) ).isEqualTo( "36° 42' 31.98\"" );
		assertThat( exifData.get( "GPS Latitude Ref" ) ).isEqualTo( "N" );
		assertThat( exifData.get( "GPS Longitude" ) ).isEqualTo( "-4° 27' 36.36\"" );
		assertThat( exifData.get( "GPS Longitude Ref" ) ).isEqualTo( "W" );
		assertThat( exifData.get( "GPS Time-Stamp" ) ).isEqualTo( "16:56:27.000 UTC" );
		assertThat( exifData.get( "GPS Version ID" ) ).isEqualTo( "2.200" );
		assertThat( exifData.get( "ISO Speed Ratings" ) ).isEqualTo( "42" );
		assertThat( exifData.get( "Interoperability Index" ) ).isEqualTo( "Recommended Exif Interoperability Rules (ExifR98)" );
		assertThat( exifData.get( "Interoperability Version" ) ).isEqualTo( "1.00" );
		assertThat( exifData.get( "Lens Make" ) ).isEqualTo( "Google" );
		assertThat( exifData.get( "Lens Model" ) ).isEqualTo( "Pixel 7 back camera 6.81mm f/1.85" );
		assertThat( exifData.get( "Make" ) ).isEqualTo( "Google" );
		assertThat( exifData.get( "Max Aperture Value" ) ).isEqualTo( "f/1.9" );
		assertThat( exifData.get( "Metering Mode" ) ).isEqualTo( "Center weighted average" );
		assertThat( exifData.get( "Model" ) ).isEqualTo( "Pixel 7" );
		assertThat( exifData.get( "Orientation" ) ).isEqualTo( "Top, left side (Horizontal / normal)" );
		assertThat( exifData.get( "Resolution Unit" ) ).isEqualTo( "Inch" );
		assertThat( exifData.get( "Saturation" ) ).isEqualTo( "None" );
		assertThat( exifData.get( "Scene Capture Type" ) ).isEqualTo( "Standard" );
		assertThat( exifData.get( "Scene Type" ) ).isEqualTo( "Directly photographed image" );
		assertThat( exifData.get( "Sensing Method" ) ).isEqualTo( "One-chip color area sensor" );
		assertThat( exifData.get( "Sharpness" ) ).isEqualTo( "None" );
		assertThat( exifData.get( "Shutter Speed Value" ) ).isEqualTo( "1/99 sec" );
		assertThat( exifData.get( "Software" ) ).isEqualTo( "HDR+ 1.0.585804401zd" );
		assertThat( exifData.get( "Sub-Sec Time" ) ).isEqualTo( "772" );
		assertThat( exifData.get( "Sub-Sec Time Digitized" ) ).isEqualTo( "772" );
		assertThat( exifData.get( "Sub-Sec Time Original" ) ).isEqualTo( "772" );
		assertThat( exifData.get( "Subject Distance" ) ).isEqualTo( "0.391 metres" );
		assertThat( exifData.get( "Subject Distance Range" ) ).isEqualTo( "Macro" );
		// assertThat( exifData.get( "Thumbnail Compression" ) ).isEqualTo( "JPEG (old-style)" );
		assertThat( exifData.get( "Thumbnail Length" ) ).isEqualTo( "5683 bytes" );
		assertThat( exifData.get( "Thumbnail Offset" ) ).isEqualTo( "1312 bytes" );
		// assertThat( exifData.get( "Unknown tag (0x9010)" ) ).isEqualTo( "+01:00" );
		// assertThat( exifData.get( "Unknown tag (0x9011)" ) ).isEqualTo( "+01:00" );
		// assertThat( exifData.get( "Unknown tag (0x9012)" ) ).isEqualTo( "+01:00" );
		assertThat( exifData.get( "Unknown tag (0xa460)" ) ).isEqualTo( "3" );
		assertThat( exifData.get( "White Balance Mode" ) ).isEqualTo( "Auto white balance" );
		assertThat( exifData.get( "X Resolution" ) ).isEqualTo( "72 dots per inch" );
		assertThat( exifData.get( "Y Resolution" ) ).isEqualTo( "72 dots per inch" );
		assertThat( exifData.get( "YCbCr Positioning" ) ).isEqualTo( "Center of pixel array" );
	}

}
