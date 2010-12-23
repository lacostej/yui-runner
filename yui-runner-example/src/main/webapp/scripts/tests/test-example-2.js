var oSuite = new Y.Test.Suite("Example 2 test suite");

oSuite.add(new Y.Test.Case({
   name: "Test Case 2",

   _should: {
     ignore:{
       testIgnored: true
     }
   },

   testPassing : function () {
     Y.Assert.isTrue(true);
   },
   testFailing :  function() {
     Y.Assert.isBoolean("a string")
   },
   testEmpty :  function() {
   },
   testIgnored :  function() {
     Y.Assert.isTrue(false); // won't fail
   }
 }));
