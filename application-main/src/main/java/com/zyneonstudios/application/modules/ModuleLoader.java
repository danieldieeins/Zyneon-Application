package com.zyneonstudios.application.modules;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zyneonstudios.application.main.ApplicationStorage;
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
        if(ApplicationStorage.getSettings().has("settings.modules.uninstall")) {
            ArrayList<String> uninstallModules = (ArrayList<String>)ApplicationStorage.getSettings().get("settings.modules.uninstall");
            for(String module:uninstallModules) {
                new File(module).delete();
            }
            ApplicationStorage.getSettings().delete("settings.modules.uninstall");
        }
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

    public HashMap<String, String> moduleJars = new HashMap<>();

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
                if(moduleJars.keySet().contains(id)) {
                    return null;
                }
                name = array.get(0).getAsJsonObject().get("name").getAsString();
                version = array.get(0).getAsJsonObject().get("version").getAsString();
                authors = array.get(0).getAsJsonObject().get("authors").getAsJsonArray().toString();
            } catch (Exception e) {
                NexusApplication.getLogger().err("[MODULES] Couldn't read module "+moduleJar.getPath()+": "+e.getMessage());
                return null;
            }
            URLClassLoader classLoader = new URLClassLoader(new URL[]{moduleJar.toURI().toURL()});
            Class<?> module = classLoader.loadClass(mainPath);
            Constructor<?> constructor = module.getConstructor(NexusApplication.class, String.class, String.class, String.class, String.class);
            moduleJars.put(id,moduleJar.getAbsolutePath().replace("\\","/"));
            return (ApplicationModule) constructor.newInstance(application, id, name, version, authors);
        } catch (Exception e) {
            NexusApplication.getLogger().err("[MODULES] Couldn't read module "+moduleJar.getPath()+": "+e.getMessage());
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