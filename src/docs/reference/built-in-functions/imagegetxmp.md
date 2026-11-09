# ImageGetXMP

Returns the XMP packet of an image, a metadata handle or a file as a string.

## Method Signature

```boxlang
string ImageGetXMP(image)
image.getXMP()
metadata.getXMP()
```

## Arguments

| Argument | Type | Required | Description | Default |
| -------- | ---- | -------- | ----------- | ------- |
| image    | any  | true     | An image, a metadata handle, the name of a variable holding one, or a file path | |

## Returns

The XMP packet as XML, or an empty string if there is none.

## Description

For a metadata handle or a file path this is the packet stored in the file. For an image object it is the packet set with `setXMP()`, because image objects do not retain their source file's XMP.

## Examples

```boxlang
xmp = ImageGetXMP("photo.jpg");
if (len(xmp)) {
    doc = xmlParse(xmp);
}
```

## Related Functions

- ImageSetXMP - Set the XMP packet
- ImageReadMetadata - Edit metadata without decoding the image
