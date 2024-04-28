let desktop = false;
let colors = "assets/cronos/css/app-colors-dark.css";

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
    document.getElementById("css-colors").href = newColors;
    localStorage.setItem('theme.colors', newColors);
    connector("sync.title."+colors+"-.-"+document.title);
}

function changeTheme() {
    if(colors==="assets/cronos/css/app-colors-dark.css") {
        setColors("assets/cronos/css/app-colors-light.css");
    } else {
        setColors("assets/cronos/css/app-colors-dark.css");
    }
}

function syncDesktop() {
    desktop = true;
}