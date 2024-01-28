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
    document.getElementById("instance-view").style.display = "inherit";
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