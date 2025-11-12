package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.modules.image.util.KeyDictionary;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.bifs.BoxMember;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.dynamic.casters.StructCaster;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.types.BoxLangType;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
@BoxMember( type = BoxLangType.CUSTOM, customType = BoxImage.class, name = "setDrawingStroke" )
public class ImageSetDrawingStroke extends BIF {

	/**
	 * Constructor
	 */
	public ImageSetDrawingStroke() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", KeyDictionary.name, Set.of( Validator.REQUIRED ) ),
		    new Argument( false, "struct", KeyDictionary.attributeCollection )
		};
	}

	/**
	 * Sets the stroke properties for subsequent drawing operations on the image.
	 * The stroke defines how lines and shape outlines are rendered, including width,
	 * end cap style, line join style, and dash patterns.
	 *
	 * <p>
	 * The attributeCollection struct can contain any combination of the following optional keys:
	 * </p>
	 *
	 * <ul>
	 * <li><b>width</b> (float) - The stroke width in pixels. Controls the thickness of lines and shape outlines.
	 * Example: 2.5</li>
	 * <li><b>endCaps</b> (string) - The end cap style for unclosed subpaths and dash segments. Valid values:
	 * <ul>
	 * <li>"butt" - Ends with no added decoration (square end at the endpoint)</li>
	 * <li>"round" - Ends with a round decoration (semicircle beyond the endpoint)</li>
	 * <li>"square" - Ends with a square projection (square beyond the endpoint)</li>
	 * </ul>
	 * Defaults to "round" if not specified or invalid.</li>
	 * <li><b>lineJoins</b> (string) - The line join style for path segment intersections. Valid values:
	 * <ul>
	 * <li>"miter" - Joins segments by extending outer edges until they meet</li>
	 * <li>"round" - Joins segments by rounding off the corner</li>
	 * <li>"bevel" - Joins segments by connecting outer corners with a straight segment</li>
	 * </ul>
	 * Defaults to "bevel" if not specified or invalid.</li>
	 * <li><b>miterLimit</b> (float) - The miter limit for mitered line joins. Controls when a miter join
	 * is replaced with a bevel join (when the miter length exceeds this value times the stroke width).
	 * Example: 10.0</li>
	 * <li><b>dashArray</b> (array) - An array of floats defining the dash pattern. Alternating values represent
	 * the lengths of opaque and transparent segments. Example: [10, 5, 2, 5] creates a dash-dot pattern.</li>
	 * <li><b>dashPhase</b> (float) - The offset to start the dash pattern. Controls where in the dash pattern
	 * to begin. Example: 0.0 starts at the beginning of the pattern.</li>
	 * </ul>
	 *
	 * <p>
	 * All attributes are optional. If an attribute is not provided, the corresponding stroke property
	 * remains unchanged from its current value.
	 * </p>
	 *
	 * <pre>
	 * // Set basic stroke width
	 * image.setDrawingStroke({ width: 3.0 });
	 *
	 * // Set width and end cap style
	 * image.setDrawingStroke({ width: 5.0, endCaps: "round" });
	 *
	 * // Create a dashed line
	 * image.setDrawingStroke({
	 *     width: 2.0,
	 *     dashArray: [10, 5],
	 *     dashPhase: 0
	 * });
	 *
	 * // Full configuration
	 * image.setDrawingStroke({
	 *     width: 4.0,
	 *     endCaps: "round",
	 *     lineJoins: "miter",
	 *     miterLimit: 10.0,
	 *     dashArray: [15, 5, 5, 5],
	 *     dashPhase: 0
	 * });
	 * </pre>
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public BoxImage _invoke( IBoxContext context, ArgumentsScope arguments ) {
		BoxImage theImage = arguments.get( KeyDictionary.name ) instanceof BoxImage
		    ? ( BoxImage ) arguments.get( KeyDictionary.name )
		    : ( BoxImage ) context.getDefaultAssignmentScope().get( arguments.getAsString( KeyDictionary.name ) );

		theImage.setDrawingStroke( StructCaster.cast( arguments.getAsStruct( KeyDictionary.attributeCollection ) ) );

		return theImage;
	}

}
