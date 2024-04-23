package com.zyneonstudios.old.integrations.index.zyndex.instance;

import com.zyneonstudios.old.Application;
import live.nerotv.shademebaby.file.Config;
import java.io.File;

public class InstanceSettings extends Config {

    private int memory = Application.memory;

    public InstanceSettings(Instance instance) {
        super(new File(instance.getPath()+"meta/instanceSettings.json"));
        if(get("settings.memory")!=null) {
            memory = getInt("settings.memory");
        }
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
        set("settings.memory",memory);
    }
}