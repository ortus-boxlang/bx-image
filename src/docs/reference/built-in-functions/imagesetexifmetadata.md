# ImageSetExifMetaData

Sets several EXIF tags from a struct on an image, a metadata handle or a file.

## Method Signature

```boxlang
any ImageSetExifMetaData(image, tags)
image.setExifMetaData(tags)
metadata.setExifMetaData(tags)
```

## Arguments

| Argument | Type   | Required | Description | Default |
| -------- | ------ | -------- | ----------- | ------- |
| image    | any    | true     | An image, a metadata handle, the name of a variable holding one, or a file path | |
| tags     | struct | true     | Tag names to values. A null value removes the tag | |

## Returns

The image or metadata handle, for chaining.

## Description

Equivalent to calling `ImageSetExifTag` for every key. See `ImageSetExifTag` for how images, handles and file paths are treated and which tag names are accepted.

## Examples

```boxlang
ImageSetExifMetaData("photo.jpg", {
    "Artist": "Jane Doe",
    "Copyright": "(c) 2026 akitogo"
});
```

## Related Functions

- ImageSetExifTag - Set one tag
- ImageReadMetadata - Edit metadata without decoding the image
