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
        AJS.$.ajax({
            url: url + "test",
            type: "POST",
            contentType: "application/json",
            data: AJS.$("#teamcityForm").serialize(),
            processData: false,
            beforeSend: function () {
                AJS.$("#alerts").empty();
                AJS.$("#alerts").removeClass("error");
            },
            error: function(jqXHR) {
                console.log(jqXHR.responseText);
                AJS.$("#alerts").addClass("error");
                AJS.$("#alerts").text("Could not connect please try again.")
            },
            success: function() {
                AJS.$("#alerts").addClass("success");
                AJS.$("#alerts").text("Connection Successful.")
            }
        }).done(function () {
            console.log("done");
        });
    }

})(AJS.$ || jQuery);
