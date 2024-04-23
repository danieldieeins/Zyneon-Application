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