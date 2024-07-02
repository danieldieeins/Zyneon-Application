let highlightedTheme = "";
let highlightedFrame = "";

function syncLang(language) {
    localStorage.setItem('settings.language', language);
    connector("sync.language."+language);
    connector("sync.firstrun.theme");
}

function initThemeWizard() {
    highlightTheme(colors);
}

function initLinuxWizard(customFrame) {
    const button = document.getElementById("firstrun-theme-finish-button");
    const button_ = document.getElementById("firstrun-continue-button");
    button.innerText = button_.innerText;
    button.onclick = function () {}
    button.href = "#linux";
    if(customFrame===true||customFrame==="true") {
        highlightFrame("linux-frame");
    } else {
        highlightFrame("default-frame");
    }
}

function highlightTheme(newTheme) {
    if(newTheme) {
        const button = document.getElementById(newTheme);
        if(button) {
            if(!button.classList.contains("highlighted")) {
                button.classList.add("highlighted");
                if(highlightedTheme) {
                    const oldTheme = document.getElementById(highlightedTheme);
                    if(oldTheme) {
                        if(oldTheme.classList.contains("highlighted")) {
                            oldTheme.classList.remove("highlighted");
                        }
                    }
                }
                highlightedTheme = newTheme;
            }
        }
    }
}

function highlightFrame(newFrame) {
    if(newFrame) {
        const button = document.getElementById(newFrame);
        if(button) {
            if(!button.classList.contains("highlighted")) {
                button.classList.add("highlighted");
                if(highlightedFrame) {
                    const oldFrame = document.getElementById(highlightedFrame);
                    if(oldFrame) {
                        if(oldFrame.classList.contains("highlighted")) {
                            oldFrame.classList.remove("highlighted");
                        }
                    }
                }
                highlightedFrame = newFrame;
            }
        }
    }
}

function setTheme(id) {
    setColors(id);
    highlightTheme(id);
}

addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get("theme.colors")) {
        highlightTheme(urlParams.get("theme.colors"));
        connector("sync.firstrun.theme");
    }
    if(urlParams.get("back")) {
        if(urlParams.get("back")===true||urlParams.get("back")==="true") {
            const button = document.getElementById("fs-back");
            button.classList.remove("disabled");
            button.innerText = document.getElementById("language-back").innerText;
            button.onclick = function () { location.href = 'settings.html' }
        }
    }
});