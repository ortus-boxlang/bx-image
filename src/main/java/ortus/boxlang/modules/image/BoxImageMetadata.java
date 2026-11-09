/**
 * [BoxLang]
 *
 * Copyright [2026] [Ortus Solutions, Corp]
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
package ortus.boxlang.modules.image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.iptc.IptcDirectory;

import ortus.boxlang.modules.image.util.ImageGuard;
import ortus.boxlang.modules.image.util.ImageMetadataUtil;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Array;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.Struct;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import pixy.image.ImageType;
import pixy.image.tiff.Tag;
import pixy.meta.MetadataType;
import pixy.meta.edit.MetadataEditor;
import pixy.meta.edit.TagNames;
import pixy.meta.iptc.IPTCApplicationTag;

/**
 * Edits the metadata (EXIF, IPTC, XMP, ICC profile) of an image without decoding or re-encoding
 * its pixels. Backed by the pixymeta library.
 * <p>
 * Two ways to get one:
 * <ul>
 * <li>{@link #read(String)} opens an image file or URL and reads its metadata. Setters change a
 * working copy, {@link #write()} / {@link #write(String)} / {@link #toBinary()} write the image
 * with the new metadata, image data copied byte for byte. This is what {@code imageReadMetadata()}
 * returns.</li>
 * <li>{@link #empty()} holds metadata to stamp onto an image that is encoded later. {@link BoxImage}
 * keeps one for the {@code setExifTag()} family and applies it to the bytes ImageIO produces in
 * {@code write()}, {@code toByteArray()} and {@code toBase64String()}.</li>
 * </ul>
 * Tag names accept the display names the module's getters return ({@code Date/Time Original},
 * {@code Copyright Notice}, {@code By-line}), the specification names ({@code DateTimeOriginal},
 * {@code CopyrightNotice}) and common aliases. Values may be strings, numbers, dates or arrays
 * (for repeatable IPTC tags such as Keywords). Setting a tag to null removes it.
 * <p>
 * Formats and what they can hold: JPEG and TIFF take EXIF, IPTC, XMP and ICC; WebP takes EXIF, XMP
 * and ICC; PNG takes XMP and ICC; GIF takes XMP. Anything else throws a clear error instead of
 * silently writing nothing.
 */
public class BoxImageMetadata {

	private static final String				TYPE_EXIF		= "exif";
	private static final String				TYPE_IPTC		= "iptc";
	private static final String				TYPE_XMP		= "xmp";
	private static final String				TYPE_ICC		= "icc";
	private static final String				TYPE_COMMENT	= "comment";
	private static final String				TYPE_PHOTOSHOP	= "photoshop";

	private static final ExifIFD0Directory	EXIF_NAMES		= new ExifIFD0Directory();
	private static final IptcDirectory		IPTC_NAMES		= new IptcDirectory();

	/** The pixymeta editor holding the working copy */
	private final MetadataEditor			editor;

	/** File path or URL the metadata was read from, or null */
	private final String					source;

	/** The source image, retained so edits can be written out; null for an empty editor */
	private final byte[]					sourceBytes;

	/** metadata-extractor structs for the current state, recomputed after a change */
	private IStruct							exifCache;
	private IStruct							iptcCache;

	private BoxImageMetadata( MetadataEditor editor, String source, byte[] sourceBytes ) {
		this.editor			= editor;
		this.source			= source;
		this.sourceBytes	= sourceBytes;
	}

	/**
	 * Reads the metadata of an image file or URL. The image is not decoded.
	 *
	 * @param source An absolute file path, a file: URI or an http(s) URL
	 *
	 * @return The metadata handle
	 *
	 * @throws BoxRuntimeException If the source cannot be read or is not a supported image
	 */
	public static BoxImageMetadata read( String source ) {
		try {
			URI		uri	= BoxImage.stringToURI( source );
			byte[]	data;
			try ( InputStream in = BoxImage.getInputStream( uri ) ) {
				data = ImageGuard.readBounded( in, ImageGuard.getMaxBytes(), source );
			}
			return fromBytes( data, uri.toString() );
		} catch ( BoxRuntimeException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to read image metadata from [" + source + "]: " + e.getMessage(), e );
		}
	}

	/**
	 * Reads the metadata of an in-memory image.
	 *
	 * @param data   The image bytes
	 * @param source A description of where the bytes came from, may be null
	 *
	 * @return The metadata handle
	 */
	public static BoxImageMetadata fromBytes( byte[] data, String source ) {
		try {
			return new BoxImageMetadata( MetadataEditor.read( data ), source, data );
		} catch ( IOException e ) {
			throw new BoxRuntimeException( "Unable to read image metadata" + ( source == null ? "" : " from [" + source + "]" ) + ": " + e.getMessage(), e );
		}
	}

	/**
	 * An editor without an image. Everything set on it is written when it is applied to an image.
	 *
	 * @return The metadata handle
	 */
	public static BoxImageMetadata empty() {
		return new BoxImageMetadata( MetadataEditor.create(), null, null );
	}

	// ---------------------------------------------------------------- information

	/**
	 * @return The file path or URL the metadata was read from, or null
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return The format of the source image (jpg, tiff, png, gif, webp), or null for an empty editor
	 */
	public String getFormat() {
		ImageType type = editor.getSourceType();
		return type == null ? null : type.getExtension();
	}

	/**
	 * @return The metadata types the image currently holds, e.g. ["exif", "iptc", "xmp"]
	 */
	public Array getTypes() {
		Array types = new Array();
		for ( MetadataType type : editor.getTypes() ) {
			String name = typeName( type );
			if ( name != null ) {
				types.add( name );
			}
		}
		return types;
	}

	/**
	 * @return True once a setter or removal has been called
	 */
	public boolean hasChanges() {
		return editor.hasChanges();
	}

	/**
	 * Whether the source image's format can hold a metadata type.
	 *
	 * @param type exif, iptc, xmp, icc or comment
	 *
	 * @return True if it can be written; false for an empty editor
	 */
	public boolean canWrite( String type ) {
		ImageType sourceType = editor.getSourceType();
		return sourceType != null && MetadataEditor.writableTypes( sourceType ).contains( parseType( type ) );
	}

	/**
	 * The pixymeta editor behind this handle, for Java callers that need the full API.
	 *
	 * @return The editor
	 */
	public MetadataEditor getEditor() {
		return editor;
	}

	// ---------------------------------------------------------------- getters

	/**
	 * All EXIF tags as a struct keyed by display name, the same keys and values
	 * {@code imageGetExifMetaData()} returns. Reflects pending changes.
	 *
	 * @return The EXIF struct, empty if there is none
	 */
	public IStruct getExif() {
		if ( exifCache == null ) {
			exifCache = sourceBytes != null ? readStruct( true ) : structFromEditorExif();
		}
		return exifCache;
	}

	/**
	 * All IPTC tags as a struct keyed by display name, the same keys and values
	 * {@code imageGetIPTCMetadata()} returns. Reflects pending changes.
	 *
	 * @return The IPTC struct, empty if there is none
	 */
	public IStruct getIPTC() {
		if ( iptcCache == null ) {
			iptcCache = sourceBytes != null ? readStruct( false ) : structFromEditorIptc();
		}
		return iptcCache;
	}

	/**
	 * One EXIF tag. The display name ({@code Date/Time Original}) and the specification name
	 * ({@code DateTimeOriginal}) both work.
	 *
	 * @param tagName The tag
	 *
	 * @return The value, or null if the tag is absent
	 */
	public Object getExifTag( String tagName ) {
		Object value = getExif().get( Key.of( tagName ) );
		if ( value != null ) {
			return value;
		}
		try {
			String formatted = editor.getExifTag( tagName );
			if ( formatted != null ) {
				return formatted;
			}
			Tag		tag			= TagNames.exif( tagName );
			String	displayName	= EXIF_NAMES.hasTagName( tag.getValue() & 0xffff ) ? EXIF_NAMES.getTagName( tag.getValue() & 0xffff ) : null;
			return displayName == null ? null : getExif().get( Key.of( displayName ) );
		} catch ( IllegalArgumentException e ) {
			return null;
		}
	}

	/**
	 * One IPTC tag. Repeatable tags with several values (Keywords) come back as an array.
	 *
	 * @param tagName The tag
	 *
	 * @return The value, or null if the tag is absent
	 */
	public Object getIPTCTag( String tagName ) {
		Object value = getIPTC().get( Key.of( tagName ) );
		if ( value != null ) {
			return value;
		}
		try {
			List<String> values = editor.getIptcTag( tagName );
			if ( values.isEmpty() ) {
				IPTCApplicationTag	tag			= TagNames.iptc( tagName );
				int					id			= 0x0200 | tag.getTag();
				String				displayName	= IPTC_NAMES.hasTagName( id ) ? IPTC_NAMES.getTagName( id ) : null;
				return displayName == null ? null : getIPTC().get( Key.of( displayName ) );
			}
			return values.size() == 1 ? values.get( 0 ) : Array.fromList( values );
		} catch ( IllegalArgumentException e ) {
			return null;
		}
	}

	/**
	 * @return The XMP packet as a string, or an empty string if there is none
	 */
	public String getXMP() {
		String xmp = editor.getXmp();
		return xmp == null ? "" : xmp;
	}

	/**
	 * @return The ICC profile bytes, or null if there is none
	 */
	public byte[] getICCProfile() {
		return editor.getIccProfile();
	}

	// ---------------------------------------------------------------- setters

	/**
	 * Sets an EXIF tag. Null removes it. Decimal GPS coordinates also set the N/S or E/W reference.
	 *
	 * @param tagName The tag, in any common spelling
	 * @param value   String, number, date or array
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata setExifTag( String tagName, Object value ) {
		return guarded( () -> editor.setExifTag( tagName, toJavaValue( value ) ), "EXIF tag [" + tagName + "]" );
	}

	/**
	 * Sets several EXIF tags from a struct of tag name to value.
	 *
	 * @param tags The tags
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata setExifMetaData( IStruct tags ) {
		for ( Map.Entry<Key, Object> entry : tags.entrySet() ) {
			setExifTag( entry.getKey().getName(), entry.getValue() );
		}
		return this;
	}

	/**
	 * Removes one EXIF tag.
	 *
	 * @param tagName The tag
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata removeExifTag( String tagName ) {
		return guarded( () -> editor.removeExifTag( tagName ), "EXIF tag [" + tagName + "]" );
	}

	/**
	 * Sets an IPTC tag, replacing its existing values. Null or an empty array removes it. Repeatable
	 * tags such as Keywords take an array.
	 *
	 * @param tagName The tag, in any common spelling
	 * @param value   String, number, date or array
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata setIPTCTag( String tagName, Object value ) {
		return guarded( () -> editor.setIptcTag( tagName, toJavaValue( value ) ), "IPTC tag [" + tagName + "]" );
	}

	/**
	 * Sets several IPTC tags from a struct of tag name to value.
	 *
	 * @param tags The tags
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata setIPTCMetaData( IStruct tags ) {
		for ( Map.Entry<Key, Object> entry : tags.entrySet() ) {
			setIPTCTag( entry.getKey().getName(), entry.getValue() );
		}
		return this;
	}

	/**
	 * Removes one IPTC tag.
	 *
	 * @param tagName The tag
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata removeIPTCTag( String tagName ) {
		return guarded( () -> editor.removeIptcTag( tagName ), "IPTC tag [" + tagName + "]" );
	}

	/**
	 * Sets the XMP packet, replacing an existing one. Null or empty removes it.
	 *
	 * @param xmp The XMP as XML
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata setXMP( String xmp ) {
		return guarded( () -> editor.setXmp( xmp ), "XMP" );
	}

	/**
	 * Sets the ICC profile, replacing an existing one. Null removes it.
	 *
	 * @param profile The profile bytes
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata setICCProfile( byte[] profile ) {
		return guarded( () -> editor.setIccProfile( profile ), "ICC profile" );
	}

	/**
	 * Removes whole metadata types.
	 *
	 * @param types Any of exif, iptc, xmp, icc, comment, photoshop, or all
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata remove( String... types ) {
		for ( String type : types ) {
			if ( "all".equalsIgnoreCase( type.trim() ) ) {
				return removeAll();
			}
			MetadataType parsed = parseType( type );
			guarded( () -> editor.remove( parsed ), type );
		}
		return this;
	}

	/**
	 * Removes every metadata type the source format can hold.
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata removeAll() {
		return guarded( editor::removeAll, "all metadata" );
	}

	// ---------------------------------------------------------------- output

	/**
	 * Writes the source file in place with the current metadata. The result is written to a
	 * temporary file first and moved over the original, so a failure leaves the original untouched.
	 *
	 * @return This handle, for chaining
	 *
	 * @throws BoxRuntimeException If the handle was not read from a local file
	 */
	public BoxImageMetadata write() {
		Path path = sourcePath();
		if ( path == null ) {
			throw new BoxRuntimeException( "Cannot write metadata: the source is not a local file. Use write( destination )." );
		}
		return guarded( () -> editor.apply( path ), "image [" + source + "]" );
	}

	/**
	 * Writes a copy of the source image with the current metadata to another file.
	 *
	 * @param destination The absolute path to write to; parent directories are created
	 *
	 * @return This handle, for chaining
	 */
	public BoxImageMetadata write( String destination ) {
		if ( sourceBytes == null ) {
			throw new BoxRuntimeException( "Cannot write metadata: this editor was not read from an image." );
		}
		Path target = Paths.get( destination );
		return guarded( () -> {
			if ( target.getParent() != null ) {
				Files.createDirectories( target.getParent() );
			}
			Path path = sourcePath();
			if ( path != null ) {
				editor.apply( path, target );
			} else {
				Files.write( target, editor.apply( sourceBytes ) );
			}
		}, "image [" + destination + "]" );
	}

	/**
	 * @return The source image with the current metadata, as bytes
	 */
	public byte[] toBinary() {
		if ( sourceBytes == null ) {
			throw new BoxRuntimeException( "Cannot produce an image: this editor was not read from an image." );
		}
		return applyTo( sourceBytes );
	}

	/**
	 * @return The source image with the current metadata, Base64 encoded
	 */
	public String toBase64() {
		return Base64.getEncoder().encodeToString( toBinary() );
	}

	/**
	 * Applies the current metadata to an encoded image. The image may be of a different format
	 * than the source; a metadata type that format cannot hold throws before anything is written.
	 *
	 * @param encodedImage The image bytes
	 *
	 * @return The image with the metadata
	 */
	public byte[] applyTo( byte[] encodedImage ) {
		try {
			return editor.apply( encodedImage );
		} catch ( IllegalArgumentException | UnsupportedOperationException e ) {
			throw new BoxRuntimeException( e.getMessage(), e );
		} catch ( IOException e ) {
			throw new BoxRuntimeException( "Unable to write image metadata: " + e.getMessage(), e );
		}
	}

	// ---------------------------------------------------------------- helpers

	private interface Action {

		void run() throws Exception;
	}

	private BoxImageMetadata guarded( Action action, String what ) {
		try {
			action.run();
		} catch ( IllegalArgumentException | UnsupportedOperationException e ) {
			throw new BoxRuntimeException( "Cannot set " + what + ": " + e.getMessage(), e );
		} catch ( BoxRuntimeException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new BoxRuntimeException( "Unable to write " + what + ": " + e.getMessage(), e );
		}
		exifCache	= null;
		iptcCache	= null;
		return this;
	}

	private Path sourcePath() {
		if ( source == null || source.toLowerCase( Locale.ROOT ).startsWith( "http" ) ) {
			return null;
		}
		try {
			if ( source.startsWith( "file:" ) ) {
				return Paths.get( new URI( source ) );
			}
			return new File( source ).toPath();
		} catch ( Exception e ) {
			return null;
		}
	}

	/**
	 * Converts a BoxLang value to what pixymeta expects. BoxLang arrays are Lists and DateTime is a
	 * TemporalAccessor, so most values pass through as they are.
	 */
	private static Object toJavaValue( Object value ) {
		if ( value instanceof IStruct ) {
			throw new IllegalArgumentException( "a struct is not a valid tag value" );
		}
		return value;
	}

	private IStruct readStruct( boolean exif ) {
		try {
			byte[] current = editor.hasChanges() ? editor.apply( sourceBytes ) : sourceBytes;
			return exif ? ImageMetadataUtil.readExifMetaData( new ByteArrayInputStream( current ) )
			    : ImageMetadataUtil.readIPTCMetaData( new ByteArrayInputStream( current ) );
		} catch ( UnsupportedOperationException | IllegalArgumentException e ) {
			// The pending change cannot be written to this format; show the editor's view instead
			return exif ? structFromEditorExif() : structFromEditorIptc();
		} catch ( ImageProcessingException | IOException e ) {
			throw new BoxRuntimeException( "Unable to read image metadata: " + e.getMessage(), e );
		}
	}

	/** EXIF tags of an editor without an image, keyed by the display names metadata-extractor uses. */
	private IStruct structFromEditorExif() {
		IStruct struct = new Struct( Struct.TYPES.LINKED );
		for ( Map.Entry<String, String> entry : editor.getExifValues().entrySet() ) {
			String name = entry.getKey();
			try {
				int id = TagNames.exif( name ).getValue() & 0xffff;
				if ( EXIF_NAMES.hasTagName( id ) ) {
					name = EXIF_NAMES.getTagName( id );
				}
			} catch ( IllegalArgumentException e ) {
				// keep the pixymeta name
			}
			struct.put( Key.of( name ), entry.getValue() );
		}
		return struct;
	}

	/** IPTC tags of an editor without an image, keyed by the display names metadata-extractor uses. */
	private IStruct structFromEditorIptc() {
		IStruct struct = new Struct( Struct.TYPES.LINKED );
		for ( Map.Entry<String, List<String>> entry : editor.getIptcValues().entrySet() ) {
			String name = entry.getKey();
			try {
				int id = 0x0200 | TagNames.iptc( name ).getTag();
				if ( IPTC_NAMES.hasTagName( id ) ) {
					name = IPTC_NAMES.getTagName( id );
				}
			} catch ( IllegalArgumentException e ) {
				// keep the pixymeta name
			}
			List<String> values = entry.getValue();
			struct.put( Key.of( name ), values.size() == 1 ? values.get( 0 ) : String.join( ";", values ) );
		}
		return struct;
	}

	private static String typeName( MetadataType type ) {
		switch ( type ) {
			case EXIF :
				return TYPE_EXIF;
			case IPTC :
				return TYPE_IPTC;
			case XMP :
				return TYPE_XMP;
			case ICC_PROFILE :
				return TYPE_ICC;
			case COMMENT :
				return TYPE_COMMENT;
			case PHOTOSHOP_IRB :
				return TYPE_PHOTOSHOP;
			default :
				return null;
		}
	}

	private static final Map<String, MetadataType> TYPE_ALIASES = new LinkedHashMap<>();

	static {
		TYPE_ALIASES.put( "exif", MetadataType.EXIF );
		TYPE_ALIASES.put( "iptc", MetadataType.IPTC );
		TYPE_ALIASES.put( "xmp", MetadataType.XMP );
		TYPE_ALIASES.put( "icc", MetadataType.ICC_PROFILE );
		TYPE_ALIASES.put( "iccprofile", MetadataType.ICC_PROFILE );
		TYPE_ALIASES.put( "icc_profile", MetadataType.ICC_PROFILE );
		TYPE_ALIASES.put( "comment", MetadataType.COMMENT );
		TYPE_ALIASES.put( "comments", MetadataType.COMMENT );
		TYPE_ALIASES.put( "photoshop", MetadataType.PHOTOSHOP_IRB );
		TYPE_ALIASES.put( "irb", MetadataType.PHOTOSHOP_IRB );
		TYPE_ALIASES.put( "photoshop_irb", MetadataType.PHOTOSHOP_IRB );
	}

	/**
	 * Parses a metadata type name.
	 *
	 * @param type exif, iptc, xmp, icc, comment or photoshop (case-insensitive)
	 *
	 * @return The type
	 *
	 * @throws BoxRuntimeException For an unknown name
	 */
	public static MetadataType parseType( String type ) {
		MetadataType parsed = type == null ? null : TYPE_ALIASES.get( type.trim().toLowerCase( Locale.ROOT ) );
		if ( parsed == null ) {
			List<String> known = new ArrayList<>( TYPE_ALIASES.keySet() );
			throw new BoxRuntimeException( "Unknown metadata type [" + type + "]. Use one of " + known + " or all." );
		}
		return parsed;
	}
}
