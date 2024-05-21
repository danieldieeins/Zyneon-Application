let updates = false;

function toggleUpdates() {
    if(updates) {
        updates = false;
        connector('sync.autoUpdates.off');
    } else {
        updates = true;
        connector('sync.autoUpdates.on');
    }
}

function syncUpdates() {
    connector('sync.updateChannel.'+document.getElementById('updater-settings-update-channel').value);
}

function syncTheme() {
    setColors(document.getElementById("appearance-settings-theme").value);
}

function syncStartPage() {
    const startPage = document.getElementById("general-settings-start-page").value;
    connector("sync.startPage."+startPage);
    localStorage.setItem('settings.startPage', startPage);
}

function syncLanguage() {
    const language = document.getElementById("appearance-settings-language").value;
    connector("sync.language."+language);
    localStorage.setItem('settings.language', language);
    location.href = "../"+language+"/settings.html";
}