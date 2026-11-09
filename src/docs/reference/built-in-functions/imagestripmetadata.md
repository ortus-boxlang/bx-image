# ImageStripMetadata

Removes all or some metadata from an image file without re-encoding it.

## Method Signature

```boxlang
metadata ImageStripMetadata(source [, destination [, types]])
```

## Arguments

| Argument    | Type   | Required | Description | Default |
| ----------- | ------ | -------- | ----------- | ------- |
| source      | any    | true     | A file path, or an image that was read from a file | |
| destination | string | false    | Where to write the stripped copy; omit or empty to rewrite the source in place | |
| types       | any    | false    | A list or array of types to remove: `exif`, `iptc`, `xmp`, `icc`, `comment`, `photoshop` | all |

## Returns

The metadata handle of the written file (see `ImageReadMetadata`).

## Description

The privacy one-liner for uploads. The image data is copied byte for byte, so the result is identical in quality and almost identical in size. Without `types` everything the format can hold is removed, including the Photoshop resource block and JPEG comments.

## Examples

```boxlang
// Everything, in place
ImageStripMetadata("upload.jpg");

// Everything, into a copy
ImageStripMetadata("upload.jpg", "public/upload.jpg");

// Only GPS-carrying EXIF and the Photoshop block, keep IPTC credits
ImageStripMetadata("upload.jpg", "", "exif,photoshop");
```

## Related Functions

- ImageReadMetadata - Read and edit metadata
- ImageRemoveMetadata - Remove types from an image, a handle or a file

## Notes

- Types the format cannot hold are ignored, so `ImageStripMetadata("logo.png")` never fails because PNG has no IPTC.
- A failure while writing leaves the original file untouched.
