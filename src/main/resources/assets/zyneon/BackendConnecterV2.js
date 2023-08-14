function callJavaMethod(message) {
	console.log("[Launcher-Bridge] "+message);
}

document.addEventListener('contextmenu', function(e) {
    e.preventDefault();
});

document.addEventListener('dragstart', function(e) {
    e.preventDefault();
});