var oSuite = new Y.Test.Suite("Example 1 test suite");

oSuite.add(new Y.Test.Case({
   name: "Test Case 1",

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

 oSuite.add(new Y.Test.Case({
    name: "Test Case 2",
   testPassing : function () {
     Y.Assert.isTrue(true);
   }
 }));
