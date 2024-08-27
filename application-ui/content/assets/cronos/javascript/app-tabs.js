const activeTabs = new Map();

function addTab(tabs,id,text,onclick) {
    if(typeof tabs === 'string' && typeof id === 'string' && typeof text === 'string' && typeof onclick === 'string') {
        if(!document.getElementById(id)) {
            document.getElementById(tabs).innerHTML += "<h4 id='" + id + "' onclick=\"switchTab('" + tabs + "','" + id + "'); " + onclick + "\">" + text + "</h4>";
        }
    }
}

function switchTab(tabs,tab) {
    if(typeof tabs === 'string' && typeof tab === 'string') {
        if(document.getElementById(tabs)&&document.getElementById(tab)) {
            if(activeTabs.has(tabs)) {
                if(document.getElementById(activeTabs.get(tabs))) {
                    document.getElementById(activeTabs.get(tabs)).classList.remove("active");
                }
                if(document.getElementById(activeTabs.get(tabs)+"-content")) {
                    document.getElementById(activeTabs.get(tabs)+"-content").classList.remove("active");
                }
                activeTabs.delete(tabs);
            }

            activeTabs.set(tabs,tab);
            if(document.getElementById(tab+"-content")) {
                document.getElementById(tab+"-content").classList.add("active");
            }
            document.getElementById(tab).classList.add("active");
        }
    }
}