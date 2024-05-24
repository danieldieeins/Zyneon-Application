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
            const gameContent = document.getElementById("game-content");
            if (!gameContent.classList.contains("active")) {
                gameContent.classList.add("active");
            }
            const addModule = document.getElementById('add-game-module');
            if (addModule.classList.contains("active")) {
                addModule.classList.remove("active");
            }
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

function addModuleToList(title,moduleId) {
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