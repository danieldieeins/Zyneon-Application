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
package fr327.theshark34.openlauncherlib.util;

import fr327.flowarg.openlauncherlib.ModifiedByFlow;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The Process Log Manager
 *
 * <p>
 * Manager logs of a Process by printing and/or writing them.
 * </p>
 *
 * @author Litarvan
 * @version 3.0.2-BETA
 * @see Process
 */
public class ProcessLogManager extends Thread
{
    /**
     * If the logs should be printed
     */
    private boolean print = true;

    /**
     * The reader
     */
    private final BufferedReader reader;

    /**
     * Input stream reader
     */
    private final InputStreamReader inputStreamReader;

    /**
     * The file where to write the logs (optional)
     */
    private Path toWrite;

    /**
     * The writer to write the logs
     */
    private BufferedWriter writer;

    /**
     * Simple constructor
     *
     * @param input The input where to read the logs
     */
    public ProcessLogManager(InputStream input)
    {
        this(input, (Path)null);
    }

    /**
     * Complete constructor
     *
     * @param input   The input where to read the logs
     * @param toWrite The files where to write the logs (optional)
     */
    @Deprecated
    public ProcessLogManager(InputStream input, File toWrite)
    {
        this(input, toWrite.toPath());
    }

    /**
     * Complete constructor
     *
     * @param input   The input where to read the logs
     * @param toWrite The files where to write the logs (optional)
     */
    public ProcessLogManager(InputStream input, Path toWrite)
    {
        this.inputStreamReader = new InputStreamReader(input, StandardCharsets.UTF_8);
        this.reader = new BufferedReader(this.inputStreamReader);
        this.toWrite = toWrite;

        if(this.toWrite == null) return;

        try
        {
            this.writer = Files.newBufferedWriter(this.toWrite);
        } catch (IOException e)
        {
            LogUtil.err("log-err", e.toString());
        }
    }

    @ModifiedByFlow
    @Override
    public void run()
    {
        String line;
        try
        {
            while ((line = this.reader.readLine()) != null)
            {
                if (this.print) System.out.printf("%s\n", line);

                if(this.writer == null) continue;

                try
                {
                    this.writer.write(line + "\n");
                } catch (IOException e)
                {
                    LogUtil.err("log-err", e.toString());
                }
            }
        }
        catch (IOException e)
        {
            LogUtil.err("log-end", e.toString());

            this.interrupt();
        }

        if(this.writer == null) return;

        try
        {
            this.writer.close();
            this.inputStreamReader.close();
        } catch (IOException ignored) {}
    }

    /**
     * If the logs are printed
     *
     * @return True if they are, false if not
     */
    public boolean isPrint()
    {
        return this.print;
    }

    /**
     * Set if the logs should be printed
     *
     * @param print If they should be printed
     */
    public void setPrint(boolean print)
    {
        this.print = print;
    }

    /**
     * Return the file where the logs are written
     *
     * @return The file where are written the logs
     */
    public Path getToWrite()
    {
        return this.toWrite;
    }

    /**
     * Set the files where to write the logs
     *
     * @param toWrite The new file
     */
    public void setToWrite(Path toWrite)
    {
        this.toWrite = toWrite;
    }
}
