package fr327.flowarg.openlauncherlib;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ModifiedByFlow
public class NewForgeVersionDiscriminator
{
	/** e.g : 32.0.2 */
    private final String forgeVersion;
    /** e.g : 1.15.2 */
    private final String mcVersion;
    /** net.minecraftforge by default */
    private final String forgeGroup;
    /** e.g : 20200625.160719 */
    private final String mcpVersion;

    public NewForgeVersionDiscriminator(Path directory, String mcVersion, String forgeVersion) throws Exception
    {
        this(directory.resolve(mcVersion + "-forge-" + forgeVersion + ".json"));
    }

    public NewForgeVersionDiscriminator(Path forgeVersionJson) throws Exception
    {
        final List<String> lines = Files.readAllLines(forgeVersionJson, StandardCharsets.UTF_8);

        final StringBuilder sb = new StringBuilder();
        lines.forEach(sb::append);

        final JSONArray array = new JSONObject(sb.toString()).getJSONObject("arguments").getJSONArray("game");

        this.forgeVersion = array.getString(3);
        this.mcVersion = array.getString(5);
        this.forgeGroup = array.getString(7);
        this.mcpVersion = array.getString(9);
    }

    public NewForgeVersionDiscriminator(String forgeVersion, String mcVersion, String mcpVersion)
    {
        this(forgeVersion, mcVersion, "net.minecraftforge", mcpVersion);
    }

    public NewForgeVersionDiscriminator(String forgeVersion, String mcVersion, String forgeGroup, String mcpVersion)
    {
        this.forgeVersion = forgeVersion;
        this.mcVersion = mcVersion;
        this.forgeGroup = forgeGroup;
        this.mcpVersion = mcpVersion;
    }

    public String getForgeVersion()
    {
        return this.forgeVersion;
    }

    public String getMcVersion()
    {
        return this.mcVersion;
    }

    public String getForgeGroup()
    {
        return this.forgeGroup;
    }

    public String getMcpVersion()
    {
        return this.mcpVersion;
    }
}
