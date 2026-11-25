# ImageSetIPTCMetaData

Sets several IPTC tags from a struct on an image, a metadata handle or a file.

## Method Signature

```boxlang
any ImageSetIPTCMetaData(image, tags)
image.setIPTCMetaData(tags)
metadata.setIPTCMetaData(tags)
```

## Arguments

| Argument | Type   | Required | Description | Default |
| -------- | ------ | -------- | ----------- | ------- |
| image    | any    | true     | An image, a metadata handle, the name of a variable holding one, or a file path | |
| tags     | struct | true     | Tag names to values. A null value removes the tag | |

## Returns

The image or metadata handle, for chaining.

## Description

Equivalent to calling `ImageSetIPTCTag` for every key. See `ImageSetIPTCTag` for how images, handles and file paths are treated and which tag names are accepted.

## Examples

```boxlang
ImageSetIPTCMetaData("photo.jpg", {
    "By-line": "Jane Doe",
    "Copyright Notice": "(c) 2026 akitogo"
});
```

## Related Functions

- ImageSetIPTCTag - Set one tag
- ImageReadMetadata - Edit metadata without decoding the image
