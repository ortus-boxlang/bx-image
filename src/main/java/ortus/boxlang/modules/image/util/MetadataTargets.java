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
package ortus.boxlang.modules.image.util;

import java.util.ArrayList;
import java.util.List;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.BoxImageMetadata;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Array;
import ortus.boxlang.runtime.types.exceptions.BoxRuntimeException;
import ortus.boxlang.runtime.util.FileSystemUtil;

/**
 * Resolves the first argument of the metadata BIFs. It may be a {@link BoxImage}, a
 * {@link BoxImageMetadata} handle, the name of a variable holding either, or a file path / URL.
 */
public final class MetadataTargets {

	private MetadataTargets() {
	}

	/**
	 * Resolves the argument to a BoxImage, a BoxImageMetadata or an absolute path / URL string.
	 *
	 * @param context The invocation context, for variable lookup and path expansion
	 * @param arg     The argument value
	 *
	 * @return BoxImage, BoxImageMetadata or String
	 */
	public static Object resolve( IBoxContext context, Object arg ) {
		if ( arg instanceof BoxImage || arg instanceof BoxImageMetadata ) {
			return arg;
		}
		if ( arg instanceof String name ) {
			if ( name.isEmpty() ) {
				throw new BoxRuntimeException( "The image argument is empty" );
			}
			// A variable holding an image, as the classic ImageWrite( "myImage" ) form allows
			Object variable = lookupVariable( context, name );
			if ( variable instanceof BoxImage || variable instanceof BoxImageMetadata ) {
				return variable;
			}
			return resolvePath( context, name );
		}
		throw new BoxRuntimeException(
		    "Expected an image, a metadata handle or a file path, got " + ( arg == null ? "null" : arg.getClass().getSimpleName() ) );
	}

	/**
	 * Expands a path with the context's mappings; URLs and file: URIs are returned as they are.
	 *
	 * @param context The invocation context
	 * @param path    The path or URL
	 *
	 * @return The absolute path or URL
	 */
	public static String resolvePath( IBoxContext context, String path ) {
		if ( BoxImage.isURISource( path ) ) {
			return path;
		}
		return FileSystemUtil.expandPath( context, path ).absolutePath().toString();
	}

	/**
	 * Opens a metadata handle for the argument: a handle is returned as is, an image read from a
	 * source path is read again from that path (metadata only), a path is read.
	 *
	 * @param context The invocation context
	 * @param arg     The argument value
	 *
	 * @return The handle
	 */
	public static BoxImageMetadata handleFor( IBoxContext context, Object arg ) {
		Object target = resolve( context, arg );
		if ( target instanceof BoxImageMetadata handle ) {
			return handle;
		}
		if ( target instanceof BoxImage image ) {
			String source = image.getSourcePath();
			if ( source == null || source.isEmpty() ) {
				throw new BoxRuntimeException( "The image has no source file to read metadata from. Pass a file path instead." );
			}
			return BoxImageMetadata.read( source );
		}
		return BoxImageMetadata.read( ( String ) target );
	}

	/**
	 * Turns a metadata type argument (a list string such as "exif,iptc" or an array) into names.
	 *
	 * @param types The argument, may be null
	 *
	 * @return The names, empty if none were given
	 */
	public static String[] typeNames( Object types ) {
		List<String> names = new ArrayList<>();
		if ( types instanceof Array array ) {
			for ( Object o : array ) {
				if ( o != null && !o.toString().isBlank() ) {
					names.add( o.toString().trim() );
				}
			}
		} else if ( types != null && !types.toString().isBlank() ) {
			for ( String part : types.toString().split( "[,;|]" ) ) {
				if ( !part.isBlank() ) {
					names.add( part.trim() );
				}
			}
		}
		return names.toArray( new String[ 0 ] );
	}

	private static Object lookupVariable( IBoxContext context, String name ) {
		try {
			return context.getDefaultAssignmentScope().get( Key.of( name ) );
		} catch ( Exception e ) {
			return null;
		}
	}
}
