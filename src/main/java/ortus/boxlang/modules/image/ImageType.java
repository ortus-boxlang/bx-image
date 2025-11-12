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

/**
 * Enum representing different image types supported in the BoxLang Image module.
 * This enum is used to specify the color model of images being created or processed.
 *
 * <ul>
 * <li><strong>RGB</strong>: Represents a standard Red-Green-Blue color model without an alpha channel.</li>
 * <li><strong>ARGB</strong>: Represents a color model with an Alpha channel for transparency, followed by Red, Green, and Blue channels.</li>
 * <li><strong>GRAYSCALE</strong>: Represents a grayscale color model where each pixel is represented by a single intensity value.</li>
 * </ul>
 *
 * @see ortus.boxlang.modules.image.ImageModule
 */
public enum ImageType {
	RGB,
	ARGB,
	GRAYSCALE
}
