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

function setColors(newColors) {
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