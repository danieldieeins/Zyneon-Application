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
import fr327.theshark34.openlauncherlib.LaunchException;
import fr327.theshark34.openlauncherlib.external.ClasspathConstructor;
import fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr327.theshark34.openlauncherlib.util.LogUtil;
import fr327.theshark34.openlauncherlib.util.explorer.Explorer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The Minecraft Launcher
 *
 * <p>
 * Contains some methods to create internal/external launch profile for Minecraft.
 * </p>
 *
 * @author Litarvan
 * @version 3.0.4-BETA
 * @since 3.0.0-BETA
 */
@fr327.flowarg.openlauncherlib.ModifiedByFlow
public class MinecraftLauncher
{
    /**
     * Generate an External Launch Profile for Minecraft
     *
     * @param infos     The GameInfos (contains your game infos)
     * @param folder    The GameFolder (contains your game folder organization)
     * @param authInfos The AuthInfos (contains the user infos)
     * @return The generated profile
     * @throws fr327.theshark34.openlauncherlib.LaunchException If it failed
     */
    public static fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile createExternalProfile(GameInfos infos, GameFolder folder, AuthInfos authInfos) throws LaunchException
    {
        fr327.theshark34.openlauncherlib.util.LogUtil.info("mc-ext", infos.getGameVersion().getName());
        fr327.theshark34.openlauncherlib.util.LogUtil.info("mc-check", infos.getGameDir().toString());

        if (authInfos == null) throw new IllegalArgumentException("authInfos == null");

        checkFolder(folder, infos.getGameDir());

        fr327.theshark34.openlauncherlib.util.LogUtil.info("mc-cp");

        final fr327.theshark34.openlauncherlib.external.ClasspathConstructor constructor = new ClasspathConstructor();
        final List<Path> libs = fr327.theshark34.openlauncherlib.util.explorer.Explorer.dir(infos.getGameDir()).sub(folder.getLibsFolder()).allRecursive().files().match("^(.*\\.((jar)$))*$").get();
        final List<Path> toRemove = new ArrayList<>();
        
        libs.forEach(f -> {
            final String fileName = f.getFileName().toString();
            if(infos.getGameVersion().getGameType().equals(fr327.theshark34.openlauncherlib.minecraft.GameType.V1_13_HIGHER_FORGE))
            {
                if(fileName.contains("asm"))
                {
                    if(fileName.contains("6") && !infos.getGameVersion().getName().contains("1.14"))
                        toRemove.add(f);
                }
                else if(fileName.contains("guava"))
                {
                    if(fileName.contains("20") || fileName.contains("25"))
                        toRemove.add(f);
                }
            }
            else if(infos.getGameVersion().getGameType().equals(fr327.theshark34.openlauncherlib.minecraft.GameType.V1_7_10) && infos.getGameTweaks().length > 0 && infos.getGameTweaks()[0] == fr327.theshark34.openlauncherlib.minecraft.GameTweak.FORGE)
            {
                if(!fileName.contains("guava")) return;

                if(fileName.contains("15"))
                    toRemove.add(f);
            }
        });
        
        toRemove.forEach(libs::remove);
        constructor.add(libs);
        constructor.add(fr327.theshark34.openlauncherlib.util.explorer.Explorer.dir(infos.getGameDir()).get(folder.getMainJar()));

        final String mainClass = infos.getGameTweaks() == null || infos.getGameTweaks().length == 0 ? infos.getGameVersion().getGameType().getMainClass(infos) : fr327.theshark34.openlauncherlib.minecraft.GameTweak.LAUNCHWRAPPER_MAIN_CLASS;
        final String classpath = constructor.make();
        final List<String> args = infos.getGameVersion().getGameType().getLaunchArgs(infos, folder, authInfos);
        final List<String> vmArgs = new ArrayList<>();

        final String nativesFolder = folder.getNativesFolder();
        vmArgs.add("-Djava.library.path=" + (nativesFolder.equals(".") ? "." : Explorer.dir(infos.getGameDir()).sub(nativesFolder).get().toString()));

        if(infos.getGameVersion().getGameType() != GameType.FABRIC)
        {
            vmArgs.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
            vmArgs.add("-Dfml.ignorePatchDiscrepancies=true");
        }

        if (infos.getGameTweaks() != null)
        {
            for (GameTweak tweak : infos.getGameTweaks())
            {
                args.add("--tweakClass");
                args.add(tweak.getTweakClass(infos));
            }
        }

        final fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile profile = new ExternalLaunchProfile(mainClass, classpath, vmArgs, args, true, infos.getServerName(), infos.getGameDir());
        LogUtil.info("done");

        return profile;
    }

    /**
     * Checks the given folder organization
     *
     * @param folder    The folder organization
     * @param directory The directory to check
     * @throws FolderException If it failed
     */
    @fr327.flowarg.openlauncherlib.ModifiedByFlow
    public static void checkFolder(GameFolder folder, Path directory) throws FolderException
    {
        final Path assetsFolder = directory.resolve(folder.getAssetsFolder());
        final Path libsFolder = directory.resolve(folder.getLibsFolder());
        final Path nativesFolder = directory.resolve(folder.getNativesFolder());
        final Path minecraftJar = directory.resolve(folder.getMainJar());

        try {
            if (Files.notExists(assetsFolder) || notEmpty(assetsFolder))
                throw new FolderException("Missing/Empty assets folder (" + assetsFolder + ")");
            else if (Files.notExists(libsFolder) || notEmpty(libsFolder))
                throw new FolderException("Missing/Empty libraries folder (" + libsFolder + ")");
            else if (Files.notExists(nativesFolder) || notEmpty(nativesFolder))
                throw new FolderException("Missing/Empty natives folder (" + nativesFolder + ")");
            else if (Files.notExists(minecraftJar))
                throw new FolderException("Missing main jar (" + minecraftJar + ")");
        } catch (IOException e)
        {
            throw new FolderException(e);
        }
    }

    @ModifiedByFlow
    private static boolean notEmpty(Path path) throws IOException
    {
        try(final Stream<Path> children = Files.list(path))
        {
            return children == null || !children.findAny().isPresent();
        }
    }
}
