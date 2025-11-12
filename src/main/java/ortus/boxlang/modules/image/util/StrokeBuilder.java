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
package ortus.boxlang.modules.image.util;

import java.awt.BasicStroke;

/**
 * Builder class for creating Java AWT BasicStroke objects with a fluent API.
 * This class provides a convenient way to configure stroke properties for drawing
 * operations in the BoxLang Image module.
 *
 * <p>
 * A stroke defines the appearance of lines and curves drawn on images, including
 * properties like width, line endings, corner styles, and dash patterns.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 *
 * BasicStroke stroke = new StrokeBuilder()
 *     .width( 2.5f )
 *     .endCaps( BasicStroke.CAP_ROUND )
 *     .lineJoins( BasicStroke.JOIN_MITER )
 *     .dashArray( new float[] { 5.0f, 3.0f } )
 *     .build();
 * </pre>
 *
 * @see BasicStroke
 */
public class StrokeBuilder {

	private float	width		= 1;
	private float	miterLimit	= 1;
	private int		endCaps		= 1;
	private int		lineJoins	= 1;
	private float[]	dashArray	= new float[] { 1 };
	private float	dashPhase	= 0;

	/**
	 * Sets the width of the stroke (line thickness).
	 * The width is measured perpendicular to the path being drawn.
	 *
	 * @param width The width of the stroke in pixels. Must be non-negative.
	 *              Values less than 1.0 create thin lines; larger values create thicker lines.
	 *              Default is 1.0.
	 *
	 * @return This StrokeBuilder instance for method chaining
	 */
	public StrokeBuilder width( float width ) {
		this.width = width;
		return this;
	}

	/**
	 * Sets the miter limit for miter line joins.
	 * The miter limit determines when a miter join is automatically converted to a bevel join.
	 * It is expressed as the ratio of the miter length to the stroke width.
	 *
	 * <p>
	 * When two line segments meet at a sharp angle and miter joins are used, the outer edges
	 * can extend very far. The miter limit prevents excessively long spikes by beveling the
	 * corner when the miter length exceeds the limit.
	 * </p>
	 *
	 * @param miterLimit The miter limit as a ratio. Must be greater than or equal to 1.0.
	 *                   Smaller values create beveled corners more aggressively.
	 *                   Larger values allow sharper corners. Default is 1.0.
	 *
	 * @return This StrokeBuilder instance for method chaining
	 */
	public StrokeBuilder miterLimit( float miterLimit ) {
		this.miterLimit = miterLimit;
		return this;
	}

	/**
	 * Sets the end cap style for the stroke.
	 * End caps define the decoration applied to the ends of unclosed subpaths and dash segments.
	 *
	 * @param endCaps The end cap style. Valid values from BasicStroke:
	 *                <ul>
	 *                <li>{@link BasicStroke#CAP_BUTT} - Ends with no added decoration</li>
	 *                <li>{@link BasicStroke#CAP_ROUND} - Ends with a round decoration (default)</li>
	 *                <li>{@link BasicStroke#CAP_SQUARE} - Ends with a square projection</li>
	 *                </ul>
	 *
	 * @return This StrokeBuilder instance for method chaining
	 *
	 * @see EnumConverterUtil#getEndCapInt(String) for converting string values to int constants
	 */
	public StrokeBuilder endCaps( int endCaps ) {
		this.endCaps = endCaps;
		return this;
	}

	/**
	 * Sets the line join style for the stroke.
	 * Line joins define the decoration applied at the intersection of two path segments
	 * and at the intersection of the endpoints of a subpath.
	 *
	 * @param lineJoins The line join style. Valid values from BasicStroke:
	 *                  <ul>
	 *                  <li>{@link BasicStroke#JOIN_BEVEL} - Joins by connecting outer corners with a straight segment</li>
	 *                  <li>{@link BasicStroke#JOIN_MITER} - Joins by extending outer edges until they meet (default)</li>
	 *                  <li>{@link BasicStroke#JOIN_ROUND} - Joins by rounding off the corner</li>
	 *                  </ul>
	 *
	 * @return This StrokeBuilder instance for method chaining
	 *
	 * @see EnumConverterUtil#getLineJoinsInt(String) for converting string values to int constants
	 */
	public StrokeBuilder lineJoins( int lineJoins ) {
		this.lineJoins = lineJoins;
		return this;
	}

	/**
	 * Sets the dash pattern for the stroke.
	 * The dash array defines a pattern of opaque and transparent segments for dashed lines.
	 *
	 * <p>
	 * The array values specify the lengths of alternating opaque and transparent segments.
	 * For example, {5.0f, 3.0f} creates a pattern with 5-pixel dashes separated by 3-pixel gaps.
	 * The pattern repeats along the length of the line.
	 * </p>
	 *
	 * @param dashArray An array of float values representing the dash pattern.
	 *                  Values alternate between the length of opaque (dash) and transparent (gap) segments.
	 *                  All values must be non-negative. A single value like {1.0f} creates equally-spaced
	 *                  dashes and gaps. Default is {1.0f}.
	 *
	 * @return This StrokeBuilder instance for method chaining
	 */
	public StrokeBuilder dashArray( float[] dashArray ) {
		this.dashArray = dashArray;
		return this;
	}

	/**
	 * Sets the dash phase for the stroke.
	 * The dash phase specifies the offset into the dash pattern at which the stroke begins.
	 *
	 * <p>
	 * This allows you to control where in the dash pattern the line starts drawing.
	 * For example, with a pattern of {5.0f, 3.0f} and a phase of 2.0f, the line starts
	 * 2 pixels into the first dash segment.
	 * </p>
	 *
	 * @param dashPhase The offset into the dash pattern, measured in pixels.
	 *                  Must be non-negative. A value of 0.0 starts at the beginning of the pattern.
	 *                  Default is 0.0.
	 *
	 * @return This StrokeBuilder instance for method chaining
	 */
	public StrokeBuilder dashPhase( float dashPhase ) {
		this.dashPhase = dashPhase;
		return this;
	}

	/**
	 * Builds and returns a BasicStroke object with the configured properties.
	 * This method creates a new BasicStroke using all the properties that have been set
	 * through the builder's fluent API.
	 *
	 * @return A new BasicStroke instance configured with the specified width, end caps,
	 *         line joins, miter limit, dash array, and dash phase
	 *
	 * @throws IllegalArgumentException if any of the configured values are invalid
	 *                                  (e.g., negative width, miter limit less than 1.0)
	 */
	public BasicStroke build() {
		return new BasicStroke( width, endCaps, lineJoins, miterLimit, dashArray, dashPhase );
	}
}
