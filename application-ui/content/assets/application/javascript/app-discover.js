let searchTerm = "Click to search";
let moduleId = "-1";
let query = "";
let offset = 0;

function initDiscover() {
    const urlParams = new URLSearchParams(window.location.search);

    if(urlParams.get("moduleId")!==null||localStorage.getItem("settings.lastSearchModule")!==null) {
        if(urlParams.get("moduleId")!==null) {
            moduleId = urlParams.get('moduleId');
            if(moduleId==="modules") {
                moduleId = "-1";
            }
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
            connector("sync.discover.search."+moduleId.replaceAll("-1","modules")+"."+offset+"."+query);
        } else {
            connector("sync.discover.search." + moduleId.replaceAll("-1", "modules")+"."+offset);
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

function addFilterGroup(id,name) {
    if(id&&name) {
        const template = document.getElementById("filter-group-template");
        const group = template.cloneNode(true);
        group.id = id;
        group.querySelector("h4").innerText = name;
        template.parentNode.insertBefore(group,template);
    }
}

function addToggleFilter(name,groupId,onclick,fullWidth,disable) {
    if(groupId&&name) {
        const template = document.getElementById(groupId).querySelector("#toggle-template");
        const filter = template.cloneNode(true);
        filter.id = groupId+"-"+name.replaceAll(" ", "-").replace(/[^a-z0-9-_]/gi, '').toLowerCase();
        if(name) {
            filter.querySelector("p").innerText = name;
        }
        if(fullWidth===true) {
            filter.classList.add('max');
        }
        const slider = filter.querySelector(".toggle-slider");
        let disabled = false;
        if(onclick) {
            if(typeof onclick === 'function') {
                slider.onclick = onclick;
            } else if(typeof onclick === "string") {
                slider.onclick = function () {
                    const sliderValue = slider.querySelector(".slider").classList;
                    sliderValue.toggle("active");
                    connector(onclick+"."+slider.querySelector(".slider").classList.contains("active"));
                }
            } else {
                disabled = true;
            }
        } else {
            disabled = true;
        }
        if(disabled||disable===true) {
            slider.classList.add("disabled");
            slider.onclick = null;
        }
        template.parentNode.insertBefore(filter,template.parentNode.querySelector(".group-bottom"));
    }
}

function addSelectFilter(name,groupId,onchange,options,disable) {
    if(groupId&&name) {
        const template = document.getElementById(groupId).querySelector("#select-template");
        const filter = template.cloneNode(true);
        filter.id = groupId+"-"+name.replaceAll(" ", "-").replace(/[^a-z0-9-_]/gi, '').toLowerCase();
        filter.classList.add("max");
        const select = filter.querySelector("select");
        select.id = filter.id+"-select";
        let disabled = false;
        if(onchange) {
            if(typeof onchange === 'function') {
                select.onchange = onchange;
            } else if(typeof onchange === 'string') {
                select.onchange = function () {
                    connector(onchange+"."+select.value);
                }
            } else {
                disabled = true;
            }
        } else {
            disabled = true;
        }
        if(options) {
            select.innerHTML = options;
        } else {
            disabled = true;
        }
        if(disabled||disable===true) {
            select.disabled = true;
        }
        template.parentNode.insertBefore(filter,template.parentNode.querySelector(".group-bottom"));
    }
}

let activeTab = "";
function showTab(tabId) {
    const tab = document.getElementById("discover-"+tabId);
    const button = document.getElementById(tabId+"-button");
    if(activeTab) {
        document.getElementById(activeTab+"-button").classList.remove("active");
        document.getElementById("discover-"+activeTab).classList.remove("active");
    }
    activeTab = tabId;
    tab.classList.add("active");
    button.classList.add("active");
}

addEventListener("DOMContentLoaded", () => {
    document.getElementById("load-more").onclick = function () {
        offset = offset+20;
        connector("sync.discover.search." + moduleId.replaceAll("-1", "modules")+"."+offset);
    }
    showTab("home");
    initDiscover();
    setMenuPanel("", "web app", "undefined version", true);

    let theme = document.getElementById("css-colors").href;
    if(theme.includes("app-colors-light.css")) {
        theme="default.light";
    } else if(theme.includes("app-colors-zyneon.css")) {
        theme="default.zyneon";
    } else {
        theme="default.dark";
    }
    document.getElementById("news-frame").src="https://danieldieeins.github.io/Zyneon-Application/news/news.html?type=new&theme="+theme;

    document.getElementById("search-bar").addEventListener('keydown', function(event) {
        if (event.keyCode === 13) {
            const value = document.getElementById("search-bar").value;
            location.href = "discover.html?moduleId="+moduleId+"&l=search&s="+encodeURIComponent(value);
        }
    });
});