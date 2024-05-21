package com.zyneonstudios.application.modules;

import com.zyneonstudios.application.main.NexusApplication;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

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

    public ApplicationModule readModule(File moduleJar) {
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{moduleJar.toURI().toURL()});
            Class<?> module = classLoader.loadClass("live.nerotv.requestreader.RequestReader");
            Constructor<?> constructor = module.getConstructor(NexusApplication.class);
            return (ApplicationModule) constructor.newInstance(application);
        } catch (Exception e) {
            NexusApplication.getLogger().error("[MODULES] Couldn't read module "+moduleJar.getPath()+": "+e.getMessage());
            return null;
        }
    }

    public void loadModule(ApplicationModule module) {
        NexusApplication.getLogger().log("[MODULES] Loading module "+module.getId()+" v"+module.getVersion()+" by "+module.getAuthor()+"...");
        if(!modules.containsValue(module)) {
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

    public void unloadModules() {
        for(ApplicationModule module: modules.values()) {
            unloadModule(module);
        }
        modules = new HashMap<>();
        System.gc();
    }
}