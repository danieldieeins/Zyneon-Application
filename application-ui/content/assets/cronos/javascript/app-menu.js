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
    let active = false;
    if(urlParams.get("menu."+menuId)) {
        const menuMode = urlParams.get("menu."+menuId);
        if(menuMode==="active") {
            active = true;
        }
    } else if(localStorage.getItem('menu.'+menuId)) {
        const menuMode = localStorage.getItem('menu.'+menuId);
        if(menuMode==="active") {
            active = true;
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