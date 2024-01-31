/*
 * Copyright 2015-2016 Adrien "Litarvan" Navratil
 *
 * This file is part of the OpenLauncherLib.

 * The OpenLauncherLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The OpenLauncherLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the OpenLauncherLib.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr327.theshark34.openlauncherlib;

import fr327.flowarg.openlauncherlib.ModifiedByFlow;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The Java Util
 *
 * <p>
 * Contains some useful things about the launching
 * </p>
 *
 * @author Litarvan
 * @version 3.0.4
 * @since 3.0.0-BETA
 */
public class JavaUtil
{
    @ModifiedByFlow
    private static String javaCommand;

    /**
     * Create an argument for the mac dock name
     *
     * @param name The name to set
     * @return The generated argument
     */
    public static String macDockName(String name)
    {
        return "-Xdock:name=" + name;
    }

    /**
     * Return the java executable path
     * Modified by Flow
     *
     * @return The java command
     */
    @ModifiedByFlow
    public static String getJavaCommand()
    {
        if (javaCommand == null)
        {
            final Path java = Paths.get(System.getProperty("java.home"), "bin", "java");
            if (Objects.requireNonNull(System.getProperty("os.name")).toLowerCase().contains("win"))
                javaCommand = "\"" + java + "\"";
            else javaCommand = java.toString();
        }
        return javaCommand;
    }

    /**
     * Set the java executable path
     *
     * @param javaCommandPath The java command
     */
    @ModifiedByFlow
    public static void setJavaCommand(String javaCommandPath)
    {
        javaCommand = javaCommandPath;
    }

    /**
     * Manually set the Java Library Path
     *
     * @param path The new library path
     * @throws Exception If it failed
     */
    public static void setLibraryPath(String path) throws Exception
    {
        System.setProperty("java.library.path", path);

        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
    }
}
