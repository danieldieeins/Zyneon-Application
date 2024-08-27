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

const colorSchemeQuery = window.matchMedia("(prefers-color-scheme: dark)");

function handleColorSchemeChange(e) {
    if(colors==="automatic") {
        setColors(colors);
    }
}

colorSchemeQuery.addListener(handleColorSchemeChange);

function setMenuPanel(img,title,description,show) {
    hideMenuPanel();
    const pTitle = document.getElementById("panel-title");
    pTitle.innerHTML = "";
    if(title) {
        pTitle.innerHTML = title;
    }

    const pDescription = document.getElementById("panel-description");
    pDescription.innerHTML = "";
    if(description) {
        pDescription.innerHTML = description;
    }

    const image = document.getElementById("panel-image");
    image.src = "";
    if(img) {
        image.src = img;
        document.getElementById("menu-panel").querySelector("p").classList.remove("active");
    } else {
        image.style.display = "none";
        document.getElementById("menu-panel").querySelector("p").classList.add("active");
    }

    if(show) {
        if(show===true) {
            showMenuPanel();
        }
    }
}

function showMenuPanel() {
    document.getElementById("menu-panel").style.display = "inherit";

}

function hideMenuPanel() {
    document.getElementById("menu-panel").style.display = "none";
}

function decodeURL(string) {
    string = decodeURIComponent(decodeURI(string));
    string = string.replaceAll("%plus%","+");
    return string;
}

function openUrl(url) {
    if(desktop) {
        connector("open.url."+url);
    } else {
        window.open(url, '_blank');
    }
}

const listInputs = new Map();

function initializeListInput(id) {
    const input = document.getElementById(id);
    if(input) {
        if (listInputs.has(id)) {
            listInputs.delete(id);
        }

        const list = [];
        listInputs.set(id, list);

        input.addEventListener('keydown', (event) => {
            if (event.keyCode === 13) {
                event.preventDefault();
                if(!list.includes(input.querySelector( "input").value)&&input.querySelector( "input").value !== "") {
                    listInputs.get(id).push(input.querySelector("input").value);
                    connector(id+".add."+input.querySelector("input").value);
                }
                input.querySelector("input").value = "";
                syncListInput(id);
            }
        });
    }
}

function syncListInput(id) {
    const input = document.getElementById(id);
    if(input) {
        if(listInputs.has(id)) {
            const list = listInputs.get(id);
            input.querySelector(".list-input-content").innerHTML = "";
            for (let i = 0; i < list.length; i++) {
                input.querySelector(".list-input-content").innerHTML += "<span class='list-input-item'>" + list[i] + " <i onclick=\"removeStringFromListInput('"+id+"','"+list[i]+"');\" class='bx bx-x'></i></span>";
            }
        }
    }
}

function removeStringFromListInput(id,string) {
    if(listInputs.has(id)) {
        for (let i = 0; i < listInputs.get(id).length; i++) {
            if(listInputs.get(id)[i] === string) {
                listInputs.get(id).splice(i, 1);
                syncListInput(id);
                connector(id+".remove."+string);
                break;
            }
        }
    }
}