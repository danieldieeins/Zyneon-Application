let lastTitle = "";
let lastHighlighted;
let highlighted;

function initLibrary() {
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.get("moduleId")!==null||localStorage.getItem("settings.lastLibraryModule")!==null) {
        let moduleId;
        if(urlParams.get("moduleId")!==null) {
            moduleId = urlParams.get('moduleId');
        } else {
            moduleId = localStorage.getItem("settings.lastLibraryModule");
        }
        if(moduleId!=="-1"&&moduleId!==-1) {
            connector("sync.library.module." + moduleId);
            document.getElementById("select-game-module").value = moduleId;
            return;
        }
    }
    const gameContent = document.getElementById("game-content");
    if(gameContent.classList.contains("active")) {
        gameContent.classList.remove("active");
    }
    const addModule = document.getElementById('add-game-module');
    if(!addModule.classList.contains("active")) {
        addModule.classList.add("active");
    }
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

function toggleLibraryOverview(title_) {
    const overview = document.getElementById("library-overview");
    const title = document.getElementById("title-name");
    overview.classList.toggle("active");
    if(overview.classList.contains("active")) {
        lastTitle = title.innerText;
        if(highlighted) {
            lastHighlighted = highlighted;
        }
        title.innerText = title_;
        highlight(document.getElementById("overview-button"));
    } else {
        title.innerText = lastTitle;
        if(lastHighlighted) {
            highlight(lastHighlighted);
        } else {
            if(highlighted) {
                highlighted.classList.remove("active");
                highlighted = null;
            }
        }
    }
}

function highlight(element) {
    if(highlighted) {
        highlighted.classList.remove("active");
    }
    element.classList.add("active");
    highlighted = element;
}

function addAction(title,iconClass,onclick,id) {
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