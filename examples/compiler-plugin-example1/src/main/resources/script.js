function appendCurrentDateToBody() {
    document.body.appendChild(document.createTextNode(new Date().toString()));
}

function onLoad() {
    appendCurrentDateToBody();
}
window['onLoad'] = onLoad;