function toggleMainMenu() {
    const menu = document.getElementById('menu');
    const submenu = document.getElementById('submenu');
    if(menu) {
        if(menu.classList.contains('active')) {
            if(!menu.classList.contains('inactive')) {
                menu.classList.add('inactive');
            }
            localStorage.setItem("menu.menu",'inactive');
            menu.classList.remove('active');
            if(submenu) {
                submenu.classList.add('active');
                localStorage.setItem("menu.submenu",'active');
                submenu.classList.remove('inactive');
            }
        } else {
            menu.classList.add('active');
            localStorage.setItem("menu.menu",'active');
            if(menu.classList.contains('inactive')) {
                menu.classList.remove('inactive');
            }
            if(submenu) {
                submenu.classList.add('inactive');
                localStorage.setItem("menu.submenu",'inactive');
                submenu.classList.remove('active');
            }
        }
    }
}

function initMainMenu(active) {
    if(active==null) {
        if(initMenu('menu')) {
            deactivateMenu('submenu',true);
        } else {
            activateMenu('submenu',true);
        }
    } else {
        if(initMenu('menu')) {
            if(!active) {
                deactivateMenu('menu');
                activateMenu('submenu');
            } else {
                activateMenu('submenu');
            }
        } else {
            if(active) {
                activateMenu('menu');
                deactivateMenu('submenu');
            } else {
                activateMenu('submenu');
            }
        }
    }
    if(document.getElementById('menu').classList.contains('active')) {
        localStorage.setItem("menu.menu","active");
        localStorage.setItem("menu.submenu","inactive");
    } else {
        localStorage.setItem("menu.menu","inactive");
        localStorage.setItem("menu.submenu","active");
    }
}

function changeThemeColors() {
    if(colors==="automatic") {
        setColors("automatic");
    } else if(colors==="../assets/cronos/css/app-colors-dark.css") {
        setColors("../assets/cronos/css/app-colors-light.css");
    } else if(colors==="../assets/cronos/css/app-colors-light.css") {
        setColors("../assets/application/css/app-colors-zyneon.css");
    } else {
        setColors("../assets/cronos/css/app-colors-dark.css");
    }
}

function preventClick(id) {
    document.getElementById(id).addEventListener('click', function(event) {
        event.preventDefault();
    });
}

function log(message) {
    console.log("[LOG] [UI] "+message)
}

function err(message) {
    console.log("[ERR] [UI] "+message)
}

function deb(message) {
    console.log("[DEB] [UI] "+message)
}

function error(message) {
    err(message);
}

function debug(message) {
    deb(message);
}