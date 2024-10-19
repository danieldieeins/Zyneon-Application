let lastHighlighted = "";
let highlighted = "";
let moduleId = "shared";

function initLibrary(skipConnector) {
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.get("moduleId")!==null||localStorage.getItem("settings.lastLibraryModule")!==null) {
        if(urlParams.get("moduleId")!==null) {
            moduleId = urlParams.get('moduleId');
        } else {
            moduleId = localStorage.getItem("settings.lastLibraryModule");
        }
        if(moduleId!=="-1"&&moduleId!==-1) {
            if(skipConnector!==true) {
                connector("sync.library.module." + moduleId);
            }
            if(optionExists("select-game-module",moduleId)) {
                document.getElementById("select-game-module").value = moduleId;
            }
            if(urlParams.get("viewId")!==null) {
                showView(urlParams.get("viewId"));
            }
        }
    }
    console.log("[CONNECTOR] init.library");
}

function optionExists(selectId, value) {
    const selectElement = document.getElementById(selectId);
    const options = selectElement.options;
    for (let i = 0; i < options.length; i++) {
        if (options[i].value === value) {
            return true;
        }
    }
    return false;
}

function addModuleToList(title,moduleId,image) {
    const template = document.getElementById('add-module-option');
    const entry = template.cloneNode(true);
    entry.value = moduleId;
    entry.innerText = title;
    template.parentNode.insertBefore(entry,template);
}

function onModuleChange() {
    const module = document.getElementById('select-game-module').value;
    if(module) {
        if(module!=="-1") {
            localStorage.setItem("settings.lastLibraryModule",module);
            location.href = "library.html?moduleId="+module;
        } else {
            location.href = "library.html?moduleId=-1";
        }
    }
}

function highlight(id) {
    const highlight = document.getElementById(id);
    if(highlight) {
        const oldHighlight = document.getElementById(highlighted);
        if(oldHighlight) {
            if(oldHighlight.classList.contains("active")) {
                oldHighlight.classList.remove("active");
            }
        }
        if(!highlight.classList.contains("active")) {
            highlight.classList.add("active");
        }
        lastHighlighted = highlight;
        highlighted = id;
    }
}

function addAction(title,iconClass,onclick,id) {
    if(document.getElementById(id)) {
        return;
    }
    const actionTemplate = document.getElementById("group-actions-entry");
    const actionEntry = actionTemplate.cloneNode(true);
    actionEntry.querySelector("img").style.display = "none";
    actionEntry.querySelector("p").innerText = title;
    actionEntry.querySelector("i").classList = iconClass;
    actionEntry.id = id;
    if(onclick) {
        actionEntry.onclick = function () {
            eval(onclick);
        }
    }
    actionTemplate.parentNode.insertBefore(actionEntry,actionTemplate);
}

function addGroup(title,id) {
    if(document.getElementById(id)) {
        return;
    }
    const actionTemplate = document.getElementById("menu-group-template");
    const actionEntry = actionTemplate.cloneNode(true);
    actionEntry.querySelector("h3").innerText = title;
    actionEntry.querySelector("li").id = "template-"+id+"-entry";
    actionEntry.querySelector("li").style.display = "none";
    actionEntry.id = id;
    actionTemplate.parentNode.insertBefore(actionEntry,actionTemplate);
}

function addGroupEntry(groupId,title,id,image) {
    if(document.getElementById(id)) {
        return;
    }
    const actionTemplate = document.getElementById("template-"+groupId+"-entry");
    const actionEntry = actionTemplate.cloneNode(true);

    actionEntry.id = id;
    actionEntry.style.display = "inherit";
    actionEntry.onclick = function () {
        connector("sync.button.library.menu.group."+groupId+"."+id);
    }

    actionEntry.addEventListener("dblclick", function() {
        connector("async.java.button.launch."+id);
        actionEntry.querySelector("p").innerText = "Starting...";
        let seconds = 5;
        const countdownInterval = setInterval(() => {
            seconds--;
            if (seconds <= 0) {
                actionEntry.querySelector("p").innerText = title;
                clearInterval(countdownInterval);
            }
        }, 1000);
    });

    if(title) {
        actionEntry.querySelector("p").innerText = title;
    }

    const n = Math.floor(Math.random() * 6) + 1;
    let i = "";
    if(Math.random() < 0.5) {
        i="bx bx-dice-"+n;
    } else {
        i="bx bxs-dice-"+n;
    }
    if(image) {
        if(image!=="") {
            actionEntry.querySelector("i").style.display = "none";
            actionEntry.querySelector("img").src = image;
        } else {
            actionEntry.querySelector("img").style.display = "none";
            actionEntry.querySelector("i").classList = i;
        }
    } else {
        actionEntry.querySelector("img").style.display = "none";
        actionEntry.querySelector("i").classList = i;
    }

    actionTemplate.parentNode.insertBefore(actionEntry,actionTemplate);

    if(id === highlighted) {
        highlighted = "";
        highlight(actionEntry);
    }
}

function setTitle(img,text,options_) {
    const title = document.getElementById("title-name");
    const image = document.getElementById("title-image");
    const options = document.getElementById("title-options");
    if(text) {
        title.innerText = text;
    } else {
        title.innerText = ""
    }
    if(img) {
        image.style.display = "inherit";
        image.src = img;
    } else {
        image.style.display = "none";
        image.src = "";
    }
    if(options_) {
        options.style.display = "inherit";
        options.innerHTML = options_;
    } else {
        options.style.display = "none";
        options.innerHTML = "";
    }
    document.getElementById("library-overlay").classList.remove("active");
}

function toggleOverlay(button) {
    const overlay = document.getElementById("library-overlay");
    if(overlay.classList.contains("active")) {
        overlay.classList.remove("active");
        if(button) {
            document.getElementById(button).classList.remove("active");
        }
    } else {
        overlay.classList.add("active");
        if(button) {
            document.getElementById(button).classList.add("active");
        }
    }
}

function setOverlayContent(content) {
    document.getElementById("library-overlay").innerHTML = content;
}

function addOverlayContent(content) {
    document.getElementById("library-overlay").innerHTML += content;
}

function clearOverlay() {
    document.getElementById("library-overlay").innerHTML = "";
}

function setViewDescription(description) {
    const desc = document.getElementById("view-description");
    if(description) {
        desc.innerHTML = description;
    } else {
        desc.innerHTML = "<i class='bx bx-loader-alt bx-spin'></i> Loading...";
    }
}

function setViewImage(image_) {
    const image = document.getElementById("view-image");
    const container = document.getElementById("view-image-container");
    if(image_) {
        container.style.display = "inherit";
        image.src = image_;
    } else {
        container.style.display = "none";
        image.src = "";
    }
}

function showView(id) {
    document.getElementById("library-view").style.display = "inherit";
    document.querySelector(".cnt").style.backgroundImage = "url('')";
    setTitle(); setViewImage(); setViewDescription(); disableLaunch();
    connector("sync.library.module." + moduleId + ".view." + id);
    highlight(id);
}

function disableLaunch() {
    const button = document.getElementById("view-launch");
    setLaunch("LAUNCH","bx bx-x-circle");
}

let launchRequest = "";
function launch_() {
    if(launchRequest) {
        const button = document.getElementById("view-launch");
        if(button.classList.contains("active")) {
            connector(launchRequest);
        }
    }
}

function setLaunch(title,icon,className,connectorRequest) {
    const button = document.getElementById("view-launch");

    let launch = "LAUNCH";
    if(title) {
        launch = title;
    }
    button.querySelector("span").innerText = title;

    let i = "bx bx-rocket";
    if(icon) {
        i = icon;
    }
    button.querySelector("i").className = icon;

    let cN = "hover-wiggle";
    if(className) {
        cN = "hover-wiggle "+className;
    }
    button.className = cN;

    if(connectorRequest) {
        button.onclick = function () {
            launchRequest = connectorRequest;
            launch_();
            launchRequest = "";
        }
    } else {
        launchRequest = "";
        button.onclick = null;
    }
}

function enableLaunch() {
    const button = document.getElementById("view-launch");
    if(!button.classList.contains("active")) {
        button.classList.add("active");
    }
}

addEventListener("DOMContentLoaded", () => {
    disableLaunch(); initLibrary();
});