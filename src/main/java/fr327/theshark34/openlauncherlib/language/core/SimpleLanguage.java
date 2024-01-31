/*
 * Copyright 2019 NeutronStars
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr327.theshark34.openlauncherlib.language.core;

import fr327.theshark34.openlauncherlib.language.api.Language;
import fr327.theshark34.openlauncherlib.language.api.LanguageInfo;
import fr327.theshark34.openlauncherlib.language.api.LanguageManager;
import fr327.flowarg.openlauncherlib.ModifiedByFlow;
import fr327.theshark34.openlauncherlib.configuration.api.Configuration;
import fr327.theshark34.openlauncherlib.configuration.core.SimpleConfiguration;
import org.json.JSONObject;

/**
 * Default Class of Language.
 *
 * @author NeutronStars
 * @version 1.0.0
 */
public class SimpleLanguage implements Language
{
    private final fr327.theshark34.openlauncherlib.configuration.api.Configuration configuration;
    private final LanguageManager manager;
    private final LanguageInfo name;

    public SimpleLanguage(LanguageInfo name, LanguageManager manager, LanguageInfo identify, fr327.theshark34.openlauncherlib.configuration.api.Configuration configuration)
    {
        this.name = name;
        this.manager = manager;
        this.configuration = new SimpleConfiguration(manager.getLogger(), new JSONObject().put(identify.get(), configuration.get(new JSONObject())));
    }

    public void registerLanguage(LanguageInfo identify, Configuration configuration)
    {
        this.configuration.set(configuration.get(new JSONObject()), identify.get());
    }

    /**
     * Retrieve the name of language.
     *
     * @return the name of language.
     */
    @Override
    public String getName()
    {
        return name.get();
    }

    /**
     * Retrieve the translated by nodes and the identifier.
     *
     * @param identify Main key for get the translate.
     * @param nodes    Key in the file of the translate.
     * @return the translated string.
     */
    @ModifiedByFlow
    @Override
    public String get(LanguageInfo identify, String... nodes)
    {
        if (nodes.length == 0) return identify.get();

        final String[] buildNodes = new String[nodes.length + 1];

        buildNodes[0] = identify.get();
        System.arraycopy(nodes, 0, buildNodes, 1, nodes.length);

        final StringBuilder builder = new StringBuilder();

        for (String node : nodes) builder.append(node);

        if (configuration.has(buildNodes) || manager.isDefaultLanguage(name))
            return configuration.get(builder.toString(), buildNodes);

        final Language language = manager.getDefaultLanguage();

        return language != null ? language.get(identify, nodes) : builder.toString();
    }
}
