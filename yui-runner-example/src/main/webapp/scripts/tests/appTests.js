// Load test methods external dependencies
// Then loads test functions
// last launch test methods for the page



AppTestPage = function(page) {
    $.getScript("scripts/tests/yui-min.js", function () {
      Y = YUI()
      Y.use('test', function(Y) {

        function handleRunnerStart(data){
          $("body").append("<div id=\"yui-reporter-running\" style=\"visibility: hidden;\"></div>")
        }
        function handleRunnerComplete(data){
          var results = Y.Test.Runner.getResults(),
              reporter = new Y.Test.Reporter("/tests/yui-report-collector", Y.Test.Format.JUnitXML);

          reporter.report(results);
          $("body").append("<div id=\"yui-reporter-ran\" style=\"visibility: hidden\">test-results published</div>")
        }
        function registerYUICollectorEvents() {
          Y.Test.Runner.subscribe(Y.Test.Runner.BEGIN_EVENT, handleRunnerStart);
          Y.Test.Runner.subscribe(Y.Test.Runner.COMPLETE_EVENT, handleRunnerComplete);
        }

        registerYUICollectorEvents();

        $.getScript('scripts/tests/test-' + page +'.js', function() {
          Y.Test.Runner.add(oSuite);
          Y.Test.Runner.run();
        });
      });
    });
}

