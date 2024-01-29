let highlighted = document.getElementById("top");

document.getElementById('values').addEventListener('submit', function(event) {
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
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.get("tab")!=null) {
        const id = urlParams.get("tab");
        document.getElementById(id).querySelector("div").classList.add("active");
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
        document.getElementById("open-mods").style.display = "none";
    } else {
        document.getElementById("open-instance").style.display = "inherit";
        document.getElementById("open-mods").style.display = "inherit";
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
    document.getElementById("description").innerText = name;
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
    document.getElementById("version").innerText = version;
    document.getElementById("minecraft").innerText = minecraft;
    document.getElementById("modloader").innerText = modloader;
    document.getElementById("mlversion").innerText = mlversion;
    document.getElementById("launch").onclick = function () {
        callJavaMethod("button.start."+id);
    };
}

function addInstance() {
    document.getElementById("instance-view").style.display = "none";
    document.getElementById("instance-adder").style.display = "inherit";
}

function installZyneonPlus() {
    callJavaMethod("button.install.official/zyneonplus/"+document.getElementById("zyneonplus-version").value);
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