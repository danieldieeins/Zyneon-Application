const news = document.getElementById("news-content");
const newsbutton = document.getElementById("news");
const updates = document.getElementById("changelog-content");
const updatesbutton = document.getElementById("changelogs");

function syncNews() {
    updates.style.display = "none";
    updatesbutton.classList.remove("active");
    news.style.display = "inherit";
    newsbutton.classList.add("active");
}

function syncUpdates() {
    news.style.display = "none";
    newsbutton.classList.remove("active");
    updates.style.display = "inherit";
    updatesbutton.classList.add("active");
}

function syncStart(response) {
    if(response!==undefined) {
        if(response==="app") {
            if(document.getElementById("app-button")!==null) {
                const button = document.getElementById("app-button");
                button.innerHTML = "<i class='bx bx-laptop ' ></i> Web version";
                button.onclick = function () {
                    openInBrowser("https://www.zyneonstudios.com/nexus/app");
                }
            }
        }
    } else {
        loadNews();
        callJavaMethod("sync.start");
    }
}

function loadNews() {
    document.getElementById("iframe").src = document.getElementById("iframe").src+"?theme="+theme;
}