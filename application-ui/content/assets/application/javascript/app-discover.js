let searchTerm = "Click to search";

function openSearch() {
    const search = document.getElementById("discover-search");
    if(!search.classList.contains('active')) {
        search.classList.add('active');
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
    const search = document.getElementById("discover-search");
    if(search.classList.contains('active')) {
        search.classList.remove('active');
    }

    const buttons = document.getElementById("search-buttons");
    if(buttons.classList.contains('active')) {
        buttons.classList.remove('active');
    }

    const start = document.getElementById("discover-buttons");
    if(start.classList.contains('active')) {
        start.classList.remove('active');
    }

    const bar = document.getElementById("search-card");
    if(bar.classList.contains('active')) {
        bar.classList.remove('active');
    }

    activateMenu("menu",true);
    document.getElementById("search-bar").placeholder = searchTerm;
}

function toggleSearch() {
    const search = document.getElementById("discover-search");
    if(search.classList.contains('active')) {
        closeSearch();
    } else {
        openSearch();
    }
}