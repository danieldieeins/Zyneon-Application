let updates = false;
let highlighted = undefined;
let content = undefined;

function toggleUpdates() {
    if(updates) {
        updates = false;
        connector('sync.autoUpdates.off');
    } else {
        updates = true;
        connector('sync.autoUpdates.on');
    }
}

function syncUpdates() {
    connector('sync.updateChannel.'+document.getElementById('updater-settings-update-channel').value);
}

function syncTheme() {
    setColors(document.getElementById("appearance-settings-theme").value);
}

function syncStartPage() {
    const startPage = document.getElementById("general-settings-start-page").value;
    connector("sync.startPage."+startPage);
    localStorage.setItem('settings.startPage', startPage);
}

function syncLanguage() {
    const language = document.getElementById("appearance-settings-language").value;
    connector("sync.language."+language);
    localStorage.setItem('settings.language', language);
    location.href = "../"+language+"/settings.html";
}

function syncVersion(version) {
    if(version) {
        log("Got version: "+version);
        const allTextNodes = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT);
        let node;
        while (node = allTextNodes.nextNode()) {
            node.nodeValue = node.nodeValue.replaceAll("${application.version}", version);
        }
    } else {
        error("No version specified");
    }
}

function addModuleSetting(icon,text,onclick,id,image) {
    const template = document.getElementById("settings-module-template");
    const entry = template.cloneNode(true);
    if(!id) {
        id="unidentified";
    }
    entry.id = id;

    const img = entry.querySelector('img');
    const i = entry.querySelector('i');
    if(image===true) {
        if(i) {
            i.remove();
        }
        if(img) {
            if(icon) {
                img.src = icon;
            }
        }
    } else {
        if(img) {
            img.remove();
        }
        if(i) {
            if(icon) {
                i.classList = icon;
            }
        }
    }

    const span = entry.querySelector("span");
    if(span) {
        if(text) {
            span.innerText = text;
        }
    }

    if(onclick) {
        entry.onclick = function() {
            connector(onclick);
        };
    }
    template.parentNode.insertBefore(entry,template);

    if(!document.getElementById("settings-modules-section").classList.contains("active")) {
        document.getElementById("settings-modules-section").classList.add("active");
    }
}

function highlight(newHighlight) {
    if(newHighlight) {
        if(!newHighlight.classList.contains("highlighted")) {
            newHighlight.classList.add("highlighted");
            if(highlighted) {
                if(highlighted.classList.contains("highlighted")) {
                    highlighted.classList.remove("highlighted");
                }
            }
            highlighted = newHighlight;
            setTitle(newHighlight.innerText)
        }
    }
}

function setTitle(title) {
    if(title) {
        document.getElementById("settings-title").innerText = title;
    }
}

function setContent(newContent,newHighlight,url) {
    const f = document.getElementById('settings-custom-iframe');
    f.src = "";
    if(url) {
        f.src = url;
    }
    if(newHighlight) {
        newHighlight = document.getElementById(newHighlight);
    } else {
        newHighlight = document.getElementById(newContent+"-button")
    }
    newContent = document.getElementById(newContent);
    if(newContent) {
        if(!newContent.classList.contains("active")) {
            newContent.classList.add("active");
            if(content) {
                if(content.classList.contains("active")) {
                    content.classList.remove("active");
                }
            }
            content = newContent;
            highlight(newHighlight);
        }
    }
}

function initSettings() {
    const urlParams = new URLSearchParams(window.location.search);
    let id = "settings-general";
    if(urlParams.get('t')) {
        id = urlParams.get("t");
    }

    let hid = id+"-button";
    if(urlParams.get('h')) {
        hid = urlParams.get("h");
    }

    if(urlParams.get("u")) {
        setContent(id,hid,urlParams.get("u"));
    } else {
        setContent(id,hid);
    }
}