let search_source = "zyneon";
let search_type = "modpacks"
let search_version = "";
let search_query = "";
let search_instance = "";
let search_disable = "";
let i = 0;

document.getElementById("search-version").onchange = versionChange;

function backToInstance() {
    link("instances.html?tab="+search_instance)
}

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
        if(urlParams.get("i")!=="") {
            search_instance = urlParams.get("i");
            document.getElementById("back-to-instance").style.display = "inherit";
            document.getElementById("fabric-button").style.display = "inherit";
            document.getElementById("modpack-button").style.display = "none";
            document.getElementById("zyneon-button").style.display = "none";
            document.getElementById("version-title").style.display = "none";
            document.getElementById("forge-button").style.display = "inherit";
            document.getElementById("shader-button").style.display = "inherit";
            document.getElementById("resourcepack-button").style.display = "inherit";
            document.getElementById("version-select").style.display = "none";
        }
    }
    if(urlParams.get("d")!=null) {
        search_disable = urlParams.get("d");
        if(document.getElementById(search_disable)) {
            document.getElementById(search_disable + "-button").style.display = "none";
        }
    }
    document.getElementById(search_source).classList.add("active");
    document.getElementById(search_type).classList.add("active");
    if(search_source==="modrinth"||search_source==="curseforge") {
        document.getElementById("load").style.display = "flex";
    }
    callJavaMethod("sync.search."+search_source+"."+search_type+"."+search_version.replaceAll(".","%")+"."+search_query+".0."+search_instance);
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

function addItem(png,name,author,description,id,slug,source) {
    if(source===search_source) {
        const base = document.getElementById("template");
        const item = base.cloneNode(true);
        if(source==="curseforge") {
            item.id = id;
        } else {
            item.id = slug;
        }
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
                            if(search_source === "modrinth") {
                                callJavaMethod("modrinth.install." + search_type + "." + slug+"."+search_instance+"."+search_version);
                            } else if(search_source === "curseforge") {
                                callJavaMethod("curseforge.install." + search_type + "." + id+"."+search_instance+"."+search_version);
                            }
                        }
                    }
                }
            } else {
                if(search_source==="zyneon") {
                    callJavaMethod("button.install."+id);
                } else if(search_source==="modrinth") {
                    callJavaMethod("modrinth.install.modpack."+id+"."+search_version);
                } else if(search_source==="curseforge") {
                    callJavaMethod("curseforge.install.modpack."+id+"."+search_version);
                }
            }
        };
        if(search_source==="zyneon") {
            item.querySelector("#show").style.display = "none";
        } else if(search_source==="curseforge") {
            item.querySelector("#show").innerText = "SHOW ON CURSEFORGE"
            item.querySelector("#show").onclick = function () {
                if(search_type) {
                    let s = "";
                    if(search_type==="modpacks") {
                        s = "modpacks";
                    } else if(search_type==="fabric"||search_type==="forge") {
                        s = "mc-mods";
                    } else if(search_type==="shaders") {
                        s = "shaders";
                    } else if(search_type==="resourcepacks") {
                        s = "texture-packs";
                    }
                    openInBrowser("https://www.curseforge.com/minecraft/"+s+"/"+slug);
                }
            };
        } else {
            item.querySelector("#show").innerText = "SHOW ON MODRINTH"
            item.querySelector("#show").onclick = function () {
                openInBrowser("https://modrinth.com/mod/"+slug);
            };
        }
    }
}

function setButton(id,con) {
    const item = document.getElementById(id);
    const button = item.querySelector("#install");
    button.innerHTML = con;
}

function loadMore() {
    i = i + 1;
    callJavaMethod("sync.search."+search_source+"."+search_type+"."+search_version.replaceAll(".","%")+"."+search_query+"."+i+"."+search_instance);
}