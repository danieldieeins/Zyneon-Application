let highlighted = "";

function initDetails() {
    const urlParams = new URLSearchParams(window.location.search);

    let name = "Unknown";
    if(urlParams.get("title")||urlParams.get("name")) {
        if(urlParams.get("title")) {
            name = urlParams.get("title").replaceAll("%plus%","+");
        } else {
            name = urlParams.get("name").replaceAll("%plus%","+");
        }
    }
    document.getElementById("details-title").innerText = name;
    document.getElementById("details-name").innerText = name;

    let description = "No description...";
    if(urlParams.get("description")) {
        description = urlParams.get("description").replaceAll("%plus%","+");
    }
    setDescription(description);

    if(urlParams.get("changelog")) {
        setChangelog(urlParams.get("changelog").replaceAll("%plus%","+"));
    }

    if(urlParams.get("versions")) {
        setVersions(urlParams.get("versions").replaceAll("%plus%","+"))
    }

    let type = "Unknown";
    if(urlParams.get("type")) {
        type = urlParams.get("type").replaceAll("%plus%","+");
    }
    document.getElementById("details-type").innerText = type;

    let id = "unknown";
    if(urlParams.get("id")) {
        id = urlParams.get("id").replaceAll("%plus%","+");
    }
    document.getElementById("details-id").innerText = id;

    let version = "Unknown";
    if(urlParams.get("version")) {
        version = urlParams.get("version").replaceAll("%plus%","+");
    }
    document.getElementById("details-version").innerText = version;

    let customInfo = "";
    if(urlParams.get("c")) {
        customInfo = "<br>"+urlParams.get("c").replaceAll("\\'","%hhdn%").replaceAll("'","\"").replaceAll("%hhdn%","'").replaceAll("%plus%","+");
    }
    if(customInfo) {
        document.getElementById("details-custom").innerHTML = customInfo;
    }

    let summary = "No summary...";
    if(urlParams.get("summary")) {
        summary = urlParams.get("summary").replaceAll("%plus%","+");
    }
    document.getElementById("details-summary").innerHTML = summary;

    let authors = "Unknown";
    if(urlParams.get("authors")) {
        authors = urlParams.get("authors").replaceAll("%plus%","+");
    }
    document.getElementById("details-authors").innerText = authors;

    let hidden = "unknown";
    if(urlParams.get("hidden")) {
        hidden = urlParams.get("hidden");
    }
    document.getElementById("details-hidden").innerText = hidden;

    let tags = "No tags...";
    if(urlParams.get("tags")) {
        tags = urlParams.get("tags").replaceAll("%plus%","+");
    }
    document.getElementById("details-tags").innerText = tags;

    if(urlParams.get("cc")) {
        const customCard = document.getElementById("details-custom-card");
        customCard.innerHTML = urlParams.get("cc").replaceAll("%plus%","+");
        customCard.style.display = "inherit";
    }

    if(urlParams.get("tab")) {
        highlight(urlParams.get("tab"));
    }

    if(urlParams.get("background")) {
        document.getElementById("background").src = decodeURL(urlParams.get("background"));
    }

    if(urlParams.get("icon")) {
        document.getElementById("dh-icon").src = decodeURL(urlParams.get("icon"));
    } else {
        document.getElementById("details-icon").style.display = "none";
    }

    if(urlParams.get("logo")) {
        document.getElementById("dh-logo").src = decodeURL(urlParams.get("logo"));
    } else {
        document.getElementById("dh-logo").style.display = "none";
    }

    if(urlParams.get("thumbnail")) {
        document.getElementById("details-thumbnail").src = decodeURL(urlParams.get("thumbnail"));
    } else {
        document.getElementById("dh-thumbnail").style.display = "none";
    }
}

function setDescription(description_) {
    const description = document.getElementById("dh-description");
    if(description_) {
        description.innerHTML = description_;
        document.getElementById("dh-description-button").style.display = "inherit";
    } else {
        description.style.display = "none";
        document.getElementById("dh-description-button").style.display = "none";
    }
}

function setChangelog(changelog_) {
    const changelog = document.getElementById("dh-changelog");
    if(changelog_) {
        changelog_ = changelog_.replaceAll("%plus%","+");
        changelog.innerHTML = changelog_;
        document.getElementById("dh-changelog-button").style.display = "inherit";
    } else {
        changelog.style.display = "none";
        document.getElementById("dh-changelog-button").style.display = "none";
    }
}

function setVersions(versions_) {
    const versions = document.getElementById("dh-versions");
    if(versions_) {
        versions_ = versions_.replaceAll("%plus%","+");
        versions.innerHTML = versions_;
        document.getElementById("dh-versions-button").style.display = "inherit";
    } else {
        versions.style.display = "none";
        document.getElementById("dh-versions-button").style.display = "none";
    }
}

function highlight(new_) {
    const highlight = document.getElementById(new_+"-button");
    if(highlight) {
        if(!highlight.classList.contains("active")) {
            highlight.classList.add("active");
            if(highlighted) {
                const highlight = document.getElementById(highlighted+"-button");
                if(highlight) {
                    if(highlight.classList.contains("active")) {
                        highlight.classList.remove("active");
                    }
                }
                const show = document.getElementById(highlighted);
                if(show) {
                    if(!(show.style.display==="none")) {
                        show.style.display = "none";
                    }
                }
            }
            const show = document.getElementById(new_);
            if(show) {
                show.style.display = "inherit";
            }
            highlighted = new_;
        }
    }
}