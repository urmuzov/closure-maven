function appendCurrentDateToBody() {
    document.body.appendChild(document.createTextNode(new Date().toString()));
};