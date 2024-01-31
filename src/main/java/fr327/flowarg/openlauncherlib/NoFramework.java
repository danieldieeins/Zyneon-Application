package fr327.flowarg.openlauncherlib;

import fr327.theshark34.openlauncherlib.configuration.api.json.JSONReader;
import fr327.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr327.theshark34.openlauncherlib.external.ExternalLauncher;
import fr327.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr327.theshark34.openlauncherlib.minecraft.GameFolder;
import fr327.theshark34.openlauncherlib.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

public class NoFramework
{
    private final Map<String, Function<Parameters, String>> keyValue = new HashMap<>();
    private final Path gameDir;
    private final Path libraries;
    private final String clientJar;
    private List<String> additionalVmArgs;
    private List<String> additionalArgs;
    private String customVanillaJsonFileName = "";
    private String customModLoaderJsonFileName = "";
    private String serverName = "";
    private SafeConsumer<fr327.theshark34.openlauncherlib.external.ExternalLauncher> lastCallback;

    private static class Parameters
    {
        private JSONObject vanilla;
        private JSONObject processing;
    }

    public enum Type {
        VM,
        GAME
    }

    public interface SafeConsumer<T> {

        void accept(T t) throws Exception;

        default SafeConsumer<T> andThen(SafeConsumer<? super T> after)
        {
            Objects.requireNonNull(after);
            return t -> { this.accept(t); after.accept(t); };
        }
    }

    /**
     * Construct a new NoFramework object.
     * @param gameDir the path of the game directory.
     * @param infos auth information.
     * @param folder the folders' name.
     */
    public NoFramework(Path gameDir, fr327.theshark34.openlauncherlib.minecraft.AuthInfos infos, fr327.theshark34.openlauncherlib.minecraft.GameFolder folder)
    {
        this(gameDir, infos, folder, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Construct a new NoFramework object.
     * @param gameDir the path of the game directory.
     * @param infos auth information.
     * @param folder the folders' name.
     * @param additionalArgs some additional arguments.
     * @param type the type of arguments to add. Can be GAME or VM.
     */
    public NoFramework(Path gameDir, fr327.theshark34.openlauncherlib.minecraft.AuthInfos infos, fr327.theshark34.openlauncherlib.minecraft.GameFolder folder, List<String> additionalArgs, Type type)
    {
        this(gameDir, infos, folder, type == Type.VM ? additionalArgs : new ArrayList<>(), type == Type.GAME ? additionalArgs : new ArrayList<>());
    }

    /**
     * Construct a new NoFramework object.
     * @param gameDir the path of the game directory.
     * @param infos auth information.
     * @param folder the folders' name.
     * @param additionalVmArgs some additional arguments.
     * @param additionalArgs some additional VM arguments.
     */
    public NoFramework(Path gameDir, AuthInfos infos, GameFolder folder, List<String> additionalVmArgs, List<String> additionalArgs)
    {
        this.gameDir = gameDir;
        this.libraries = this.gameDir.resolve(folder.getLibsFolder());
        this.clientJar = folder.getMainJar();
        this.additionalVmArgs = additionalVmArgs;
        this.additionalArgs = additionalArgs;

        this.keyValue.put("${library_directory}", parameters -> this.libraries.toAbsolutePath().toString());
        this.keyValue.put("${classpath_separator}", parameters -> File.pathSeparator);
        this.keyValue.put("${auth_player_name}", parameters -> infos.getUsername());
        this.keyValue.put("${version_name}", parameters -> parameters.processing.getString("id"));
        this.keyValue.put("${game_directory}", parameters -> this.gameDir.toAbsolutePath().toString());
        this.keyValue.put("${assets_root}", parameters -> this.gameDir.resolve(folder.getAssetsFolder()).toAbsolutePath().toString());
        this.keyValue.put("${assets_index_name}", parameters -> parameters.vanilla.getJSONObject("assetIndex").getString("id"));
        this.keyValue.put("${auth_uuid}", parameters -> infos.getUuid());
        this.keyValue.put("${auth_access_token}", parameters -> infos.getAccessToken());
        this.keyValue.put("${user_type}", parameters -> "msa");
        this.keyValue.put("${version_type}", parameters -> "release");
        this.keyValue.put("${clientid}", parameters -> infos.getClientId());
        this.keyValue.put("${auth_xuid}", parameters -> infos.getAuthXUID());
        this.keyValue.put("${natives_directory}", parameters -> folder.getNativesFolder().equals(".") ? "." : this.gameDir.resolve(folder.getNativesFolder()).toAbsolutePath().toString());
        this.keyValue.put("${user_properties}", parameters -> "{}");
    }

    public enum ModLoader
    {
        OLD_FORGE((version, modLoaderVersion) -> version + "-forge" + (modLoaderVersion.startsWith(version) ? modLoaderVersion : version + "-" + modLoaderVersion) + ".json"), // only to 1.12.2-14.23.5.2847
        FORGE((version, modLoaderVersion) -> version + "-forge-" + modLoaderVersion + ".json"),
        NEO_FORGE((version, modLoaderVersion) -> "neoforge-" + modLoaderVersion + ".json"),
        VANILLA(null),
        FABRIC((version, modLoaderVersion) -> "fabric-loader-" + modLoaderVersion + "-" + version + ".json"),
        QUILT((version, modLoaderVersion) -> "quilt-loader-" + modLoaderVersion + "-" + version + ".json"),
        CUSTOM(null);

        private BiFunction<String, String, String> jsonFileNameProvider;

        ModLoader(BiFunction<String, String, String> jsonFileNameProvider)
        {
            this.jsonFileNameProvider = jsonFileNameProvider;
        }

        public void setJsonFileNameProvider(BiFunction<String, String, String> jsonFileNameProvider)
        {
            this.jsonFileNameProvider = jsonFileNameProvider;
        }
    }

    /**
     * Launch the game for the specified versions.
     * @param version Minecraft version (like 1.17.1)
     * @param modLoaderVersion Mod loader version (like 37.0.33 for Forge), do NOT pass a version like 1.17.1-37.0.33!
     * @param modLoader The type of mod loader.
     * @return the launched process
     * @throws Exception throws an exception if an error has occurred.
     */
    public Process launch(String version, String modLoaderVersion, ModLoader modLoader) throws Exception
    {
        final Logger logger = Logger.getLogger("OpenLauncherLib");
        final Path vanillaJson = this.customVanillaJsonFileName.equals("") ? this.gameDir.resolve(version + ".json") : this.gameDir.resolve(this.customVanillaJsonFileName);
        final JSONObject vanilla = new JSONReader(logger, vanillaJson).toJSONObject();

        JSONObject modLoaderJsonObject = null;

        if(modLoader != ModLoader.VANILLA)
        {
            final Path modLoaderJson = this.customModLoaderJsonFileName.equals("") ? this.gameDir.resolve(modLoader.jsonFileNameProvider.apply(version, modLoaderVersion)) : this.gameDir.resolve(this.customModLoaderJsonFileName);
            modLoaderJsonObject = new JSONReader(logger, modLoaderJson).toJSONObject();
        }

        LogUtil.info("no-framework");

        final fr327.theshark34.openlauncherlib.external.ExternalLauncher launcher = new fr327.theshark34.openlauncherlib.external.ExternalLauncher(new ExternalLaunchProfile(
                modLoaderJsonObject != null ? modLoaderJsonObject.getString("mainClass") : vanilla.getString("mainClass"),
                this.getClassPath(vanilla, modLoaderJsonObject),
                this.getVmArgs(vanilla, modLoaderJsonObject),
                this.getArgs(vanilla, modLoaderJsonObject),
                true,
                this.serverName.equals("") ? "Minecraft " + version : this.serverName,
                this.gameDir
        ));

        if(this.lastCallback != null)
            this.lastCallback.accept(launcher);

        return launcher.launch();
    }

    private List<String> getVmArgs(JSONObject vanilla, JSONObject modLoader)
    {
        final List<String> result = new ArrayList<>(this.getVmArgsFor(vanilla, vanilla));
        if(modLoader != null)
            result.addAll(this.getVmArgsFor(modLoader, vanilla));
        result.addAll(this.additionalVmArgs);
        return result;
    }

    private List<String> getVmArgsFor(JSONObject object, JSONObject vanilla)
    {
        final Parameters parameters = new Parameters();
        parameters.vanilla = vanilla;
        parameters.processing = object;

        final List<String> sb = new ArrayList<>();

        if(object.isNull("arguments"))
        {
            sb.add("-Djava.library.path=" + this.map("${natives_directory}", parameters));
            return sb;
        }

        final JSONObject arguments = object.getJSONObject("arguments");

        if(arguments.isNull("jvm")) return sb;

        final JSONArray array = object.getJSONObject("arguments").getJSONArray("jvm");

        for (Object element : array)
        {
            if(element instanceof String)
            {
                final String arg = (String)element;

                if(arg.contains("minecraft.launcher") || arg.contains("${classpath}") || arg.equals("-cp")) continue;

                sb.add(this.map(arg, parameters));
            }
        }

        return sb;
    }

    private String getClassPath(JSONObject vanilla, JSONObject modLoader)
    {
        final List<String> cp = new ArrayList<>();

        if (modLoader != null)
            this.appendLibraries(cp, modLoader);
        this.appendLibraries(cp, vanilla);

        cp.add(this.gameDir.toAbsolutePath().resolve(this.clientJar).toString());

        return this.toString(cp);
    }

    private void appendLibraries(List<String> sb, JSONObject object)
    {
        object.getJSONArray("libraries").forEach(jsonElement -> {
            final JSONObject libraryObject = ((JSONObject)jsonElement);
            final Path path;
            if(libraryObject.isNull("downloads"))
            {
                final String[] nameParts = libraryObject.getString("name").split(":");
                path = this.libraries.resolve(nameParts[0].replace('.', '/')).resolve(nameParts[1]).resolve(nameParts[2]).resolve(nameParts[1] + "-" + nameParts[2] + ".jar");
            }
            else
            {
                final JSONObject downloads = libraryObject.getJSONObject("downloads");

                if(downloads.isNull("artifact"))
                    return;
                path = this.libraries.resolve(downloads.getJSONObject("artifact").getString("path"));
            }
            final String str = path.toAbsolutePath() + File.pathSeparator;
            if(!sb.contains(str) && Files.exists(path))
                sb.add(str);
        });
    }

    private List<String> getArgs(JSONObject vanilla, JSONObject modLoader)
    {
        final Parameters parameters = new Parameters();
        parameters.vanilla = vanilla;
        parameters.processing = modLoader != null ? modLoader : vanilla;

        final List<String> result = new ArrayList<>(this.getArgs(vanilla, parameters));
        if(modLoader != null)
            result.addAll(this.getArgs(modLoader, parameters));
        result.addAll(this.additionalArgs);
        return result;
    }

    private List<String> getArgs(JSONObject object, Parameters parameters)
    {
        if(object.isNull("arguments"))
        {
            if(object.isNull("minecraftArguments") || object == parameters.vanilla)
                return new ArrayList<>();

            final List<String> sb = new ArrayList<>();
            for (final String s : object.getString("minecraftArguments").split(" "))
                sb.add(this.map(s, parameters));
            return sb;
        }
        final JSONObject arguments = object.getJSONObject("arguments");
        final JSONArray array = arguments.getJSONArray("game");

        final List<String> sb = new ArrayList<>();

        for (Object element : array)
        {
            if(element instanceof String)
                sb.add(this.map((String)element, parameters));
        }

        return sb;
    }

    private String map(String str, Parameters parameters)
    {
        if(str.contains("${version_name}.jar")) return str.replace("${version_name}.jar", this.clientJar);
        String result = str;
        for(Map.Entry<String, Function<Parameters, String>> entry : keyValue.entrySet())
            result = result.replace(entry.getKey(), entry.getValue().apply(parameters));

        return result;
    }

    private String toString(List<String> stringList)
    {
        final StringBuilder sb = new StringBuilder();
        stringList.forEach(sb::append);
        return sb.toString();
    }

    public List<String> getAdditionalArgs()
    {
        return this.additionalArgs;
    }

    public List<String> getAdditionalVmArgs()
    {
        return this.additionalVmArgs;
    }

    public String getCustomVanillaJsonFileName()
    {
        return this.customVanillaJsonFileName;
    }

    public String getCustomModLoaderJsonFileName()
    {
        return this.customModLoaderJsonFileName;
    }

    public String getServerName()
    {
        return this.serverName;
    }

    public SafeConsumer<fr327.theshark34.openlauncherlib.external.ExternalLauncher> getLastCallback()
    {
        return this.lastCallback;
    }

    /**
     * Define some additional args (like window size or server ip/port)
     * @param additionalArgs args to add.
     */
    public void setAdditionalArgs(List<String> additionalArgs)
    {
        this.additionalArgs = additionalArgs;
    }

    /**
     * Define some additional VM args (like RAM with -Xmx2G)
     * @param additionalVmArgs VM args to add.
     */
    public void setAdditionalVmArgs(List<String> additionalVmArgs)
    {
        this.additionalVmArgs = additionalVmArgs;
    }

    /**
     * Define a custom json file's path (start is {@link #gameDir}). (Vanilla)
     * @param customVanillaJsonFileName custom json file's path.
     */
    public void setCustomVanillaJsonFileName(String customVanillaJsonFileName)
    {
        this.customVanillaJsonFileName = customVanillaJsonFileName;
    }

    /**
     * Define a custom json file's path (start is {@link #gameDir}).
     * @param customModLoaderJsonFileName custom json file's path.
     */
    public void setCustomModLoaderJsonFileName(String customModLoaderJsonFileName)
    {
        this.customModLoaderJsonFileName = customModLoaderJsonFileName;
    }

    /**
     * Define a custom serverName for Mac users. Default is Minecraft {version}
     * @param serverName the server name.
     */
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    /**
     * Define a callback called before launch. You can directly interact with a {@link fr327.theshark34.openlauncherlib.external.ExternalLauncher} object.
     * @param lastCallback the last callback.
     */
    public void setLastCallback(SafeConsumer<ExternalLauncher> lastCallback)
    {
        this.lastCallback = lastCallback;
    }

    /**
     * Get the view of the parameters to map
     * @return the view of the parameters to map
     */
    public Map<String, Function<Parameters, String>> boardView()
    {
        return Collections.unmodifiableMap(this.keyValue);
    }

    /**
     * Define new parameters to map
     * @param key key
     * @param value value
     */
    public void putArgument(String key, Function<Parameters, String> value)
    {
        this.keyValue.putIfAbsent(key, value);
    }

    /**
     * Replace an existing mapping function for a parameter
     * @param key key
     * @param value value
     */
    public void replaceArgument(String key, Function<Parameters, String> value)
    {
        this.keyValue.replace(key, value);
    }
}
