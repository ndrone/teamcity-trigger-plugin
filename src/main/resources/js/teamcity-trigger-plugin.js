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
            deleteConfig();
        });
        AJS.$("#refresh").click(function () {
           fetchBuildConfigs();
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
                AJS.$("#alerts").removeClass("success");
            },
            error: function(jqXHR) {
                console.log(jqXHR.responseText);
                AJS.$("#alerts").addClass("error");
                AJS.$("#alerts").text("Could not connect please try again.")
            },
            success: function() {
                AJS.$("#alerts").addClass("success");
                AJS.$("#alerts").text("Connection Successful.");
                fetchBuildConfigs();
                AJS.$("#buildConfigDiv").show();
            }
        });
    }

    function save() {
        AJS.$("#buildConfigId").val(AJS.$("#buildConfig option:selected").val());
        AJS.$("#buildConfigName").val(AJS.$("#buildConfig option:selected").text());
        AJS.$.ajax({
            url: url + "save",
            type: "POST",
            contentType: "application/json",
            data: toJSONString(document.getElementById("teamcityForm")),
            processData: false,
            beforeSend: function () {
                AJS.$("#alerts").empty();
                AJS.$("#alerts").removeClass("error");
                AJS.$("#alerts").removeClass("success");
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
        });
    }

    function fetchBuildConfigs() {
        var selectedBuildId = AJS.$("#buildConfig option:selected").val();
        AJS.$.ajax({
            url: url + "fetchBuilds",
            type: "POST",
            contentType: "application/json",
            data: toJSONString(document.getElementById("teamcityForm")),
            processData: false,
            beforeSend: function () {
                AJS.$("#alerts").empty();
                AJS.$("#alerts").removeClass("error");
                AJS.$("#alerts").removeClass("success");
                AJS.$('html,body').css('cursor', 'wait');
            },
            error: function(jqXHR) {
                console.log(jqXHR.responseText);
                AJS.$("#alerts").addClass("error");
                AJS.$("#alerts").text("Could not connect please try again.")
            },
            success: function(data) {
                AJS.$("#buildConfig").empty();
                data.buildType.forEach(function (item) {
                    AJS.$("#buildConfig").append(AJS.$('<option>', {
                        value: item.id,
                        text: item.projectName + " :: " + item.name,
                        selected: item.id === selectedBuildId
                    }));
                });
                AJS.$('html,body').css('cursor', 'initial');
            }
        });
    }

    function deleteConfig() {
        AJS.$("#buildConfigId").val(AJS.$("#buildConfig option:selected").val());
        AJS.$("#buildConfigName").val(AJS.$("#buildConfig option:selected").text());
        AJS.$.ajax({
            url: url + "delete",
            type: "POST",
            contentType: "application/json",
            data: toJSONString(document.getElementById("teamcityForm")),
            processData: false,
            beforeSend: function () {
                AJS.$("#alerts").empty();
                AJS.$("#alerts").removeClass("error");
                AJS.$("#alerts").removeClass("success");
            },
            error: function(jqXHR) {
                console.log(jqXHR.responseText);
                AJS.$("#alerts").addClass("error");
                AJS.$("#alerts").text("Could not save user please try again.");
            },
            success: function() {
                AJS.$("#alerts").addClass("success");
                AJS.$("#alerts").text("Delete Successful.");
                AJS.$("#buildConfigDiv").hide();
                AJS.$("#buildConfig").empty();
                AJS.$("#buildConfigId").val('');
                AJS.$("#buildConfigName").val('');
                AJS.$("#username").val('');
                AJS.$("#password").val('');
                AJS.$("#url").val('');
            }
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
