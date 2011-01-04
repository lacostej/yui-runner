package myapp

import org.openqa.selenium.WebDriver
import org.junit.{AfterClass, BeforeClass}
import org.junit.runner.RunWith
import java.io.File
import java.lang.{String}

import org.coffeebreaks.yui.{YUIJunitTestRunner, YUITest}
import org.openqa.selenium.htmlunit.HtmlUnitDriver

import utils.JettyUtils
import org.openqa.selenium.firefox.FirefoxDriver

/**
 * Sample class for running all YUI tests.
 *
 * The test starts/stops Jetty before and after running all tests and uses selenium to drive the testing.
 *
 * @author jerome@coffeebreaks.org
 * @since  12/20/10 5:07 PM
 */
@RunWith(classOf[YUIJunitTestRunner])
class YUI2Test(htmlFile: File) extends YUITest(htmlFile) {
  def getDriver : WebDriver = {
    YUI2Test.driver
  }
  def webUrlRoot : String = {
    YUI2Test.webUrlRoot
  }
}

object YUI2Test {
  val WEBAPP_STATIC_ROOT_DIR: String = "src/main/webapp"
  val YUI_TESTS_ROOT_DIR: String = WEBAPP_STATIC_ROOT_DIR
  var driver : WebDriver = createDriver

  def webUrlRoot = {
    JettyUtils.getBaseUrl
  }

  def createDriver : WebDriver = {
    new FirefoxDriver() // FIXME
    /*
    val driver : HtmlUnitDriver = new HtmlUnitDriver;
    driver.setJavascriptEnabled(true)
    driver
    */
  }

  @BeforeClass def startServer() = {
    val port: Int = 0 // means random
    JettyUtils.startServer(port, WEBAPP_STATIC_ROOT_DIR)
    println("Started jetty server on: " + webUrlRoot)
  }

  @AfterClass def stopServer() = {
    println("Stopping jetty server...")
    JettyUtils.getServer.stop
    driver.close
  }
}
