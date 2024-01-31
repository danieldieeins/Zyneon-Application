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
package fr327.theshark34.openlauncherlib.util.ramselector;

import fr327.flowarg.openlauncherlib.ModifiedByFlow;
import fr327.theshark34.openlauncherlib.util.CrashReporter;
import fr327.theshark34.openlauncherlib.util.LogUtil;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The RAM Selector
 *
 * <p>
 * A Tool to select the RAM for your project.
 * </p>
 *
 * @author Litarvan
 * @version 3.0.2-BETA
 * @since 3.0.0-BETA
 */
@fr327.flowarg.openlauncherlib.ModifiedByFlow
public class RamSelector
{
    /**
     * The RAM !
     */
    public static final String[] RAM_ARRAY = new String[]{"1Go", "2Go", "3Go", "4Go", "5Go", "6Go", "7Go", "8Go", "9Go", "10Go"};

    /**
     * The file where to save the ram
     */
    private Path file;

    /**
     * The class of the selector frame
     */
    private Class<? extends fr327.theshark34.openlauncherlib.util.ramselector.AbstractOptionFrame> frameClass = OptionFrame.class;

    /**
     * The created frame
     */
    private fr327.theshark34.openlauncherlib.util.ramselector.AbstractOptionFrame frame;

    /**
     * The RAM Selector with a file to save the RAM
     *
     * @param file The file where to save the RAM
     * @deprecated use {@link #RamSelector(Path)} instead.
     */
    @Deprecated
    public RamSelector(File file)
    {
        this.file = file.toPath();
    }

    /**
     * The RAM Selector with a file to save the RAM
     *
     * @param path The file where to save the RAM
     */
    public RamSelector(Path path)
    {
        this.file = path;
    }

    /**
     * Display the selector
     *
     * @return The displayed frame, an instance of the given
     * frame class (by default OptionFrame)
     * @see #setFrameClass(Class)
     * @see #getFrameClass()
     */
    public JFrame display()
    {
        if (this.frame == null)
        {
            try
            {
                Constructor<?>[] constructors = frameClass.getDeclaredConstructors();

                Constructor<?> constructor = null;
                for (Constructor<?> c : constructors)
                    if (c.getParameterTypes().length == 1 && c.getParameterTypes()[0] == RamSelector.class)
                        constructor = c;

                if (constructor == null)
                    throw new IllegalStateException("Can't load the OptionFrame class, it needs to have a constructor with just a RamSelector as argument.");

                this.frame = (fr327.theshark34.openlauncherlib.util.ramselector.AbstractOptionFrame)constructor.newInstance(this);
                this.frame.setSelectedIndex(this.readRam());
            } catch (Exception e)
            {
                System.err.println("[OpenLauncherLib] Can't display the Ram Selector !");
                System.err.println(CrashReporter.makeCrashReport("OpenLauncherLib Ram Selector", e));

                return null;
            }
        }

        this.frame.setVisible(true);

        return this.frame;
    }

    /**
     * Get the generated RAM arguments
     *
     * @return An array of two strings containing the arguments
     */
    @ModifiedByFlow
    public String[] getRamArguments()
    {
        int maxRam = Integer.parseInt(this.frame == null ? RAM_ARRAY[this.readRam()].replace("Go", "") : RAM_ARRAY[this.frame.getSelectedIndex()].replace("Go", "")) * 1024;
        int minRam = maxRam - 1024;

        if (maxRam - 1024 <= 0) minRam = 128;

        return new String[]{"-Xms" + minRam + "M", "-Xmx" + maxRam + "M"};
    }

    /**
     * Read the saved ram
     *
     * @return An int, of the selected index of RAM_ARRAY
     */
    private int readRam()
    {
        try(BufferedReader br = Files.newBufferedReader(this.file, StandardCharsets.UTF_8))
        {
            final String ramText = br.readLine();

            if (ramText != null) return Integer.parseInt(ramText);
            else LogUtil.err("warn", "ram-empty");
        } catch (IOException e)
        {
            System.err.println("[OpenLauncherLib] WARNING: Can't read ram : " + e);
        }

        return 0;
    }

    /**
     * Save the RAM
     */
    public void save()
    {
        if (this.frame == null) return;

        try(BufferedWriter bw = Files.newBufferedWriter(this.file, StandardCharsets.UTF_8))
        {
            bw.write(String.valueOf(this.frame.getSelectedIndex()));
        } catch (IOException e)
        {
            System.err.println("[OpenLauncherLib] WARNING: Can't save ram : " + e);
        }
    }

    /**
     * Return the file where to save the ram
     *
     * @return The file where the ram is saved
     * @see #setFile(Path)
     */
    public Path getFile()
    {
        return this.file;
    }

    /**
     * Set the file where to save the ram
     *
     * @param file The new file where the ram is saved
     * @see #getFile()
     */
    public void setFile(Path file)
    {
        this.file = file;
    }

    /**
     * Return the class of the selector Frame (? extends JFrame)
     *
     * @return The selector frame class
     * @see #setFrameClass(Class)
     */
    public Class<? extends JFrame> getFrameClass()
    {
        return this.frameClass;
    }

    /**
     * Set the class of the selector Frame (need to be a JFrame)
     *
     * @param frameClass The new class of the selector
     * @see #getFrameClass()
     */
    public void setFrameClass(Class<? extends AbstractOptionFrame> frameClass)
    {
        this.frameClass = frameClass;
    }
}
