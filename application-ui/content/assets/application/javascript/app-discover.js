let searchTerm = "Click to search";
let moduleId = "-1";
let query = "";

function initDiscover() {
    const urlParams = new URLSearchParams(window.location.search);

    if(urlParams.get("moduleId")!==null||localStorage.getItem("settings.lastSearchModule")!==null) {
        if(urlParams.get("moduleId")!==null) {
            moduleId = urlParams.get('moduleId');
            localStorage.setItem("settings.lastSearchModule",moduleId);
        } else {
            moduleId = localStorage.getItem("settings.lastSearchModule");
        }


        if(moduleId!=="-1"&&moduleId!==-1) {
            document.getElementById("search-type-select").value = moduleId;
        } else {
            document.getElementById("search-type-select").value = moduleId;
        }
    }

    if(urlParams.get("s")) {
        query = decodeURIComponent(urlParams.get("s"));
        document.getElementById("search-bar").value = query;
        document.getElementById('search-bar').placeholder = query;
    }

    if(urlParams.get("l")) {
        if(urlParams.get("l")==="search") {
            document.getElementById('search-bar').disabled = false;
            openSearch();
        }
    }

    console.log("[CONNECTOR] init.discover");
}

function openSearch() {
    const search = document.getElementById("discover-search");
    if(!search.classList.contains('active')) {
        search.classList.add('active');
        if(query) {
            connector("sync.discover.search."+moduleId.replaceAll("-1","modules")+"."+query);
        } else {
            connector("sync.discover.search." + moduleId.replaceAll("-1", "modules"));
        }
    }

    const buttons = document.getElementById("search-buttons");
    if(!buttons.classList.contains('active')) {
        buttons.classList.add('active');
    }

    const start = document.getElementById("discover-buttons");
    if(!start.classList.contains('active')) {
        start.classList.add('active');
    }

    const bar = document.getElementById("search-card");
    if(!bar.classList.contains('active')) {
        bar.classList.add('active');
    }

    deactivateMenu("menu",true);
    document.getElementById("search-bar").placeholder = "Goggles";
}

function closeSearch() {
    window.location = "discover.html?moduleId="+moduleId;
}

function toggleSearch() {
    const search = document.getElementById("discover-search");
    if(search.classList.contains('active')) {
        closeSearch();
    } else {
        openSearch();
    }
}

function addModuleToList(title,moduleId,image) {
    const template = document.getElementById('add-module-option');
    const entry = template.cloneNode(true);
    entry.value = moduleId;
    entry.innerText = title;
    template.parentNode.insertBefore(entry,template);
}

function addResult(id,img,title,authors,description,meta,actions,location,connectorRequest) {
    const template = document.getElementById("result-template");
    if(template) {
        const result = template.cloneNode(true);
        if(id) {
            result.id = id;
            result.querySelector("img").onclick = function () {
                if(connectorRequest) {
                    connector(connectorRequest);
                } else {
                    connector("sync.discover.details.module." + location);
                }
            };
            result.querySelector("a").onclick = function () {
                if(connectorRequest) {
                    connector(connectorRequest);
                } else {
                    connector("sync.discover.details.module." + location);
                }
            };
        } else {
            result.id = "";
        }
        if(img) {
            result.querySelector("img").src = img;
        }
        if(title) {
            result.querySelector(".result-title").innerText = title;
        }
        if(authors) {
            result.querySelector(".result-authors").innerText = authors;
        }
        if(description) {
            description = decodeURL(description);
            result.querySelector(".result-description").innerHTML = description;
        }
        if(meta) {
            result.querySelector(".result-meta").innerHTML = meta;
        } else {
            result.querySelector(".result-meta").style.display = "none";
        }
        if(actions) {
            result.querySelector(".result-actions").innerHTML = actions;
        }
        template.parentNode.insertBefore(result,template);
    } else {
        error("Couldn't find result template.");
    }
}

addEventListener("DOMContentLoaded", () => {
    initDiscover();

    document.getElementById("search-bar").addEventListener('keydown', function(event) {
        if (event.keyCode === 13) {
            const value = document.getElementById("search-bar").value;
            if(value) {
                location.href = "discover.html?moduleId="+moduleId+"&l=search&s="+encodeURIComponent(value);
            }
        }
    });
});