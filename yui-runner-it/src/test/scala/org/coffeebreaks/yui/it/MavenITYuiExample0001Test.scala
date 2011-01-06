package org.coffeebreaks.yui.it

import org.junit.Test
import org.junit.Assert._
import java.io.File
import org.apache.maven.it.Verifier
import org.apache.maven.it.util.ResourceExtractor
import scala.collection.JavaConversions._

class MavenITYuiExample0001Test {
  @Test def testSurefireWrapping() = {
    val testDir: File = ResourceExtractor.simpleExtractResources( getClass(), "/example-0001" )
    val verifier: Verifier = new Verifier(testDir.getAbsolutePath, null, true)
    
    //verifier.setMavenDebug(true)

    /*val cliOptions: List = [ "-N" ]
    verifier.setCliOptions()
    */
    verifier.executeGoal("install")

    verifier.displayStreamBuffers

    VerifierUtil.displayMavenLog(verifier)

    verifier.verifyTextInLog("Tests run: 2, Failures: 0, Errors: 2, Skipped: 0, Time elapsed:")
    verifier.verifyTextInLog("Tests in error:")
    verifier.verifyTextInLog("example1Test.html(myapp.YUI2Test)")
    verifier.verifyTextInLog("example2Test.html(myapp.YUI2Test)")

    verifier.verifyErrorFreeLog

    verifier.resetStreams
  }
}

object VerifierUtil {
  def displayMavenLog(verifier: Verifier): Unit = {
    val lines = verifier.loadFile(verifier.getBasedir(), verifier.getLogFileName(), false)
    lines.foreach(line => println(line))
  }
}