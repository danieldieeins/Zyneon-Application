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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The File List
 *
 * <p>
 * The File List is a List of files, but with some very useful
 * methods like the add(File Array / FileList / List of file) methods,
 * the match method, etc...
 * </p>
 *
 * @author Litarvan
 * @version 3.0.2-BETA
 * @since 3.0.0-BETA
 */
@ModifiedByFlow
public class FileList
{
    /**
     * The file list
     */
    protected List<Path> files;

    /**
     * The File List, empty
     */
    public FileList()
    {
        this.files = new ArrayList<>();
    }

    /**
     * The File List, with pre-defined files
     *
     * @param files The files
     */
    public FileList(List<Path> files)
    {
        this.files = files;
    }

    /**
     * Add all the given files to the list
     *
     * @param files The files to add
     */
    public void add(Path... files)
    {
        this.add(Arrays.asList(files));
    }

    /**
     * Add the list content to the current one
     *
     * @param files The list of files to add
     */
    public void add(List<Path> files)
    {
        this.files.addAll(files);
    }

    /**
     * Add the list content to the current one
     *
     * @param list The list of files to add
     */
    public void add(FileList list)
    {
        this.add(list.get());
    }

    /**
     * Create a new list with all the files with a name matching
     * the given regex
     *
     * @param regex The regex to apply
     * @return The generated list
     */
    public FileList match(String regex)
    {
        List<Path> matching = new ArrayList<>();

        for (Path f : files)
            if (f.toString().matches(regex))
                matching.add(f);

        return new FileList(matching);
    }

    /**
     * Return a new list of all the directories of the current one
     * (not the files)
     *
     * @return The generated list
     */
    public FileList dirs()
    {
        List<Path> dirs = new ArrayList<>();

        for (Path f : files)
            if (Files.isDirectory(f))
                dirs.add(f);

        return new FileList(dirs);
    }

    /**
     * Return a new list of all the files of the current one
     * (not the directories)
     *
     * @return The generated list
     */
    public FileList files()
    {
        List<Path> files = new ArrayList<>();

        for (Path f : this.files)
            if (!Files.isDirectory(f))
                files.add(f);

        return new FileList(files);
    }

    /**
     * Return this, as a List object
     *
     * @return This, as {@link List}
     */
    public List<Path> get()
    {
        return files;
    }
}
