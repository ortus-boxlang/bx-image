# ImageSetIPTCTag

Sets an IPTC tag on an image, a metadata handle or a file.

## Method Signature

```boxlang
any ImageSetIPTCTag(image, tagName, value)
image.setIPTCTag(tagName, value)
metadata.setIPTCTag(tagName, value)
```

## Arguments

| Argument | Type   | Required | Description | Default |
| -------- | ------ | -------- | ----------- | ------- |
| image    | any    | true     | An image, a metadata handle from `ImageReadMetadata()`, the name of a variable holding one, or a file path | |
| tagName  | string | true     | The IPTC tag, e.g. `Copyright Notice`, `By-line`, `Caption/Abstract`, `Keywords`, `City`; repeatable tags take an array | |
| value    | any    | false    | String, number, date or array. Null removes the tag | |

## Returns

The image or metadata handle, for chaining.

## Description

- On an **image**, the tag is applied to the encoded bytes when the image is written (`write()`, `ImageWriteBase64()`, `toByteArray()`). The pixels are not touched. A plain `write()` without setters produces the same file as before, and source metadata is not carried through pixel edits.
- On a **metadata handle**, the working copy is changed; call `write()` on the handle to save.
- On a **file path**, the file is rewritten in place without re-encoding.

Tag names accept the display names the getters return and the specification names. Setting a tag to `null` removes it.

## Examples

```boxlang
ImageSetIPTCTag("photo.jpg", "Keywords", ["garden", "spring"]);
ImageSetIPTCTag("photo.jpg", "Copyright Notice", "(c) 2026 akitogo");
imageRead("photo.jpg").setIPTCTag("By-line", "Jane Doe").write("out.jpg");
```

## Related Functions

- ImageSetIPTCMetaData - Set several IPTC tags from a struct
- ImageGetIPTCTag - Read an IPTC tag
- ImageReadMetadata - Edit metadata without decoding the image
