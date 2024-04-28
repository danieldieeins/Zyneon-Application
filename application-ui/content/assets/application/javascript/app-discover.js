let searchSource = "modrinth";
let searchType = "modpacks";

function syncType() {
    document.getElementById(searchSource+"-modpacks").checked = false;
    document.getElementById(searchSource+"-mods").checked = false;
    document.getElementById(searchSource+"-datapacks").checked = false;
    document.getElementById(searchSource+"-shaders").checked = false;
    document.getElementById(searchSource+"-resourcepacks").checked = false;
    document.getElementById(searchSource+"-"+searchType).checked = true;

    document.getElementById("loader-filters").classList.remove("active");
    document.getElementById("category-filters").classList.remove("active");
    document.getElementById("environment-filters").classList.remove("active");
    document.getElementById("shader-category-filters").classList.remove("active");
    document.getElementById("shader-features-filters").classList.remove("active");
    document.getElementById("performance-filters").classList.remove("active");
    document.getElementById("resourcepack-category-filters").classList.remove("active");
    document.getElementById("resourcepack-features-filters").classList.remove("active");
    document.getElementById("resolution-filters").classList.remove("active");
    document.getElementById("modpack-category-filters").classList.remove("active");

    if(searchType === "mods") {
        document.getElementById("loader-filters").classList.add("active");
        document.getElementById("category-filters").classList.add("active");
        document.getElementById("environment-filters").classList.add("active");
    } else if(searchType === "datapacks") {
        document.getElementById("category-filters").classList.add("active");
    } else if(searchType === "shaders") {
        document.getElementById("shader-category-filters").classList.add("active");
        document.getElementById("shader-features-filters").classList.add("active");
        document.getElementById("performance-filters").classList.add("active");
        document.getElementById("environment-filters").classList.add("active");
    } else if(searchType === "resourcepacks") {
        document.getElementById("resourcepack-category-filters").classList.add("active");
        document.getElementById("resourcepack-features-filters").classList.add("active");
        document.getElementById("resolution-filters").classList.add("active");
        document.getElementById("environment-filters").classList.add("active");
    } else {
        document.getElementById("loader-filters").classList.add("active");
        document.getElementById("modpack-category-filters").classList.add("active");
        document.getElementById("environment-filters").classList.add("active");
    }
}