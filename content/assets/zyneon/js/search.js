let search_source = "modrinth";
let search_type = "fabric"
let search_version = "1.20.4";
let search_query = "";
let search_instance = "";
let search_disable = "";

document.getElementById("search-version").onchange = versionChange;

function syncSearch() {
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.get("s")!=null) {
        search_source = urlParams.get("s");
    }
    if(urlParams.get("t")!=null) {
        search_type = urlParams.get("t");
    }
    if(urlParams.get("v")!=null) {
        if(urlParams.get("v")!=="") {
            search_version = urlParams.get("v");
            document.getElementById("search-version").value = search_version;
        } else {
            search_version = document.getElementById("search-version").value;
        }
    } else {
        search_version = document.getElementById("search-version").value;
    }
    if(urlParams.get("q")!=null) {
        search_query = urlParams.get("q");
        document.getElementById("search-query").value = search_query;
    }
    if(urlParams.get("i")!=null) {
        search_instance = urlParams.get("i");
    }
    if(urlParams.get("d")!=null) {
        search_disable = urlParams.get("d");
        if(document.getElementById(search_disable)) {
            document.getElementById(search_disable + "-button").style.display = "none";
        }
    }
    document.getElementById(search_source).classList.add("active");
    document.getElementById(search_type).classList.add("active");
    callJavaMethod("sync.search."+search_source+"."+search_type+"."+search_version.replaceAll(".","%")+"."+search_query);
}

function update(query) {
    if(query !== undefined) {
        search_query = query;
    } else {
        search_query = document.getElementById("search-query").value;
    }
    link("resources.html?s="+search_source+"&t="+search_type+"&v="+search_version+"&q="+search_query+"&i="+search_instance+"&d="+search_disable);
}

function versionChange() {
    search_version = document.getElementById("search-version").value;
    update();
}

function addItem(png,name,author,description,id,slug) {
    const base = document.getElementById("template");
    const item = base.cloneNode(true);
    item.id = slug;
    base.parentNode.insertBefore(item, base);
    if (png !== undefined) {
        if (png !== "") {
            item.querySelector("#png").src = png;
        }
    }
    item.querySelector("#name").innerText = name;
    item.querySelector("#author").innerText = "by "+author;
    item.querySelector("#description").innerText = description;
    item.querySelector("#install").onclick = function () {
        if(search_type!=="modpacks") {
            if (search_instance !== null) {
                if (search_instance !== undefined) {
                    if (search_instance !== "") {
                        callJavaMethod("button.install." + search_instance + "." + search_source + "." + search_type + "." + id);
                    }
                }
            }
        } else {
            if(search_source==="zyneon") {
                callJavaMethod("button.install."+id);
            }
        }
    };
    if(search_source==="zyneon") {
        item.querySelector("#show").style.display = "none";
    } else {
        item.querySelector("#show").onclick = function () {
            openInBrowser("https://modrinth.com/mod/"+slug);
        };
    }
}