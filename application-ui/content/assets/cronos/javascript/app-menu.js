function toggleMenu(menuId,save) {
    const menu = document.getElementById(menuId);
    if(menu.classList.contains('active')) {
        deactivateMenu(menuId,save);
    } else {
        activateMenu(menuId,save);
    }
}

function setMenu(menuId,active,save) {
    if(active) {
        deactivateMenu(menuId,save);
    } else {
        activateMenu(menuId,save);
    }
}

function activateMenu(menuId,save) {
    const menu = document.getElementById(menuId);
    if(!menu.classList.contains('active')) {
        if (save) {
            localStorage.setItem('menu.' + menuId, "active");
        }
        menu.classList.add('active');
        menu.classList.remove('inactive');
    }
}

function deactivateMenu(menuId,save) {
    const menu = document.getElementById(menuId);
    if(!menu.classList.contains('inactive')) {
        if (save) {
            localStorage.setItem('menu.' + menuId, "inactive");
        }
        menu.classList.add('inactive');
        menu.classList.remove('active');
    }
}

function initMenu(menuId) {
    const urlParams = new URLSearchParams(window.location.search);
    let active = true;
    if(urlParams.get("menu."+menuId)) {
        const menuMode = urlParams.get("menu."+menuId);
        if(menuMode!=="active") {
            active = false;
        }
    } else if(localStorage.getItem('menu.'+menuId)) {
        const menuMode = localStorage.getItem('menu.'+menuId);
        if(menuMode!=="active") {
            active = false;
        }
    }
    if(active) {
        activateMenu(menuId,false);
        return true;
    } else {
        deactivateMenu(menuId,false);
    }
    return false;
}

function addMenuEntry(entryId,icon,text,clickRequest) {
    if(entryId) {
        if (text) {
            const template = document.getElementById('side-menu-item');
            const entry = template.cloneNode(true);
            entry.id = entryId;

            const p = entry.querySelector("p");
            if(p) {
                p.innerText = text.toString();
            }

            if (clickRequest) {
                entry.onclick = function () {
                    connector(clickRequest.toString());
                }
            }

            template.parentNode.insertBefore(entry,template);

            if (icon) {
                const i = icon.toString().toLowerCase();
                if (i.endsWith(".png") || i.endsWith(".svg") || i.endsWith(".jpg") || i.endsWith(".gif") || i.endsWith(".jpeg")) {
                    const ie = entry.querySelector("i");
                    const img = entry.querySelector("img");
                    if (ie) {
                        ie.style.display = "none";
                    }
                    if (img) {
                        img.src = icon.toString();
                        img.classList.remove("invisible");
                    }
                } else {
                    const ie = entry.querySelector("i");
                    if (ie) {
                        ie.classList.remove("bx");
                        ie.classList.remove("bx-loader-circle");
                        ie.classList.remove("bx-spin");
                        const classList = i.toString().split(/\s+/);
                        classList.forEach(className => {
                            if (className.trim() !== "") { // Leere Klassen ignorieren
                                ie.classList.add(className);
                            }
                        });
                    }
                }
            }
        }
    }
}