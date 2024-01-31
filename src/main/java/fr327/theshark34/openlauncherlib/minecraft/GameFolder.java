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
package fr327.theshark34.openlauncherlib.minecraft;

import fr327.flowarg.openlauncherlib.ModifiedByFlow;

/**
 * The Game Folder
 *
 * <p>
 * The Game Folder contains the Minecraft folder organisation.
 * </p>
 *
 * @author Litarvan
 * @version 3.0.2-BETA
 * @since 2.0.0-SNAPSHOT
 */
@ModifiedByFlow
public class GameFolder
{

    /**
     * The basic game folder (assets, libs, natives, minecraft.jar)
     */
    public static final GameFolder BASIC = new GameFolder("assets", "libs", "natives", "minecraft.jar");
    
    /**
     * The game folder for FlowUpdater (assets, libraries, natives, client.jar)
     */
    public static final GameFolder FLOW_UPDATER = new GameFolder("assets", "libraries", "natives", "client.jar");

    /**
     * The game folder for FlowUpdater (assets, libraries, natives, client.jar) but for a Minecraft version equal or superior to 1.19
     */
    public static final GameFolder FLOW_UPDATER_1_19_SUP = new GameFolder("assets", "libraries", ".", "client.jar");

    /**
     * The name of the folder containing the assets
     */
    private final String assetsFolder;

    /**
     * The name of the folder containing the librairies
     */
    private final String libsFolder;

    /**
     * The name of the folder containing the natives
     */
    private final String nativesFolder;

    /**
     * The name of the main jar
     */
    private final String mainJar;

    /**
     * The Main Constructor
     *
     * @param assetsFolder  The name of the folder containing the assets
     * @param libsFolder    The name of the folder containing the librairies
     * @param nativesFolder The name of the folder containing the natives
     * @param mainJar       The name of the main Jar
     */
    public GameFolder(String assetsFolder, String libsFolder, String nativesFolder, String mainJar)
    {
        this.assetsFolder  = assetsFolder;
        this.libsFolder    = libsFolder;
        this.nativesFolder = nativesFolder;
        this.mainJar       = mainJar;
    }

    /**
     * Returns the name of the folder containing the assets
     *
     * @return The name of the assets folder
     */
    public String getAssetsFolder()
    {
        return assetsFolder;
    }

    /**
     * Returns the name of the folder containing the librairies
     *
     * @return The name of the librairies folder
     */
    public String getLibsFolder()
    {
        return libsFolder;
    }

    /**
     * Returns the name of the folder containing the natives
     *
     * @return The name of the natives folder
     */
    public String getNativesFolder()
    {
        return nativesFolder;
    }

    /**
     * Return the main minecraft jar
     *
     * @return The main jar
     */
    public String getMainJar()
    {
        return mainJar;
    }

}
