/**
 * [BoxLang]
 *
 * Copyright [2023] [Ortus Solutions, Corp]
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

import ortus.boxlang.runtime.scopes.Key;

/**
 * Image module event keys.
 *
 * This class defines constant keys for various events that can occur within the
 * BoxLang Image module. These keys are used to identify and handle specific
 * image-related events in the module's event system.
 */
public class ImageEvents {

	public static final Key	IMAGE_WRITE_TO_BROWSER	= Key.of( "imageWriteToBrowser" );
	public static final Key	IMAGE_UNKNOWN_ACTION	= Key.of( "imageUnknownAction" );
}
