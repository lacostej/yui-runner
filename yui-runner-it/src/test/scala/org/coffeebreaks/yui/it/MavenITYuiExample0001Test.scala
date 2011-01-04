package org.coffeebreaks.yui.it

import org.junit.Test
import org.junit.Assert._
import java.io.File
import org.apache.maven.it.Verifier
import org.apache.maven.it.util.ResourceExtractor

class MavenITYuiExample0001Test {
  @Test def testSurefireWrapping() = {
    val testDir: File = ResourceExtractor.simpleExtractResources( getClass(), "/example-0001" )
    val verifier: Verifier = new Verifier(testDir.getAbsolutePath, null, true)
    
    verifier.displayStreamBuffers

    /*val cliOptions: List = [ "-N" ]
    verifier.setCliOptions()
    */
    verifier.executeGoal("install")

    verifier.verifyTextInLog("Tests run: 3, Failures: 0, Errors: 2, Skipped: 1, Time elapsed:")
    verifier.verifyTextInLog("Tests in error:")
    verifier.verifyTextInLog("example2Test.html(myapp.YUI2Test)")
    verifier.verifyTextInLog("example1Test.html(myapp.YUI2Test)")

    verifier.verifyErrorFreeLog

    verifier.resetStreams
  }
}
