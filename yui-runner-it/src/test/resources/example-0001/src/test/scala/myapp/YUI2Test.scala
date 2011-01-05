package myapp

import org.openqa.selenium.WebDriver
import org.junit.{AfterClass, BeforeClass}
import org.junit.runner.RunWith
import java.io.File
import scala.collection.JavaConversions._

import org.openqa.selenium.htmlunit.HtmlUnitDriver

import utils.JettyUtils
import org.openqa.selenium.firefox.FirefoxDriver
import java.net.URL
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.FileUtils
import org.coffeebreaks.yui.{URLsLister, URLsFrom, YUIJunitTestRunner, YUITest}
import java.lang.{Iterable, String}

/**
 * Sample class for running all YUI tests.
 *
 * The test starts/stops Jetty before and after running all tests and uses selenium to drive the testing.
 *
 * @author jerome@coffeebreaks.org
 * @since  12/20/10 5:07 PM
 */
@RunWith(classOf[YUIJunitTestRunner])
@URLsFrom(classOf[YUI2TestURLsFinder])
class YUI2Test(url : URL) extends YUITest(url) {
  def getDriver : WebDriver = {
    ResourcesManager.driver
  }
}

object YUI2Test {

  @BeforeClass def startServer() = {
    // ResourcesManager.initResources
  }

  @AfterClass def stopServer() = {
    ResourcesManager.terminateResources
  }
}

class YUI2TestURLsFinder extends URLsLister {
  val DEFAULT_YUI_TESTS_ROOT_DIR: String = Shared.WEBAPP_STATIC_ROOT_DIR
  val yuiDir: File = new File(DEFAULT_YUI_TESTS_ROOT_DIR)
  private val files1 : java.util.Collection[File] = FileUtils.listFiles(yuiDir, FileFilterUtils.suffixFileFilter("Test.html"), FileFilterUtils.trueFileFilter)
  val files : List[File] = files1.toList


  def pageToUrl(webappStaticRootDir: String, webUrlRoot: String, f: File) : URL = {
    var i : Int = f.getAbsolutePath.indexOf(webappStaticRootDir)
    new URL(webUrlRoot + f.getAbsolutePath.substring(i + webappStaticRootDir.length))
  }

  def getURLs: java.lang.Iterable[URL] = {
    println("Getting URLs: " + ResourcesManager.webUrlRoot)
    val urls: Iterable[URL] = files.map(f => pageToUrl(ResourcesManager.webappStaticRootDir, ResourcesManager.webUrlRoot, f))
    println("Got URLs " + urls.mkString(", "))
    urls
  }
}

object Shared {
  val WEBAPP_STATIC_ROOT_DIR: String = "src/main/webapp"
}

object ResourcesManager {
  var driver : WebDriver = createDriver

  def webappStaticRootDir = Shared.WEBAPP_STATIC_ROOT_DIR

  // this should ideally go in the @BeforeClass annotated method of your test class, but the Junit ParentRunner
  // class calls getDescription (and thus getURLs) before it calls the @BeforeClass statements :( FIXME
  ResourcesManager.initResources

  def createDriver : WebDriver = {
    new FirefoxDriver() // FIXME
    /*
    val driver : HtmlUnitDriver = new HtmlUnitDriver;
    driver.setJavascriptEnabled(true)
    driver
    */
  }

  def webUrlRoot: String = {
    JettyUtils.getBaseUrl
  }

  def initResources() = {
    val port: Int = 0 // means random
    println("Startig jetty on " + port)
    JettyUtils.startServer(port, ResourcesManager.webappStaticRootDir)
    println("Started jetty server on: " + ResourcesManager.webUrlRoot)
  }

  def terminateResources() = {
    println("Stopping jetty server...")
    JettyUtils.getServer.stop
    driver.close
  }
}