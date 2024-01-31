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
package fr327.theshark34.openlauncherlib.external;

import fr327.flowarg.openlauncherlib.ModifiedByFlow;
import fr327.theshark34.openlauncherlib.JavaUtil;
import fr327.theshark34.openlauncherlib.LaunchException;
import fr327.theshark34.openlauncherlib.util.LogUtil;
import fr327.theshark34.openlauncherlib.util.ProcessLogManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The External Launcher
 *
 * <p>
 * Launch a program using java command launched by a ProcessBuilder.
 * </p>
 *
 * @author Litarvan
 * @version 3.0.2-BETA
 * @since 3.0.0-BETA
 */
public class ExternalLauncher
{
    /**
     * The Before Launching Event
     *
     * @see fr327.theshark34.openlauncherlib.external.BeforeLaunchingEvent
     */
    private fr327.theshark34.openlauncherlib.external.BeforeLaunchingEvent launchingEvent;

    /**
     * The launch profile, contains all the information about the launching
     *
     * @see fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile
     */
    private fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile profile;

    /**
     * If the logs are enabled
     */
    private boolean logsEnabled = true;

    /**
     * Vm arguments
     */
    @ModifiedByFlow
    private List<String> vmArgs = new ArrayList<>();

    /**
     * The External Launcher
     *
     * @param profile The launch profile
     * @see fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile
     */
    public ExternalLauncher(fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile profile)
    {
        this(profile, null);
    }

    /**
     * The External Launcher (with Launching Event)
     *
     * @param profile        The launch profile
     * @param launchingEvent The launching event (optional)
     * @see fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile
     * @see fr327.theshark34.openlauncherlib.external.BeforeLaunchingEvent
     */
    public ExternalLauncher(fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile profile, fr327.theshark34.openlauncherlib.external.BeforeLaunchingEvent launchingEvent)
    {
        this.profile        = profile;
        this.launchingEvent = launchingEvent;
    }

    /**
     * @return If the logs are enabled
     */
    public boolean isLogsEnabled()
    {
        return logsEnabled;
    }

    /**
     * Sets the logs enabled or not
     *
     * @param logsEnabled If the logs will be enabled
     */
    public void setLogsEnabled(boolean logsEnabled)
    {
        this.logsEnabled = logsEnabled;
    }

    /**
     * Launch the program !
     *
     * @return The created (and launched) process
     * @throws LaunchException If it failed something
     */
    @ModifiedByFlow
    public Process launch() throws LaunchException
    {
        LogUtil.info("hi-ext");

        final ProcessBuilder builder = new ProcessBuilder();
        vmArgs.add(JavaUtil.getJavaCommand());

        if (profile.getMacDockName() != null && System.getProperty("os.name").toLowerCase().contains("mac"))
            vmArgs.add(JavaUtil.macDockName(profile.getMacDockName()));
        if (profile.getVmArgs() != null)
            vmArgs.addAll(profile.getVmArgs());

        vmArgs.add("-cp");
        vmArgs.add(profile.getClassPath());

        vmArgs.add(profile.getMainClass());

        if (profile.getArgs() != null)
            vmArgs.addAll(profile.getArgs());

        if (profile.getDirectory() != null)
            builder.directory(profile.getDirectory().toFile());

        if (profile.isRedirectErrorStream())
            builder.redirectErrorStream(true);

        if (launchingEvent != null)
            launchingEvent.onLaunching(builder);

        builder.command(vmArgs);

        final StringBuilder entireCommand = new StringBuilder();
        for (String command : vmArgs)
            entireCommand.append(command).append(" ");

        LogUtil.info("ent", ":", entireCommand.toString());
        LogUtil.info("start", profile.getMainClass());

        try
        {
            final Process p = builder.start();

            if (logsEnabled)
            {
                ProcessLogManager manager = new ProcessLogManager(p.getInputStream());
                manager.start();
            }

            return p;
        } catch (IOException e)
        {
            throw new LaunchException("Cannot launch !", e);
        }
    }

    /**
     * The Before Launching event
     * Null by default, or the given one
     *
     * @return The set launching event
     * @see fr327.theshark34.openlauncherlib.external.BeforeLaunchingEvent
     */
    public fr327.theshark34.openlauncherlib.external.BeforeLaunchingEvent getLaunchingEvent()
    {
        return launchingEvent;
    }

    /**
     * Set the launching event (executed just before the launching to customize the ProcessBuilder)
     *
     * @param launchingEvent The launching event to use
     * @see fr327.theshark34.openlauncherlib.external.BeforeLaunchingEvent
     */
    public void setLaunchingEvent(BeforeLaunchingEvent launchingEvent)
    {
        this.launchingEvent = launchingEvent;
    }

    /**
     * Return the given launch profile (containing all the launch information)
     *
     * @return The launch profile
     * @see fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile
     */
    public fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile getProfile()
    {
        return profile;
    }

    /**
     * Set a new launch profile
     *
     * @param profile The new profile
     * @see fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile
     */
    public void setProfile(ExternalLaunchProfile profile)
    {
        this.profile = profile;
    }

    @ModifiedByFlow
    public List<String> getVmArgs()
    {
        return vmArgs;
    }

    @ModifiedByFlow
    public void setVmArgs(List<String> vmArgs)
    {
        this.vmArgs = vmArgs;
    }
}
