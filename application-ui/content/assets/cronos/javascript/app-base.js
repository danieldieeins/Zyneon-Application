let desktop = false;
let colors = "automatic";

document.addEventListener('contextmenu',function(e){
    e.preventDefault();
});

document.addEventListener('dragstart', function(e){
    e.preventDefault();
});

function init() {
    const urlParams = new URLSearchParams(window.location.search);
    if(location.href.includes("http://localhost:63342/Zyneon-Application/")) {
        if (!urlParams.get("_ij_reload")) {
            if(location.href.includes("?")) {
                location.href = location.href + "&_ij_reload=RELOAD_ON_SAVE";
            } else {
                location.href = location.href + "?_ij_reload=RELOAD_ON_SAVE";
            }
        }
    }
    let theme = colors;
    if(localStorage.getItem("theme.colors")) {
        theme = localStorage.getItem("theme.colors");
    }
    if(urlParams.get("theme.colors")) {
        theme = urlParams.get("theme.colors");
    }
    setColors(theme);
}

function connector(request) {
    console.log("[CONNECTOR] " + request);
}

function setColors(newColors,fromApp) {
    colors=newColors;
    localStorage.setItem('theme.colors', newColors);
    if(newColors==="automatic") {
        if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
            document.getElementById("css-colors").href = "../assets/cronos/css/app-colors-dark.css";
            connector("sync.title.automatic-dark-.-" + document.title);
            return;
        }
        document.getElementById("css-colors").href = "../assets/cronos/css/app-colors-light.css";
        connector("sync.title.automatic-light-.-" + document.title);
    } else {
        document.getElementById("css-colors").href = newColors;
        connector("sync.title." + colors + "-.-" + document.title);
    }
}

function setColors_(newColors,fromApp) {
    colors=newColors;
    localStorage.setItem('theme.colors', newColors);
    if(newColors==="automatic") {
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            document.getElementById("css-colors").href = "../assets/cronos/css/app-colors-dark.css";
            connector("sync.title.automatic-dark-.-" + document.title);
        } else {
            document.getElementById("css-colors").href = "../assets/cronos/css/app-colors-light.css";
            connector("sync.title.automatic-light-.-" + document.title);
        }
    } else {
        document.getElementById("css-colors").href = newColors;
        connector("sync.title." + colors + "-.-" + document.title);
    }
}

function changeTheme() {
    if(colors==="../assets/cronos/css/app-colors-dark.css") {
        setColors("../assets/cronos/css/app-colors-light.css");
    } else {
        setColors("../assets/cronos/css/app-colors-dark.css");
    }
}

function syncDesktop() {
    desktop = true;
}

function enableOverlay(url) {
    if(url) {
        if(typeof url === "string") {
            const overlay = document.getElementById("overlay");
            const frame = document.getElementById("overlay-frame");
            frame.src = url;
            if (!overlay.classList.contains("active")) {
                overlay.classList.add("active");
            }
        }
    }
}

function disableOverlay() {
    const overlay = document.getElementById("overlay");
    if(overlay.classList.contains("active")) {
        overlay.classList.remove("active");
    }
    const frame = document.getElementById("overlay-frame");
    frame.src = "";
}