let appType = "web";

addEventListener("DOMContentLoaded", () => {
    const oldContent = document.getElementById("old-changelog");
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.get("type")!=null) {
        if(urlParams.get("type")==="new") {
            const newContent = document.getElementById("new-changelog");
            newContent.classList.add("active");
            document.getElementById("neco").classList.add("active");
            document.getElementById("neme").classList.add("active");
            appType = "new";
            return;
        }
    }
    console.log("hi");
    oldContent.classList.add("active");
});

function openUrlInBrowser(url) {
    if(appType==="new") {
        console.log("[CONNECTOR] open.url."+url)
    } else {
        openInBrowser(url);
    }
}