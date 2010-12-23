var $App = function(){
    return {
    };
};

$App.Test = function() {
  // private variables and methods
    return {  // public variables and methods
       Start: function(code) {
            $.getScript("scripts/tests/appTests.js", function() {
                AppTestPage(code);
            });
       }
    };
 }();
