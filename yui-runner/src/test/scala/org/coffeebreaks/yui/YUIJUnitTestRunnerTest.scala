package org.coffeebreaks.yui

import org.junit.Test
import org.junit.Assert._
import java.net.URL
import org.openqa.selenium.WebDriver

class YUIJUnitTestRunnerTest {
  val URL1: URL = new URL("http://ignored.com/test/example.html")

  @Test def testGetSimpleName: Unit  = {
    assertEquals("test_example.html", YUIJunitTestRunner.getSimpleName(URL1))
  }

  @Test def getSurefireFileName: Unit = {
    val child = new DummyTest(URL1)
    assertEquals("TEST-org.coffeebreaks.yui.DummyTest-test_example_html.xml", YUIJunitTestRunner.getSurefireFileName(child))
  }
}
class DummyTest(url : URL) extends YUITest(url) {
  def getDriver : WebDriver = {
    null
  }
}
