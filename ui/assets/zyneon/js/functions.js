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

let theme = "dark";
function toggleTheme() {
    if(theme==="dark") {
        setTheme("zyneon");
    } else if(theme==="zyneon") {
        setTheme("light");
    } else {
        setTheme("dark");
    }
}

function setTheme(newTheme) {
    let root = document.documentElement;
    if(newTheme==="light") {
        theme = "light";
        root.style.setProperty("--background", "#8f8f8f");
        root.style.setProperty('--background2', '#c7c7c7');
        root.style.setProperty("--background-accent", "#fff");
        root.style.setProperty("--highlight", "#000");
        root.style.setProperty("--color", "#000");
        root.style.setProperty("--color-dim", "#111");
        root.style.setProperty("--inverted", "#fff");
    } else if(newTheme==="zyneon") {
        setTheme("dark");
        root.style.setProperty('--background', '#140c28');
        root.style.setProperty('--background2', '#0d061c');
        root.style.setProperty('--background-accent', '#050113');
    } else {
        theme = "dark";
        root.style.setProperty('--background', '#181818');
        root.style.setProperty('--background2', '#101010');
        root.style.setProperty('--background-accent', '#000');
        root.style.setProperty("--highlight", "#fff");
        root.style.setProperty("--color", "#fff");
        root.style.setProperty("--color-dim", "#777");
        root.style.setProperty("--inverted", "#000");
    }
}