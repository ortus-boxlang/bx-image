/**
 * [BoxLang]
 *
 * Copyright [2024] [Ortus Solutions, Corp]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package ortus.boxlang.modules.image;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import ortus.boxlang.runtime.BoxRuntime;

/**
 * Regression test for a classloader-isolation bug: BoxLang gives every
 * module its own isolated {@code ClassLoader} for its bundled {@code libs/}
 * (see {@code ModuleConfig.bx}'s own docblock: "Every Module will have it's
 * own ClassLoader that will be used to load the module libs and
 * dependencies."). {@code javax.imageio.ImageIO}'s own static plugin
 * registry is built once, lazily, using whichever thread's context
 * classloader happens to be active the very first time *anything* in the
 * whole JVM process touches {@code ImageIO} - which may not be this
 * module's own classloader at all, and {@code ImageIO} never rescans
 * automatically afterward. Without an explicit rescan forced from this
 * module's own classloader ({@link BoxImage}'s own static initializer),
 * the WebP writer/reader plugins bundled in this module's own {@code libs/}
 * (webp-imageio, imageio-webp) are never discovered from inside a real
 * BoxLang module load, even though they're right there on this module's
 * own classpath.
 *
 * <p>
 * This class lives in its own dedicated {@code isolationTest} Gradle
 * source set (see {@code build.gradle}'s {@code isolationTest} task),
 * deliberately kept free of any dependency on the main source set. That
 * matters because Gradle's default {@code testImplementation} configuration
 * extends {@code implementation}, so on the normal {@code test} task's flat
 * classpath this module's own {@code webp-imageio}/{@code imageio-webp}
 * deps are already directly present - meaning {@code ImageIO} would
 * discover WebP there regardless of whether {@link BoxImage}'s static
 * initializer fix ever ran, which would make a regression test for this
 * bug meaningless. On the {@code isolationTest} classpath, the only route
 * by which the WebP codecs can be seen at all is through the isolated
 * {@link URLClassLoader} below, wrapping the actual shaded module jar
 * built by the {@code shadowJar} task - exactly like BoxLang's own module
 * loader does.
 * </p>
 */
public class WebPClassLoaderIsolationTest {

	@Test
	public void loadingBoxImageThroughAnIsolatedClassLoaderMakesWebpDiscoverable() throws Exception {
		String jarPath = System.getProperty( "bx.image.shadedJarPath" );
		Assumptions.assumeTrue( jarPath != null && new File( jarPath ).isFile(), "Shaded module jar not found - run the shadowJar task first" );

		// The platform classloader (JDK classes only, no app classpath) is
		// used as the isolated loader's parent, with the BoxLang runtime jar
		// (needed to resolve BoxImage's own ortus.boxlang.runtime.* imports)
		// and this module's own shaded jar added explicitly as the child's
		// only sources - so BoxImage loads through a classloader that has
		// never touched ImageIO before, exactly like BoxLang's own
		// per-module ClassLoader does.
		URL				boxlangRuntimeJar	= BoxRuntime.class.getProtectionDomain().getCodeSource().getLocation();
		URLClassLoader	isolatedLoader		= new URLClassLoader(
		    new URL[] { boxlangRuntimeJar, new File( jarPath ).toURI().toURL() },
		    ClassLoader.getPlatformClassLoader() );

		Class<?>		boxImageClass		= Class.forName( "ortus.boxlang.modules.image.BoxImage", true, isolatedLoader );

		assertThat( boxImageClass.getClassLoader() ).isEqualTo( isolatedLoader );
		assertThat( Arrays.stream( ImageIO.getWriterFormatNames() ).anyMatch( f -> f.equalsIgnoreCase( "webp" ) ) ).isTrue();
	}

}
