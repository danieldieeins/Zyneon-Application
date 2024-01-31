package fr327.flowarg.openlauncherlib;

import java.util.ArrayList;
import java.util.List;

@ModifiedByFlow
public interface IForgeArgumentsProvider
{
    default List<String> getForgeArguments()
    {
        final List<String> arguments = new ArrayList<>();
        arguments.add("--launchTarget");
        arguments.add("fmlclient");

        arguments.add("--fml.forgeVersion");
        arguments.add(this.getNFVD().getForgeVersion());

        arguments.add("--fml.mcVersion");
        arguments.add(this.getNFVD().getMcVersion());

        arguments.add("--fml.forgeGroup");
        arguments.add(this.getNFVD().getForgeGroup());

        arguments.add("--fml.mcpVersion");
        arguments.add(this.getNFVD().getMcpVersion());
        return arguments;
    }

    NewForgeVersionDiscriminator getNFVD();
}
