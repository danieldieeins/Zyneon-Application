let theme = "zyneon";

document.addEventListener('contextmenu',function(e){
    e.preventDefault();
});

document.addEventListener('dragstart', function(e){
    e.preventDefault();
});

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
    document.getElementById("username").innerText = name;
    document.getElementById("profile-picture").src = "https://cravatar.eu/helmhead/"+name+"/64.png";
    document.getElementById("profile-picture").style.display = "inherit";
    document.getElementById("loginlogout").innerText = "Logout";
    document.getElementById("loading").style.display = "none";
}

function logout() {
    document.getElementById("username").innerText = "Not logged in";
    document.getElementById("profile-picture").src = "assets/zyneon/images/steve.png";
    document.getElementById("profile-picture").style.display = "inherit";
    document.getElementById("loginlogout").innerText = "Login";
    document.getElementById("loading").style.display = "none";
}

function syncTheme() {
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
    } else if (newTheme === "dark") {
        theme = "dark";
        root.style.setProperty('--background', '#181818');
        root.style.setProperty('--background2', '#101010');
        root.style.setProperty('--background3', '#1a1a1a');
        root.style.setProperty('--background4', '#0c0c0c');
        root.style.setProperty('--background-accent', '#000');
        root.style.setProperty("--highlight", "#fff");
        root.style.setProperty("--color", "#fff");
        root.style.setProperty("--color-dim", "#ffffff60");
        root.style.setProperty("--color-dim-less", "#ffffff90");
        root.style.setProperty("--inverted", "#000");
    } else {
        setTheme("dark");
        theme = "zyneon";
        root.style.setProperty('--background', '#140c28');
        root.style.setProperty('--background2', '#0d061c');
        root.style.setProperty('--background3', '#120925');
        root.style.setProperty('--background4', '#060112');
        root.style.setProperty('--background-accent', '#050113');
    }
    callJavaMethod('button.theme.' + theme);
}

function callJavaMethod(message) {
    console.log("[Launcher-Bridge] "+message);
}

function link(url) {
    if(url.includes("?")) {
        url=url+"&theme="+theme;
    } else {
        url=url+"?theme="+theme;
    }
    window.location.href = url;
}