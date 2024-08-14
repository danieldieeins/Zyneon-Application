function initModulePage() {
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.get("title")) {
        document.title = urlParams.get("title");
    }

    if(urlParams.get("id")) {
        const id = urlParams.get("id");
        connector("init."+id);
    } else {
        connector("init.module-page");
    }

    if(urlParams.get("url")) {
        document.getElementById("iframe").src = urlParams.get("url");
    }
}

function executeCustomScript(script) {
    document.getElementById("iframe").contentWindow.eval(script);
}