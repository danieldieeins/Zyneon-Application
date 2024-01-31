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

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Crash Reporter
 *
 * <p>
 * The Crash Reporter can catch errors and save them as a crash report.
 * </p>
 *
 * @author Litarvan
 * @version 3.0.2-BETA
 * @since 3.0.0-BETA
 */
@ModifiedByFlow
public class CrashReporter
{
    /**
     * The directory to write the crashes
     */
    private Path dir;

    /**
     * The reporter name
     */
    private String name;

    /**
     * Basic constructor
     *
     * @param name The project name
     * @param dir  The directory to write the crashes
     */
    @Deprecated
    public CrashReporter(String name, File dir)
    {
        this(name, dir.toPath());
    }

    /**
     * Basic constructor
     *
     * @param name The project name
     * @param dir  The directory to write the crashes
     */
    public CrashReporter(String name, Path dir)
    {
        this.name = name;
        this.dir = dir;
    }

    /**
     * Catch an error and write it to a crash
     *
     * @param e       The error to catch
     * @param message The error message
     */
    public void catchError(Exception e, String message)
    {
        LogUtil.err("ex-caught");

        System.out.printf("%s\n", makeCrashReport(name, e));

        String msg;

        try
        {
            msg = "\nThe crash report is in : " + this.writeError(e).toString() + "";
        } catch (IOException e2)
        {
            LogUtil.err("report-error");
            e.printStackTrace();
            msg = "\nAnd unable to write the crash report :( : " + e2;
        }

        JOptionPane.showMessageDialog(null, message + "\n" + e + "\n" + msg, "Error", JOptionPane.ERROR_MESSAGE);

        System.exit(1);
    }

    /**
     * Write a stacktrace to a file
     *
     * @param e The exception
     * @return The file where the crash was saved
     * @throws IOException If it failed to write the crash
     */
    public Path writeError(Exception e) throws IOException
    {
        Path path;
        int  number = 0;
        while (Files.exists(path = this.dir.resolve("crash-" + number + ".txt")))
            number++;

        LogUtil.info("writing-crash", path.toString());
        Files.createDirectories(path.getParent());

        try(final BufferedWriter writer = Files.newBufferedWriter(path))
        {
            writer.write(makeCrashReport(name, e));
        }

        return path;
    }

    /**
     * Return the crash directory
     *
     * @return The crash dir
     */
    public Path getDir()
    {
        return dir;
    }

    /**
     * Set the directory where are the crashes
     *
     * @param dir The crash dir
     */
    public void setDir(Path dir)
    {
        this.dir = dir;
    }

    /**
     * Return the reporter name
     *
     * @return The name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the reporter name
     *
     * @param name The new name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Create a crash report with an exception
     *
     * @param e           The exception to make the crash report
     * @param projectName The name of your project
     * @return The made crash report
     */
    public static String makeCrashReport(String projectName, Exception e)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date       date       = new Date();

        StringBuilder report = new StringBuilder()
                .append("# ").append(projectName).append(" Crash Report\n\r")
                .append("#\n\r# At : ").append(dateFormat.format(date)).append("\n\r")
                .append("#\n\r# Exception : ").append(e.getClass().getSimpleName()).append("\n\r")
                .append("\n\r# ").append(e);

        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement element : stackTrace)
            report.append("\n\r#     ").append(element);

        Throwable cause = e.getCause();

        if (cause == null) return report.toString();

        report.append("\n\r# Caused by: ").append(cause);

        StackTraceElement[] causeStackTrace = cause.getStackTrace();
        for (StackTraceElement element : causeStackTrace)
            report.append("\n\r#     ").append(element);

        return report.toString();
    }
}
