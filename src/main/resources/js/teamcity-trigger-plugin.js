(function ($) { // this closure helps us keep our variables to ourselves.
// This pattern is known as an "iife" - immediately invoked function expression

    // form the URL
    var url = AJS.contextPath() + "/rest/teamcity-trigger/1.0/";

    // wait for the DOM (i.e., document "skeleton") to load. This likely isn't necessary for the current case,
    // but may be helpful for AJAX that provides secondary content.
    $(document).ready(function() {
        AJS.$("#testConn").click(function () {
            var data = toJSONString(document.getElementById("teamcityForm"));
            testConnection(data);
        });
        AJS.$("#save").click(function () {
            save();
        });
        AJS.$("#update").click(function () {
           save();
        });
        AJS.$("#delete").click(function () {

        });
        if(AJS.$("#buildConfigId").val().length > 0 ) {
            AJS.$("#buildConfigDiv").show();
        }
    });

    function testConnection(data) {
        AJS.$.ajax({
            url: url + "test",
            type: "POST",
            contentType: "application/json",
            data: data,
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
                AJS.$("#alerts").text("Connection Successful.");
                AJS.$("#buildConfigDiv").show();
            }
        }).done(function () {
            console.log("done");
        });
    }

    function save() {
        AJS.$("#buildConfigId").val(AJS.$("#buildConfig").val());
        AJS.$.ajax({
            url: url + "save",
            type: "POST",
            contentType: "application/json",
            data: toJSONString(document.getElementById("teamcityForm")),
            processData: false,
            beforeSend: function () {
                AJS.$("#alerts").empty();
                AJS.$("#alerts").removeClass("error");
            },
            error: function(jqXHR) {
                console.log(jqXHR.responseText);
                AJS.$("#alerts").addClass("error");
                AJS.$("#alerts").text("Could not save user please try again.");
            },
            success: function() {
                AJS.$("#alerts").addClass("success");
                AJS.$("#alerts").text("Save Successful.");
                AJS.$("#buildConfigDiv").show();
            }
        }).done(function () {
            console.log("done");
        });
    }

    function toJSONString( form ) {
        var obj = {};
        var elements = form.querySelectorAll( "input" );
        for( var i = 0; i < elements.length; ++i ) {
            var element = elements[i];
            var name = element.name;
            var value = element.value;

            if( name ) {
                obj[ name ] = value;
            }
        }

        return JSON.stringify( obj );
    }

})(AJS.$ || jQuery);
