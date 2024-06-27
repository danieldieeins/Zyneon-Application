package com.zyneonstudios.application.modules;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zyneonstudios.application.main.NexusApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarFile;

public class ModuleLoader {

    private HashMap<String, ApplicationModule> modules = new HashMap<>();
    private final NexusApplication application;

    public ModuleLoader(NexusApplication application) {
        this.application = application;
    }

    public Collection<ApplicationModule> getApplicationModules() {
        return modules.values();
    }

    public HashMap<String, ApplicationModule> getModules() {
        return modules;
    }

    public Set<String> getModuleIds() {
        return modules.keySet();
    }

    @SuppressWarnings("all")
    public ApplicationModule readModule(File moduleJar) {
        try {
            String mainPath;
            String id;
            String name;
            String version;
            String authors;
            try (JarFile jarFile = new JarFile(moduleJar.getAbsolutePath())) {
                InputStream is = jarFile.getInputStream(jarFile.getJarEntry("nexus.json"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                JsonArray array = new Gson().fromJson(reader, JsonObject.class).getAsJsonArray("modules");
                mainPath = array.get(0).getAsJsonObject().get("main").getAsString();
                id = array.get(0).getAsJsonObject().get("id").getAsString();
                name = array.get(0).getAsJsonObject().get("name").getAsString();
                version = array.get(0).getAsJsonObject().get("version").getAsString();
                authors = array.get(0).getAsJsonObject().get("authors").getAsJsonArray().toString();
            } catch (Exception e) {
                NexusApplication.getLogger().error("[MODULES] Couldn't read module "+moduleJar.getPath()+": "+e.getMessage());
                return null;
            }
            URLClassLoader classLoader = new URLClassLoader(new URL[]{moduleJar.toURI().toURL()});
            Class<?> module = classLoader.loadClass(mainPath);
            Constructor<?> constructor = module.getConstructor(NexusApplication.class, String.class, String.class, String.class, String.class);
            return (ApplicationModule) constructor.newInstance(application, id, name, version, authors);
        } catch (Exception e) {
            NexusApplication.getLogger().error("[MODULES] Couldn't read module "+moduleJar.getPath()+": "+e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("all")
    public ArrayList<ApplicationModule> readModules(File moduleJar) {
        try {
            String mainPath;
            try (JarFile jarFile = new JarFile(moduleJar.getAbsolutePath())) {
                InputStream is = jarFile.getInputStream(jarFile.getJarEntry("nexus.json"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                JsonArray array = new Gson().fromJson(reader, JsonObject.class).getAsJsonArray("modules");
                ArrayList<ApplicationModule> modules = new ArrayList<>();
                for(JsonElement e:array) {
                    mainPath = e.getAsJsonObject().get("main").getAsString();
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{moduleJar.toURI().toURL()});
                    Class<?> module = classLoader.loadClass(mainPath);
                    Constructor<?> constructor = module.getConstructor(NexusApplication.class);
                    modules.add((ApplicationModule)constructor.newInstance(application));
                }
                if(modules.isEmpty()) {
                    return null;
                } else {
                    return modules;
                }
            } catch (Exception e) {
                NexusApplication.getLogger().error("[MODULES] Couldn't read module "+moduleJar.getPath()+": "+e.getMessage());
                return null;
            }
        } catch (Exception e) {
            NexusApplication.getLogger().error("[MODULES] Couldn't read module "+moduleJar.getPath()+": "+e.getMessage());
            return null;
        }
    }

    public void loadModule(ApplicationModule module) {
        if(!modules.containsKey(module.getId())) {
            NexusApplication.getLogger().log("[MODULES] Loading module "+module.getId()+" v"+module.getVersion()+" by "+module.getAuthors()+"...");
            modules.put(module.getId(),module);
            module.onLoad();
        }
    }

    public void activateModules() {
        for(ApplicationModule module:modules.values()) {
            module.onEnable();
        }
    }

    public void deactivateModules() {
        for(ApplicationModule module:modules.values()) {
            module.onDisable();
        }
    }

    public void unloadModule(ApplicationModule module) {
        modules.remove(module.getId());
    }

    @SuppressWarnings("unused")
    public void unloadModules() {
        for(ApplicationModule module: modules.values()) {
            unloadModule(module);
        }
        modules = new HashMap<>();
        System.gc();
    }
}