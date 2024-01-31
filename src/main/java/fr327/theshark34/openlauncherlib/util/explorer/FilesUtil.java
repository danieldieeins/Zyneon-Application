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
package fr327.theshark34.openlauncherlib.util.explorer;

import fr327.flowarg.openlauncherlib.ModifiedByFlow;
import fr327.theshark34.openlauncherlib.FailException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Files Util class
 *
 * <p>
 * Contains some useful methods about the files.
 * </p>
 *
 * @author Litarvan
 * @version 3.0.2-BETA
 * @since 3.0.0-BETA
 */
@ModifiedByFlow
public class FilesUtil
{
    /**
     * List the sub-files and sub-directory of a given one, recursively
     *
     * @param directory The directory to list
     * @return The generated list of files
     */
    public static List<Path> listRecursive(final Path directory)
    {
        final List<Path> files = new ArrayList<>();
        final List<Path> fs = list(directory);

        for (final Path f : fs)
        {
            if (Files.isDirectory(f)) files.addAll(listRecursive(f));
            files.add(f);
        }
        return files;
    }


    /**
     * Get a file in a directory and checks if it is existing
     * (throws a FailException if not)
     *
     * @param root The root directory where the file is supposed to be
     * @param file The name of the file to get
     * @return The found file
     */
    public static Path get(Path root, String file)
    {
        final Path f = root.resolve(file);
        if (Files.notExists(f))
            throw new fr327.theshark34.openlauncherlib.FailException("Given file/directory doesn't exist !");

        return f;
    }

    /**
     * Return the given directory, but check if it is a directory
     *
     * @param d The directory to check
     * @return The given directory
     */
    public static Path dir(Path d)
    {
        if (!Files.isDirectory(d))
            throw new fr327.theshark34.openlauncherlib.FailException("Given directory is not one !");

        return d;
    }

    /**
     * Mix between the get method and the dir method
     *
     * @param root The directory where the other one is supposed to be
     * @param dir  The name of the directory to get
     * @return The got directory
     * @see #get(Path, String)
     * @see #dir(Path)
     */
    public static Path dir(Path root, String dir)
    {
        return dir(get(root, dir));
    }

    /**
     * Return the list of the files of the given directory, but
     * checks if it is a directory, and return an empty file list if listFiles returns null
     *
     * @param dir The directory to list
     * @return The files in the given directory
     * @see #dir(Path)
     */
    public static List<Path> list(final Path dir)
    {
        final List<Path> result = new ArrayList<>();
        try
        {
            if(Files.exists(dir))
            {
                try(final Stream<Path> stream = Files.list(dir))
                {
                    result.addAll(stream.collect(Collectors.toList()));
                }
            }
            return result;
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new FailException(e.getMessage());
        }
    }
}
