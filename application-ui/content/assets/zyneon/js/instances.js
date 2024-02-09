let highlighted = document.getElementById("top");

document.getElementById('values').addEventListener('submit', function(event) {
    event.preventDefault();
});

document.getElementById('settings').addEventListener('submit', function(event) {
    event.preventDefault();
});

function highlight(id) {
    const element = document.getElementById(id);
    const newHighlight = element.querySelector("div");
    if(newHighlight) {
        if(highlighted !== null) {
            if(highlighted !== undefined) {
                highlighted.classList.remove("active");
            }
        }
        highlighted = newHighlight;
        newHighlight.classList.add("active");
    }
}

function syncInstanceList() {
    callJavaMethod("sync.instances.list");
}

function loadTab() {
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.get("tab")!=null) {
        syncInstance(urlParams.get("tab"));
    }
}

function addInstanceToList(id,name,png) {
    const base = document.getElementById("list-template");
    const instance = base.cloneNode(true);
    instance.id = id;
    base.parentNode.insertBefore(instance, base);
    const a = instance.querySelector("a");
    if (a) {
        a.onclick = function () {
            syncInstance(id);
        }
    }
    const i = instance.querySelector("i");
    if(i) {
        if(!id.includes("official/")) {
            i.style.display = "none";
        }
    }
    const span = instance.querySelector("span");
    if (span) {
        span.innerText = name;
    }
    if (png !== undefined) {
        if (png !== "") {
            const img = instance.querySelector("img");
            if (img) {
                img.src = png;
            }
        }
    }
}

function syncInstance(id) {
    closeSettings();
    document.getElementById("instance-adder").style.display = "none";
    document.getElementById("instance-view").style.display = "inherit";
    document.getElementById("open-resourcepacks").onclick = function () { callJavaMethod("button.resourcepacks."+id); };
    document.getElementById("open-screenshots").onclick = function () { callJavaMethod("button.screenshots."+id); };
    document.getElementById("open-shaderpacks").onclick = function () { callJavaMethod("button.shaders."+id); };
    document.getElementById("open-worlds").onclick = function () { callJavaMethod("button.worlds."+id); };
    document.getElementById("open-instance").onclick = function () { callJavaMethod("button.folder."+id); };
    document.getElementById("open-mods").onclick = function () { callJavaMethod("button.mods."+id); };

    if(id.includes("official/")) {
        document.getElementById("open-instance").style.display = "none";
        document.getElementById("content").style.display = "none";
        document.getElementById("open-mods").style.display = "none";
        document.getElementById("local-settings").style.display = "none";
        document.getElementById("local-appearance").style.display = "none";
        document.getElementById("check").style.display = "inherit";
    } else {
        document.getElementById("check").style.display = "none";
        document.getElementById("content").style.display = "inline";
        document.getElementById("open-instance").style.display = "inherit";
        document.getElementById("open-mods").style.display = "inherit";
        document.getElementById("local-settings").style.display = "inherit";
        document.getElementById("local-appearance").style.display = "inherit";
    }

    document.getElementById("configure-memory").onclick = function () { callJavaMethod("button.settings."+id); };
    document.getElementById("delete-instance").onclick = function () { callJavaMethod("button.delete."+id); };

    highlight(id);
    callJavaMethod("button.instance." + id);
}

function syncTitle(name,png) {
    document.getElementById("title").innerText = name;
    if(png !== undefined) {
        if(png !== "") {
            document.getElementById("icon").src = png;
        } else {
            document.getElementById("icon").src = "assets/zyneon/images/instances/default.png";
        }
    } else {
        document.getElementById("icon").src = "assets/zyneon/images/instances/default.png";
    }
}

function syncLogo(png) {
    if(png !== undefined) {
        if(png !== "") {
            document.getElementById("logo").src = png;
        } else {
            document.getElementById("logo").src = "assets/zyneon/images/instances/default-logo.png";
        }
    } else {
        document.getElementById("logo").src = "assets/zyneon/images/instances/default-logo.png";
    }
}

function syncDescription(description) {
    document.getElementById("description").innerText = description;
}

function syncBackground(png) {
    if(png !== undefined) {
        if(png !== "") {
            document.getElementById('instance-view').style.backgroundImage="url("+png+")";
        } else {
            document.getElementById('instance-view').style.backgroundImage="none";
        }
    } else {
        document.getElementById('instance-view').style.backgroundImage="none";
    }
}

function syncDock(id,version,minecraft,modloader,mlversion) {
    launchDefault();
    document.getElementById("version").innerText = version;
    document.getElementById("minecraft").innerText = minecraft;
    document.getElementById("modloader").innerText = modloader;
    document.getElementById("mlversion").innerText = mlversion;
    document.getElementById("launch").onclick = function () {
        callJavaMethod("button.start."+id);
    };
}

function launchUpdate() {
    document.getElementById("launch").innerHTML = "<i class='bx bx-loader-alt bx-spin bx-rotate-90' ></i> UPDATING";
}

function launchStarted() {
    document.getElementById("launch").innerHTML = "<i class='bx bx-check'></i> STARTED";
}

function launchDefault() {
    document.getElementById("launch").innerHTML = "<i class='bx bxs-rocket'></i> LAUNCH";
}

function syncSettings(id,ram,name,version,description,minecraft,modloader,mlversion,icon,logo,background) {
    document.getElementById("settings-name").value = name;
    document.getElementById("settings-version").value = version;
    document.getElementById("settings-description").value = description;
    document.getElementById("settings-minecraft").value = minecraft;
    document.getElementById("settings-modloader").value = modloader;
    document.getElementById("settings-mlversion").value = mlversion;
    document.getElementById("memory-int").innerText = ram;
    document.getElementById("memory-int").style.display = "inline";
    document.getElementById("settings-save").onclick = function () { updateInstance(id); };
    document.getElementById("content").onclick = function () {
        if(modloader==="Fabric") {
            link("resources.html?s=modrinth&t="+modloader.toLowerCase()+"&v="+minecraft+"&d=forge&i="+id);
        } else if(modloader==="Forge") {
            link("resources.html?s=modrinth&t="+modloader.toLowerCase()+"&v="+minecraft+"&d=fabric&i="+id);
        } else {
            showSettings();
        }
    };
    document.getElementById("configure-icon").onclick = function () { callJavaMethod("button.change.icon."+id); };
    document.getElementById("configure-logo").onclick = function () { callJavaMethod("button.change.logo."+id); };
    document.getElementById("configure-background").onclick = function () { callJavaMethod("button.change.background."+id); };
    if(icon !== undefined) {
        if(icon !== "") {
            document.getElementById("show-icon").style.display = "inline";
            document.getElementById("show-icon").onclick = function () { callJavaMethod("button.icon."+id); };
        }
    }
    if(logo !== undefined) {
        if(logo !== "") {
            document.getElementById("show-logo").style.display = "inline";
            document.getElementById("show-logo").onclick = function () { callJavaMethod("button.logo."+id); };
        }
    }
    if(background !== undefined) {
        if(background !== "") {
            document.getElementById("show-background").style.display = "inline";
            document.getElementById("show-background").onclick = function () { callJavaMethod("button.background."+id); };
        }
    }
}

function showSettings() {
    document.getElementById("sidebyside").style.display = "none";
    document.getElementById("instance-settings").style.display = "inherit";
    document.getElementById("configure-instance").innerText = "Close settings";
}

function closeSettings() {
    document.getElementById("instance-settings").style.display = "none";
    document.getElementById("sidebyside").style.display = "inherit";
    document.getElementById("configure-instance").innerText = "Settings";
}

function toggleSettings() {
    if(document.getElementById("instance-settings").style.display === "none") {
        showSettings();
    } else {
        closeSettings();
    }
}

function addInstance() {
    document.getElementById("instance-view").style.display = "none";
    document.getElementById("instance-adder").style.display = "inherit";
}

function installZyneonPlus() {
    callJavaMethod("button.install.official/zyneonplus/"+document.getElementById("zyneonplus-version").value);
}

function installInstanceID() {
    id = document.getElementById("id").value;
    callJavaMethod('button.install.'+id);
}

function validateInstanceCreator() {
    const instanceCreator = document.getElementById('values');
    if (instanceCreator.checkValidity()) {
        const name = document.getElementById('creator-name');
        const version = document.getElementById('creator-version');
        const minecraft = document.getElementById('creator-minecraft');
        const modloader = document.getElementById('creator-modloader');
        const mlversion = document.getElementById('creator-mlversion');

        const n = name.value;
        const v = version.value;
        const m = minecraft.value;
        let l;
        let k;
        if(mlversion.value!=null) {
            l = modloader.value;
            k = mlversion.value;
        } else {
            l = "Vanilla";
            k = "";
        }

        const fN = n.replace(/\./g, "%DOT%");
        const fV = v.replace(/\./g, "%DOT%");
        const fM = m.replace(/\./g, "%DOT%");
        const fL = l.replace(/\./g, "");
        const fK = k.replace(/\./g, "%DOT%");

        callJavaMethod('button.creator.create.'+fN+'.'+fV+'.'+fM+'.'+fL+'.'+fK);
    }
    return false;
}

function updateInstance(id) {
    const instanceCreator = document.getElementById('settings');
    if (instanceCreator.checkValidity()) {
        const name = document.getElementById('settings-name');
        const version = document.getElementById('settings-version');
        const minecraft = document.getElementById('settings-minecraft');
        const modloader = document.getElementById('settings-modloader');
        const mlversion = document.getElementById('settings-mlversion');
        const description = document.getElementById('settings-description');

        const n = name.value;
        const v = version.value;
        const m = minecraft.value;
        let l;
        let k;
        const d = description.value;
        if(mlversion.value!=null) {
            l = modloader.value;
            k = mlversion.value;
        } else {
            l = "Vanilla";
            k = "";
        }

        const fN = n.replace(/\./g, "%DOT%");
        const fV = v.replace(/\./g, "%DOT%");
        const fM = m.replace(/\./g, "%DOT%");
        const dS = d.replace(/\./g, "%DOT%");
        const fL = l.replace(/\./g, "");
        const fK = k.replace(/\./g, "%DOT%");

        callJavaMethod('button.creator.update.'+id+'.'+fN+'.'+fV+'.'+fM+'.'+fL+'.'+fK+'.'+dS);
    }
    return false;
}