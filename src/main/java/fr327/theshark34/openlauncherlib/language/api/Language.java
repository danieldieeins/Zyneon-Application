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
package fr327.theshark34.openlauncherlib.language.api;

/**
 * @author NeutronStars.
 * @version 1.0.0
 */
public interface Language
{
    /**
     * Retrieve the name of language.
     *
     * @return the name of language.
     */
    String getName();

    /**
     * Retrieve the translated by nodes and the identifier.
     *
     * @param identify Main key for get the translate.
     * @param nodes    Key in the file of the translate.
     * @return the translated string.
     */
    String get(LanguageInfo identify, String... nodes);
}
