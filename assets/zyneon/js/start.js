const news = document.getElementById("news");
const newsbutton = document.getElementById("news-button");
const updates = document.getElementById("updates");
const updatesbutton = document.getElementById("updates-button");

function syncNews() {
    updates.style.display = "none";
    updatesbutton.classList.remove("active");
    news.style.display = "inherit";
    newsbutton.classList.add("active");
    callJavaMethod("sync.start.news");
}

function syncUpdates() {
    news.style.display = "none";
    newsbutton.classList.remove("active");
    updates.style.display = "inherit";
    updatesbutton.classList.add("active");
    callJavaMethod("sync.start.updates");
}