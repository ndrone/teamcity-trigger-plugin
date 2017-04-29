(function ($) { // this closure helps us keep our variables to ourselves.
// This pattern is known as an "iife" - immediately invoked function expression

    // form the URL
    var url = AJS.contextPath() + "/rest/teamcity-trigger/1.0/";

    // wait for the DOM (i.e., document "skeleton") to load. This likely isn't necessary for the current case,
    // but may be helpful for AJAX that provides secondary content.
    $(document).ready(function() {
        AJS.$("#testConn").click(function () {
            testConnection();
        });
    });

    function testConnection() {
        var data = '{ "username": "' + AJS.$("#username").attr("value")
            + '", "password": "' +  AJS.$("#password").attr("value")
            + '", "url": "' + AJS.$("#url").attr("value") + '" }';
        console.log(data);
        AJS.$.ajax({
            url: url,
            type: "POST",
            contentType: "application/json",
            data: data,
            processData: false,
            error: function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                console.log(textStatus);
                console.log(errorThrown);
            },
            success: function() {
                console.log("Tested ok")
            }
        }).done(function () {
            console.log("done");
        });
    }

})(AJS.$ || jQuery);
