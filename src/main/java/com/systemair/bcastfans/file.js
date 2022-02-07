/**
 * Waits for all active AJAX requests to finish during specified timeout. Works only for AJAX requests which are
 * instantiated using one of the following frameworks: jQuery, Prototype, Dojo. Don't work (immediately returns without
 * any errors) if standard AJAX API or one of other frameworks is used to send XML HTTP request.
 *
 * @param timeout Timeout in milliseconds.
 * @throws SeleniumError If timeout is reached.
 */
Selenium.prototype.doWaitForAjaxRequests = function(timeout) {
    return Selenium.decorateFunctionWithTimeout(function() {
        var userWindow = selenium.browserbot.getUserWindow();
        var isJqueryComplete = typeof(userWindow.jQuery) != 'function' || userWindow.jQuery.active == 0;
        var isPrototypeComplete = typeof(userWindow.Ajax) != 'function' || userWindow.Ajax.activeRequestCount == 0;
        var isDojoComplete = typeof(userWindow.dojo) != 'function' || userWindow.dojo.io.XMLHTTPTransport.inFlight.length == 0;
        return isJqueryComplete && isPrototypeComplete && isDojoComplete;
    }, timeout);
};