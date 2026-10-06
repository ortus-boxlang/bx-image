
/**
 * [BoxLang]
 * Copyright [2026] [Ortus Solutions, Corp]
 * Licensed under the Apache License, Version 2.0.
 */
import java.nio.file.Files;
import java.nio.file.Path;
import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.modules.ModuleRecord;
import ortus.boxlang.runtime.context.ScriptingRequestBoxContext;

/** Runs the shared image-contract.bxs against the unchanged JVM backend. */
class ImageContract {

	public static void main( String[] args ) throws Exception {
		BoxRuntime	runtime	= BoxRuntime.getInstance( true, null, Files.createTempDirectory( "bx-image-contract-" ).toString() );
		var			context	= new ScriptingRequestBoxContext( runtime.getRuntimeContext() );
		var			module	= new ModuleRecord( Path.of( args[ 0 ] ).toAbsolutePath().toString() );
		runtime.getModuleService().getRegistry().put( module.name, module );
		module.loadDescriptor( context ).register( context ).activate( context );
		try {
			runtime.executeSource( Files.readString( Path.of( args[ 1 ] ) ), context );
		} finally {
			runtime.shutdown();
		}
	}
}
