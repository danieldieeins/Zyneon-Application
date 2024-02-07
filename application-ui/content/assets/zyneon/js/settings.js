const general = document.getElementById("general");
const global = document.getElementById("global");
const profile = document.getElementById("profile");
const fallback = document.getElementById("fallback");
const instance = document.getElementById("path-select");
const about = document.getElementById("about-app");
const generalbutton = document.getElementById("general-settings");
const globalbutton = document.getElementById("global-settings");
const profilebutton = document.getElementById("profile-settings");
const aboutbutton = document.getElementById("about");

function syncSettings() {
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.get("tab")!=null) {
        const tab = urlParams.get("tab");
        if(tab==="global") {
            syncGlobalSettings();
        } else if(tab==="select") {
            openGlobalSettings();
        } else if(tab==="profile") {
            syncProfileSettings();
        } else {
            syncGeneralSettings();
        }
    } else {
        syncGeneralSettings();
    }
}

function syncGeneralSettings() {
    instance.style.display = "none";
    fallback.style.display = "none";
    about.style.display = "none";
    aboutbutton.classList.remove("active");
    global.style.display = "none";
    globalbutton.classList.remove("active");
    profile.style.display = "none";
    profilebutton.classList.remove("active");

    const dark = document.getElementById("appearance-theme-dark");
    const light = document.getElementById("appearance-theme-light");
    const zyneon = document.getElementById("appearance-theme-zyneon");
    if(theme==="zyneon") {
        dark.classList.remove("active");
        light.classList.remove("active");
        zyneon.classList.add("active");
    } else if(theme==="light") {
        dark.classList.remove("active");
        zyneon.classList.remove("active");
        light.classList.add("active");
    } else {
        light.classList.remove("active");
        zyneon.classList.remove("active");
        dark.classList.add("active");
    }

    general.style.display = "inherit";
    generalbutton.classList.add("active");
    callJavaMethod("sync.settings.general");
}

function openGlobalSettings() {
    fallback.style.display = "none";
    about.style.display = "none";
    aboutbutton.classList.remove("active");
    general.style.display = "none";
    generalbutton.classList.remove("active");
    profile.style.display = "none";
    profilebutton.classList.remove("active");

    global.style.display = "none";
    instance.style.display = "inherit";
    globalbutton.classList.add("active");
}

function syncGlobalSettings() {
    openGlobalSettings();
    callJavaMethod("sync.settings.global");
}

function syncProfileSettings() {
    instance.style.display = "none";
    general.style.display = "none";
    about.style.display = "none";
    aboutbutton.classList.remove("active");
    generalbutton.classList.remove("active");
    global.style.display = "none";
    globalbutton.classList.remove("active");

    fallback.style.display = "inherit";
    profile.style.display = "none";
    profilebutton.classList.add("active");
    callJavaMethod("sync.settings.profile");
}

function syncVersion() {
    callJavaMethod("sync.settings.version")
}

function syncApp(ver) {
    document.getElementById("app-version").innerText = ver;
    document.getElementById("global-button").style.display = "inherit";
    document.getElementById("profile-button").style.display = "inherit";
    document.getElementById("refresh-button").style.display = "inherit";
    document.getElementById("exit-button").style.display = "inherit";
    document.getElementById("startTitle").style.display = "inherit";
    document.getElementById("startTab").style.display = "inherit";
}

function syncGeneral(start) {
    const news = document.getElementById("general-tab-start");
    const instances = document.getElementById("general-tab-instances");
    if(start==="start") {
        instances.classList.remove("active");
        news.classList.add("active");
    } else {
        news.classList.remove("active");
        instances.classList.add("active");
    }
}

function syncGlobal(memory,instance_path) {
    instance.style.display = "none";
    global.style.display = "inherit";
    const memory_int = document.getElementById("memory-int");
    const instance_path_string = document.getElementById("instance-path-string");
    memory_int.innerText = memory;
    instance_path_string.innerText = instance_path;
}

function syncProfile(name,uuid) {
    document.getElementById("user-name").innerText = name;
    document.getElementById("uuid").innerText = uuid;
    document.getElementById("user-image").src = "https://cravatar.eu/helmhead/"+name+"/512";
    fallback.style.display = "none";
    profile.style.display = "inherit";
}

function syncAbout() {
    instance.style.display = "none";
    general.style.display = "none";
    generalbutton.classList.remove("active");
    fallback.style.display = "none";
    profile.style.display = "none";
    profilebutton.classList.remove("active");
    global.style.display = "none";
    globalbutton.classList.remove("active");

    about.style.display = "inherit";
    aboutbutton.classList.add("active");
}