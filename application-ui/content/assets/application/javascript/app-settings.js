let updates = false;
let linuxFrame = false;
let highlighted = undefined;
let content = undefined;

function toggleUpdates() {
    const updateSlider = document.getElementById("updater-settings-enable-updates");
    if(updateSlider.classList.contains("active")) {
        updates = false;
        connector('sync.autoUpdates.off');
        updateSlider.classList.remove("active");
    } else {
        updates = true;
        connector('sync.autoUpdates.on');
        updateSlider.classList.add("active");
    }
}

function toggleLinuxFrame() {
    if(linuxFrame) {
        linuxFrame = false;
        connector('sync.linuxFrame.off');
    } else {
        linuxFrame = true;
        connector('sync.linuxFrame.on');
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

function addGroup(title,id) {
    if(id) {
        if(title) {
            if (document.getElementById(id) == null) {
                const template = document.getElementById("settings-group-template");
                const group = template.cloneNode(true);
                group.id = id;
                group.querySelector("h3").innerText = title;
                template.parentNode.insertBefore(group,template);
            }
        }
    }
}

function addModuleSetting(icon,text,onclick,id,image,group) {
    let template;

    if(group) {
        template = document.getElementById(group).querySelector("#settings-module-template");
    } else {
        template = document.getElementById("settings-module-template");
    }

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

    const p = entry.querySelector("p");
    if(p) {
        if(text) {
            p.innerText = text;
        }
    }

    if(onclick) {
        entry.onclick = function() {
            connector(onclick);
            log(entry.id);
        };
    }

    template.parentNode.insertBefore(entry,template);
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
        if(id==="modules") {
            setContent('settings-modules'); connector('sync.settings.modules');
        }
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

function addSettingsGroup(title,page,id) {
    if(title&&page) {
        const template = document.getElementById(page+"-group-template");
        const group = template.cloneNode(true);
        if(id) {
            group.id = id;
        } else {
            group.id = (page+"-"+title).replaceAll(" ", "-").replace(/[^a-z0-9-_]/gi, '').toLowerCase();
        }
        group.querySelector("h4").innerText = title;
        group.classList.remove("template");
        template.parentNode.insertBefore(group,template);
    }
}

function addSelectToGroup(title,group,id,options,onchangeRequest) {
    if(title&&group) {
        const g = document.getElementById(group);
        let i = (group+"-"+title).replaceAll(" ", "-").replace(/[^a-z0-9-_]/gi, '').toLowerCase();
        if(id) {
            i = id;
        }
        g.innerHTML += "<h3>"+title+" <span class='select'><label><select id='"+i+"'></select></label></span></h3>";
        if(options) {
            document.getElementById(i).innerHTML = options;
        }
        if(onchangeRequest) {
            document.getElementById(i).onchange = function () {
                connector(onchangeRequest+"."+document.getElementById(i).value);
            }
        } else {
            document.getElementById(i).onchange = function () {
                connector(i+"."+document.getElementById(i).value);
            }
        }
    }
}

function addToggleToGroup(title,group,id,onchangeRequest,defaultState) {
    if(title&&group) {
        const g = document.getElementById(group);
        let i = (group+"-"+title).replaceAll(" ", "-").replace(/[^a-z0-9-_]/gi, '').toLowerCase();
        if(id) {
            i = id;
        }
        g.innerHTML += "<h3>"+title+" <div id='"+i+"' class='toggle'><div class='toggle-slider'></div></div></h3>";
        const toggle = document.getElementById(i);
        if(defaultState) {
            if(defaultState===true) {
                toggle.classList.add("active");
            }
        }
        if(onchangeRequest) {
            toggle.onclick = function () {
                toggle.classList.toggle("active");
                connector(onchangeRequest+"."+toggle.classList.contains("active"));
            }
        } else {
            toggle.onclick = function () {
                toggle.classList.toggle("active");
                connector(i+"."+toggle.classList.contains("active"));
            }
        }
    }
}

function addValueToGroup(title,group,id,onclickRequest,defaultValue) {
    if(title&&group) {
        const g = document.getElementById(group);
        let i = (group+"-"+title).replaceAll(" ", "-").replace(/[^a-z0-9-_]/gi, '').toLowerCase();
        if(id) {
            i = id;
        }
        if(onclickRequest) {
            g.innerHTML += "<h3>" + title + " <span id='" + i + "' class='value flex'><div class='setting-value'></div><a class='setting-button'><i class='bx bxs-edit' onclick=\"connector('" + onclickRequest + "');\"></i></a></span></h3>";
        } else {
            g.innerHTML += "<h3>" + title + " <span id='" + i + "' class='value flex'><div class='setting-value'></div><a class='setting-button'><i class='bx bxs-edit' onclick=\"connector('" + i + "');\"></i></a></span></h3>";
        }
        if(defaultValue) {
            document.getElementById(i).querySelector(".setting-value").innerText = defaultValue;
        }
    }
}