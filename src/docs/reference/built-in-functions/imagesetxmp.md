# ImageSetXMP

Sets the XMP packet of an image, a metadata handle or a file, replacing an existing one.

## Method Signature

```boxlang
any ImageSetXMP(image, xmp)
image.setXMP(xmp)
metadata.setXMP(xmp)
```

## Arguments

| Argument | Type   | Required | Description | Default |
| -------- | ------ | -------- | ----------- | ------- |
| image    | any    | true     | An image, a metadata handle, the name of a variable holding one, or a file path | |
| xmp      | string | false    | The XMP packet as XML, with or without the `<?xpacket?>` wrapper. Empty removes the packet | |

## Returns

The image or metadata handle, for chaining.

## Description

XMP is stored as is; the function only checks that it is well-formed XML and adds the packet wrapper the format requires. All formats the module writes (JPEG, TIFF, PNG, GIF, WebP) can hold XMP. See `ImageSetExifTag` for how images, handles and file paths are treated.

## Examples

```boxlang
xmp = "<x:xmpmeta xmlns:x='adobe:ns:meta/'><rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns##'>"
    & "<rdf:Description rdf:about='' xmlns:dc='http://purl.org/dc/elements/1.1/'>"
    & "<dc:creator><rdf:Seq><rdf:li>Jane Doe</rdf:li></rdf:Seq></dc:creator>"
    & "</rdf:Description></rdf:RDF></x:xmpmeta>";
ImageSetXMP("photo.jpg", xmp);
```

## Related Functions

- ImageGetXMP - Read the XMP packet
- ImageReadMetadata - Edit metadata without decoding the image
