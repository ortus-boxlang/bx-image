# ImageRemoveMetadata

Removes metadata types from an image, a metadata handle or a file.

## Method Signature

```boxlang
any ImageRemoveMetadata(image [, types])
image.removeMetadata(types)
metadata.removeMetadata(types)
```

## Arguments

| Argument | Type | Required | Description | Default |
| -------- | ---- | -------- | ----------- | ------- |
| image    | any  | true     | An image, a metadata handle, the name of a variable holding one, or a file path | |
| types    | any  | false    | A list or array of types: `exif`, `iptc`, `xmp`, `icc`, `comment`, `photoshop`, or `all` | all |

## Returns

The image or metadata handle, for chaining.

## Description

- On a **file path**, the file is rewritten in place without re-encoding; `ImageStripMetadata` is the same with a destination option.
- On a **metadata handle**, the working copy is changed; call `write()` to save.
- On an **image object**, only tags set earlier with the setter functions are undone, because an image's `write()` never carries source metadata in the first place.

## Examples

```boxlang
ImageRemoveMetadata("photo.jpg", "xmp");
ImageRemoveMetadata("photo.jpg", ["exif", "photoshop"]);
ImageReadMetadata("photo.jpg").removeMetadata("iptc").write("no-iptc.jpg");
```

## Related Functions

- ImageStripMetadata - Remove metadata from a file into a copy or in place
- ImageReadMetadata - Edit metadata without decoding the image
