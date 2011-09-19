goog.provide('sample');

goog.require('goog.dom');

sample.doDesktop = function() {
    goog.dom.getElement('example').innerHTML = "Desktop JavaScript is OK!";
};
sample.doMobile = function() {
    goog.dom.getElement('example').innerHTML = "Mobile JavaScript is OK!";
};