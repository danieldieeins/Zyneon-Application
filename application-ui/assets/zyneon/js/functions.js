let theme = "dark";

document.addEventListener('contextmenu',function(e){
    e.preventDefault();
});

document.addEventListener('dragstart', function(e){
    e.preventDefault();
});

function openInBrowser(url) {
    callJavaMethod("browser."+url);
}

function toggleMenu() {
    let menu = document.querySelector(".menu");
    menu.classList.toggle('active');
    let submenu = document.querySelector("#submenu");
    submenu.classList.toggle('active');
}

function activateMenu() {
    let menu = document.querySelector(".menu");
    menu.classList.add('active');
    let submenu = document.querySelector("#submenu");
    submenu.classList.remove('active');
}

function deactivateMenu() {
    let menu = document.querySelector(".menu");
    menu.classList.remove('active');
    let submenu = document.querySelector("#submenu");
    submenu.classList.add('active');
}

function syncApplication() {
    deactivateMenu();
    callJavaMethod("sync.login");
}

function login(name) {
    checkForWeb();
    document.getElementById("username").innerText = name;
    document.getElementById("profile-picture").src = "https://cravatar.eu/helmhead/"+name+"/64.png";
    document.getElementById("profile-picture").style.display = "inherit";
    document.getElementById("loginlogout").innerText = "Logout";
    document.getElementById("loading").style.display = "none";
}

function logout() {
    checkForWeb();
    document.getElementById("username").innerText = "Not logged in";
    document.getElementById("profile-picture").src = "assets/zyneon/images/steve.png";
    document.getElementById("profile-picture").style.display = "inherit";
    document.getElementById("loginlogout").innerText = "Login";
    document.getElementById("loading").style.display = "none";
}

function checkForWeb() {
    document.getElementById("profile_").style.display = "inherit";
    document.getElementById("instance-button").style.display = "inherit";
    document.getElementById("download-button").style.display = "none";
}

function syncTheme() {
    if(localStorage.getItem("theme")!==null) {
        theme = localStorage.getItem("theme");
    }
    setTheme(theme);
}

function syncTheme_old() {
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.get("theme")!=null) {
        theme = urlParams.get('theme');
    }
    setTheme(theme);
}

function setTheme(newTheme) {
    let root = document.documentElement;
    if (newTheme === "light") {
        theme = "light";
        root.style.setProperty("--background", '#c7c7c7');
        root.style.setProperty('--background2', '#e0e0e0');
        root.style.setProperty('--background3', '#d3d3d3');
        root.style.setProperty("--background4", "#f6f6f6");
        root.style.setProperty("--background-accent", "#fff");
        root.style.setProperty("--highlight", "#000");
        root.style.setProperty("--color", "#000");
        root.style.setProperty("--color-dim", "#00000085");
        root.style.setProperty("--color-dim-less", "#00000099");
        root.style.setProperty("--inverted", "#fff");
    } else if (newTheme === "zyneon") {
        setTheme("dark");
        theme = "zyneon";
        root.style.setProperty('--background', '#140c28');
        root.style.setProperty('--background2', '#0d061c');
        root.style.setProperty('--background3', '#120925');
        root.style.setProperty('--background4', '#060112');
        root.style.setProperty('--background-accent', '#050113');
    } else {
        theme = "dark";
        root.style.setProperty('--background', '#181818');
        root.style.setProperty('--background2', '#101010');
        root.style.setProperty('--background3', '#1a1a1a');
        root.style.setProperty('--background4', '#080808');
        root.style.setProperty('--background-accent', '#000');
        root.style.setProperty("--highlight", "#fff");
        root.style.setProperty("--color", "#fff");
        root.style.setProperty("--color-dim", "#ffffff60");
        root.style.setProperty("--color-dim-less", "#ffffff90");
        root.style.setProperty("--inverted", "#000");
    }
    localStorage.setItem('theme', theme);
    callJavaMethod('button.theme.' + theme);
}

function callJavaMethod(message) {
    console.log("[Launcher-Bridge] "+message);
}

function link(url) {
    window.location.href = url;
}

function addVersionsToMinecraftSelect(id) {
    callJavaMethod("sync.select.minecraft."+id);
}

function addToSelect(selectID,value,name) {
    const select = document.getElementById(selectID);
    const option = document.createElement("option");
    option.text = name;
    option.value = value;
    select.add(option);
}