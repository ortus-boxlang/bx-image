package ortus.boxlang.modules.image.bifs;

import java.util.Set;

import ortus.boxlang.modules.image.BoxImage;
import ortus.boxlang.runtime.bifs.BIF;
import ortus.boxlang.runtime.bifs.BoxBIF;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.ArgumentsScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.Argument;
import ortus.boxlang.runtime.validation.Validator;

@BoxBIF
public class IsImage extends BIF {

	/**
	 * Constructor
	 */
	public IsImage() {
		super();
		declaredArguments = new Argument[] {
		    new Argument( true, "any", Key.value, Set.of( Validator.REQUIRED ) )
		};
	}

	/**
	 * ExampleBIF
	 *
	 * @param context   The context in which the BIF is being invoked.
	 * @param arguments Argument scope for the BIF.
	 */
	public Boolean _invoke( IBoxContext context, ArgumentsScope arguments ) {
		return arguments.get( Key.value ) instanceof BoxImage;
	}

}
