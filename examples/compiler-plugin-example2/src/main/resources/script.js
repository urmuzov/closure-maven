/**
 * Some platform-independent application logic
 */
function getData() {
    return "Time: " + new Date().toString();
}

function renderDesktopVersion() {
    var div = document.createElement('div');
    div.innerHTML = "<h1>Desktop Version</h1>"+getData();
    document.body.appendChild(div);
}

function renderMobileVersion() {
    var div = document.createElement('div');
    div.innerHTML = "<h1>Mobile Version</h1>"+getData();
    div.style.width = "240px";
    div.style.height = "320px";
    div.style.backgroundColor="#eee";
    document.body.appendChild(div);
}