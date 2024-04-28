let updates = false;

function toggleUpdates() {
    if(updates) {
        updates = false;
        connector('sync.autoUpdates.off');
    } else {
        updates = true;
        connector('sync.autoUpdates.on');
    }
}