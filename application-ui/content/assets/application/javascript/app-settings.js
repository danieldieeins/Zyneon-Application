let updates = false;
let highlighted = undefined;

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