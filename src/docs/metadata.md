# Image Metadata

Learn how to read, write and strip EXIF, IPTC and XMP metadata.

## Table of Contents

- [What is Metadata?](#what-is-metadata)
- [Reading EXIF Metadata](#reading-exif-metadata)
- [Reading IPTC Metadata](#reading-iptc-metadata)
- [Common EXIF Tags](#common-exif-tags)
- [Common IPTC Tags](#common-iptc-tags)
- [Writing Metadata](#writing-metadata)
- [Stripping Metadata](#stripping-metadata)
- [Practical Examples](#practical-examples)

## What is Metadata?

Image metadata is embedded information about the image and how it was created:

**EXIF (Exchangeable Image File Format):**

- Camera settings (ISO, aperture, shutter speed)
- Date/time photo was taken
- GPS coordinates
- Camera make and model
- Image dimensions and orientation

**IPTC (International Press Telecommunications Council):**

- Copyright information
- Creator/photographer name
- Keywords and categories
- Caption/description
- Headline and title

## Reading EXIF Metadata

### Get All EXIF Data

```js
img = ImageNew("photo.jpg");
exifData = img.getExifMetaData();

// Returns struct with all EXIF tags
writeDump(exifData);
```

**BIF Syntax:**

```js
img = ImageRead("photo.jpg");
exifData = ImageGetExifMetaData(img);
```

### Get Specific EXIF Tag

```js
img = ImageNew("photo.jpg");

// Get camera make
make = img.getExifTag("Make");

// Get date taken
dateTaken = img.getExifTag("DateTime");

// Get ISO speed
iso = img.getExifTag("ISO Speed Ratings");
```

**BIF Syntax:**

```js
make = ImageGetExifTag(img, "Make");
```

### Check for EXIF Data

```js
img = ImageNew("photo.jpg");
exifData = img.getExifMetaData();

if (structCount(exifData) > 0) {
    // Image has EXIF data
    writeln("Photo taken with: " & exifData["Make"]);
} else {
    // No EXIF data
    writeln("No camera information available");
}
```

## Reading IPTC Metadata

### Get All IPTC Data

```js
img = ImageNew("photo.jpg");
iptcData = img.getIPTCMetaData();

// Returns struct with all IPTC tags
writeDump(iptcData);
```

**BIF Syntax:**

```js
img = ImageRead("photo.jpg");
iptcData = ImageGetIPTCMetadata(img);
```

### Get Specific IPTC Tag

```js
img = ImageNew("photo.jpg");

// Get copyright
copyright = img.getIPTCTag("Copyright Notice");

// Get keywords
keywords = img.getIPTCTag("Keywords");

// Get caption
caption = img.getIPTCTag("Caption/Abstract");
```

**BIF Syntax:**

```js
copyright = ImageGetIPTCTag(img, "Copyright Notice");
```

## Common EXIF Tags

### Camera Information

```js
img = ImageNew("photo.jpg");

// Camera details
make = img.getExifTag("Make");              // "Canon"
model = img.getExifTag("Model");            // "EOS 5D Mark IV"
software = img.getExifTag("Software");      // "Adobe Photoshop"
```

### Camera Settings

```js
// Exposure settings
iso = img.getExifTag("ISO Speed Ratings");       // 400
aperture = img.getExifTag("F-Number");           // 2.8
shutterSpeed = img.getExifTag("Exposure Time");  // "1/500"
exposureProgram = img.getExifTag("Exposure Program"); // "Manual"

// Lens information
focalLength = img.getExifTag("Focal Length");    // "50mm"
lens = img.getExifTag("Lens Model");             // "EF50mm f/1.8 STM"
```

### Date and Time

```js
// When photo was taken
dateTime = img.getExifTag("DateTime");                   // "2025:01:15 14:30:22"
dateTimeOriginal = img.getExifTag("Date/Time Original"); // Original capture time
dateTimeDigitized = img.getExifTag("Date/Time Digitized"); // When digitized
```

### GPS Information

```js
// Location data
latitude = img.getExifTag("GPS Latitude");       // [40, 44, 54.36]
longitude = img.getExifTag("GPS Longitude");     // [-73, 59, 10.56]
altitude = img.getExifTag("GPS Altitude");       // "10.5m"
```

### Image Information

```js
// Image properties
width = img.getExifTag("Image Width");           // 6000
height = img.getExifTag("Image Height");         // 4000
orientation = img.getExifTag("Orientation");     // "Rotate 90 CW"
resolution = img.getExifTag("Resolution Unit");  // "Inches"
xResolution = img.getExifTag("X Resolution");    // 300
yResolution = img.getExifTag("Y Resolution");    // 300
```

### Common EXIF Tag Reference

| Tag Name | Description | Example Value |
|----------|-------------|---------------|
| `Make` | Camera manufacturer | "Canon", "Nikon" |
| `Model` | Camera model | "EOS 5D Mark IV" |
| `DateTime` | Last modification time | "2025:01:15 14:30:22" |
| `Date/Time Original` | Original capture time | "2025:01:15 14:30:22" |
| `ISO Speed Ratings` | ISO sensitivity | 400, 800, 1600 |
| `F-Number` | Aperture value | 2.8, 5.6, 16 |
| `Exposure Time` | Shutter speed | "1/500", "1/60" |
| `Focal Length` | Lens focal length | "50mm", "24mm" |
| `Flash` | Flash status | "Flash fired", "No flash" |
| `GPS Latitude` | GPS latitude | [40, 44, 54.36] |
| `GPS Longitude` | GPS longitude | [-73, 59, 10.56] |
| `Orientation` | Image orientation | "Normal", "Rotate 90 CW" |

## Common IPTC Tags

### Copyright Information

```js
img = ImageNew("photo.jpg");

copyright = img.getIPTCTag("Copyright Notice");
creator = img.getIPTCTag("By-line");  // Photographer name
credit = img.getIPTCTag("Credit");
source = img.getIPTCTag("Source");
```

### Descriptive Information

```js
// Content description
headline = img.getIPTCTag("Headline");
caption = img.getIPTCTag("Caption/Abstract");
keywords = img.getIPTCTag("Keywords");
category = img.getIPTCTag("Category");
```

### Editorial Information

```js
// Publication details
city = img.getIPTCTag("City");
state = img.getIPTCTag("Province/State");
country = img.getIPTCTag("Country/Primary Location Name");
instructions = img.getIPTCTag("Special Instructions");
```

### Common IPTC Tag Reference

| Tag Name | Description | Example Value |
|----------|-------------|---------------|
| `Copyright Notice` | Copyright information | "© 2025 John Doe" |
| `By-line` | Creator/photographer | "John Doe" |
| `Headline` | Image headline | "Sunset Over Mountains" |
| `Caption/Abstract` | Image description | "Beautiful sunset..." |
| `Keywords` | Search keywords | ["landscape", "sunset"] |
| `Category` | Image category | "Nature" |
| `Credit` | Credit line | "Photo by John Doe" |
| `Source` | Image source | "Photography Studio" |
| `City` | City of origin | "New York" |
| `Country/Primary Location Name` | Country | "United States" |

## Writing Metadata

Metadata can be written in two ways: directly into an existing file, without decoding or re-encoding the image, or onto an image object that is being edited anyway.

### Edit a File Without Decoding It

`ImageReadMetadata()` returns a metadata handle. Setters change a working copy, `write()` rewrites the file. The pixels are copied byte for byte, so this is lossless and fast even for large photos.

```js
meta = ImageReadMetadata( "photo.jpg" );

// Read
writeDump( meta.getExif() );            // same struct as ImageGetExifMetaData()
writeDump( meta.getIPTC() );
xmp = meta.getXMP();                     // XMP packet as string, "" if none
types = meta.getTypes();                 // e.g. [ "exif", "iptc", "xmp" ]

// Write
meta.setExifTag( "Copyright", "(c) 2026 akitogo" )
    .setExifTag( "Date/Time Original", now() )
    .setExifTag( "GPSLatitude", 52.503472 )        // decimal degrees, sets GPS Latitude Ref too
    .setExifTag( "GPSLongitude", 13.41 )
    .setIPTCTag( "Keywords", [ "garden", "spring" ] )
    .setIPTCTag( "Copyright Notice", "(c) 2026 akitogo" )
    .setXMP( xmpString )
    .write();                                      // in place, atomic

// Or write a copy and leave the original alone
meta.write( "photo-tagged.jpg" );

// Or get the bytes
bytes = meta.toBinary();
```

Tag names accept the display names the getters return (`Date/Time Original`, `Copyright Notice`, `By-line`), the specification names (`DateTimeOriginal`, `CopyrightNotice`) and common aliases (`ISO`, `Copyright`, `Caption`, `Creator`). Values may be strings, numbers, dates or arrays (for repeatable IPTC tags such as `Keywords`). Setting a tag to `null` removes it; `removeExifTag()` and `removeIPTCTag()` do the same by name.

Several tags at once:

```js
meta.setExifMetaData( { "Artist": "Jane Doe", "Copyright": "(c) 2026" } )
    .setIPTCMetaData( { "By-line": "Jane Doe", "Keywords": [ "a", "b" ] } )
    .write();
```

The same functions work on a file path directly and rewrite the file in place:

```js
ImageSetExifTag( "photo.jpg", "Copyright", "(c) 2026 akitogo" );
ImageSetIPTCTag( "photo.jpg", "Keywords", [ "garden", "spring" ] );
ImageSetXMP( "photo.jpg", xmpString );
```

### Set Metadata on an Image You Are Editing

The setters are also member functions of an image. The metadata is applied to the encoded bytes when the image is written, in `write()`, `ImageWriteBase64()` and `toByteArray()`.

```js
imageRead( "photo.jpg" )
    .resize( 800, 600 )
    .setExifTag( "Artist", "Jane Doe" )
    .setIPTCTag( "Copyright Notice", "(c) 2026" )
    .write( "thumb.jpg" );
```

A plain `write()` without setters produces the same file as before. Source metadata is **not** carried through pixel edits: `imageRead( "photo.jpg" ).resize( 800, 600 ).write( "thumb.jpg" )` still drops the camera's EXIF, as it always did. Set the tags you want to keep explicitly, or edit the metadata of the finished file with `ImageReadMetadata()`.

### What Each Format Can Hold

| Format | EXIF | IPTC | XMP | ICC profile |
|--------|------|------|-----|-------------|
| JPEG   | yes  | yes  | yes | yes |
| TIFF   | yes  | yes  | yes | yes |
| WebP   | yes  | no   | yes | yes |
| PNG    | no   | no   | yes | yes |
| GIF    | no   | no   | yes | no  |

An unsupported combination, such as EXIF in a PNG, throws a clear error instead of silently writing nothing. `meta.canWrite( "exif" )` tells you in advance.

## Stripping Metadata

`ImageStripMetadata()` removes metadata from a file without re-encoding it. The one-liner for uploads:

```js
ImageStripMetadata( "upload.jpg" );                         // everything, in place
ImageStripMetadata( "upload.jpg", "public/upload.jpg" );    // everything, into a copy
ImageStripMetadata( "upload.jpg", "", "exif,photoshop" );   // only some types, in place
```

Types are `exif`, `iptc`, `xmp`, `icc`, `comment` and `photoshop` (the Photoshop resource block). On a metadata handle the same is `meta.remove( "exif" )` or `meta.removeAll()` followed by `write()`; `ImageRemoveMetadata( "photo.jpg", "xmp" )` does it in one call.

## Practical Examples

### Display Photo Information

```js
img = ImageNew("photo.jpg");
exif = img.getExifMetaData();

if (structCount(exif) > 0) {
    writeOutput("
        <h3>Photo Information</h3>
        <p><strong>Camera:</strong> #exif['Make']# #exif['Model']#</p>
        <p><strong>Date:</strong> #exif['Date/Time Original']#</p>
        <p><strong>Settings:</strong> ISO #exif['ISO Speed Ratings']#,
           f/#exif['F-Number']#, #exif['Exposure Time']#s</p>
        <p><strong>Lens:</strong> #exif['Focal Length']#</p>
    ");
}
```

### Check Image Copyright

```js
img = ImageNew("photo.jpg");
iptc = img.getIPTCMetaData();

if (structKeyExists(iptc, "Copyright Notice")) {
    copyright = iptc["Copyright Notice"];
    writeln("Copyright: " & copyright);
} else {
    writeln("No copyright information found");
}
```

### Extract GPS Coordinates

```js
img = ImageNew("photo.jpg");
exif = img.getExifMetaData();

if (structKeyExists(exif, "GPS Latitude") && structKeyExists(exif, "GPS Longitude")) {
    lat = exif["GPS Latitude"];
    lon = exif["GPS Longitude"];

    // Convert to decimal degrees
    latDecimal = convertGPSToDecimal(lat);
    lonDecimal = convertGPSToDecimal(lon);

    writeln("Location: #latDecimal#, #lonDecimal#");
    writeln("View on map: https://maps.google.com?q=#latDecimal#,#lonDecimal#");
} else {
    writeln("No GPS data available");
}
```

### Generate Image Report

```js
img = ImageNew("photo.jpg");
info = img.info();
exif = img.getExifMetaData();
iptc = img.getIPTCMetaData();

report = {
    filename: "photo.jpg",
    dimensions: "#info.width# x #info.height#",
    fileSize: info.source.size,
    camera: structKeyExists(exif, "Make") ? "#exif.Make# #exif.Model#" : "Unknown",
    dateTaken: structKeyExists(exif, "Date/Time Original") ? exif["Date/Time Original"] : "Unknown",
    copyright: structKeyExists(iptc, "Copyright Notice") ? iptc["Copyright Notice"] : "None",
    keywords: structKeyExists(iptc, "Keywords") ? iptc.Keywords : []
};

writeDump(report);
```

### Filter Images by Camera

```js
// Find all images taken with specific camera
files = directoryList("photos", false, "path", "*.jpg");
canonPhotos = [];

for (file in files) {
    img = ImageNew(file);
    exif = img.getExifMetaData();

    if (structKeyExists(exif, "Make") && exif.Make == "Canon") {
        arrayAppend(canonPhotos, file);
    }
}

writeln("Found #arrayLen(canonPhotos)# Canon photos");
```

### Sort Images by Date

```js
// Sort images by capture date
files = directoryList("photos", false, "path", "*.jpg");
photosByDate = [];

for (file in files) {
    img = ImageNew(file);
    exif = img.getExifMetaData();

    if (structKeyExists(exif, "Date/Time Original")) {
        arrayAppend(photosByDate, {
            file: file,
            date: exif["Date/Time Original"]
        });
    }
}

// Sort by date
arraySort(photosByDate, function(a, b) {
    return compare(a.date, b.date);
});
```

### Add Metadata to Thumbnails

```js
// Create thumbnails with metadata overlays
files = directoryList("photos", false, "path", "*.jpg");

for (file in files) {
    original = ImageNew(file);
    exif = original.getExifMetaData();

    // Create thumbnail
    thumb = original.copy()
        .scaleToFit(400, 300);

    // Add metadata text
    if (structKeyExists(exif, "Date/Time Original")) {
        thumb.setDrawingColor("white")
            .setDrawingTransparency(80)
            .drawText(exif["Date/Time Original"], 10, 290, {
                font: "Arial",
                size: 12
            });
    }

    filename = getFileFromPath(file);
    thumb.write("thumbnails/#filename#");
}
```

## Important Notes

1. **Not all images have metadata** - Check for existence before accessing
2. **EXIF data can be removed** - Privacy tools often strip EXIF
3. **Format support** - EXIF typically in JPEG/TIFF, not PNG/GIF
4. **GPS privacy** - Consider removing GPS data before publishing
5. **Tag names vary** - Different cameras may use different tag names

## Next Steps

- **[Utilities](utilities.md)** - Image properties and format support
- **[Advanced Examples](advanced-examples.md)** - Metadata-driven workflows
- **[Getting Started](getting-started.md)** - Basic image operations
