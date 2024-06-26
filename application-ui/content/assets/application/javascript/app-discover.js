let searchTerm = "Click to search";

function openSearch() {
    const search = document.getElementById("discover-search");
    if(!search.classList.contains('active')) {
        search.classList.add('active');
        connector("sync.discover.search."+document.getElementById("search-type-select").value);
    }

    const buttons = document.getElementById("search-buttons");
    if(!buttons.classList.contains('active')) {
        buttons.classList.add('active');
    }

    const start = document.getElementById("discover-buttons");
    if(!start.classList.contains('active')) {
        start.classList.add('active');
    }

    const bar = document.getElementById("search-card");
    if(!bar.classList.contains('active')) {
        bar.classList.add('active');
    }

    deactivateMenu("menu",true);
    document.getElementById("search-bar").placeholder = "Goggles";
}

function closeSearch() {
    window.location.reload();
}

function toggleSearch() {
    const search = document.getElementById("discover-search");
    if(search.classList.contains('active')) {
        closeSearch();
    } else {
        openSearch();
    }
}

function addResult(id,img,title,authors,description,meta,actions,location) {
    const template = document.getElementById("result-template");
    if(template) {
        const result = template.cloneNode(true);
        if(id) {
            result.id = id;
            result.querySelector("img").onclick = function () {
                connector("sync.discover.details.module."+location);
            };
            result.querySelector("a").onclick = function () {
                connector("sync.discover.details.module."+location);
            };
        } else {
            result.id = "";
        }
        if(img) {
            result.querySelector("img").src = img;
        }
        if(title) {
            result.querySelector(".result-title").innerText = title;
        }
        if(authors) {
            result.querySelector(".result-authors").innerText = authors;
        }
        if(description) {
            description = decodeURL(description);
            result.querySelector(".result-description").innerHTML = description;
        }
        if(meta) {
            result.querySelector(".result-meta").innerHTML = meta;
        } else {
            result.querySelector(".result-meta").style.display = "none";
        }
        if(actions) {
            result.querySelector(".result-actions").innerHTML = actions;
        }
        template.parentNode.insertBefore(result,template);
    } else {
        error("Couldn't find result template.");
    }
}