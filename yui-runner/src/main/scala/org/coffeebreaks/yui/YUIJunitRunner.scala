package org.coffeebreaks.yui

import java.io.{FileWriter, File}
import java.lang.{String}
import java.lang.reflect.Constructor
import java.net.URL
import scala.collection.JavaConversions._
import org.apache.commons.io.{IOUtils, FileUtils}
import org.apache.commons.io.filefilter.{FileFilterUtils}
import org.junit.runners.{ParentRunner}
import org.junit.runner.{Result, Description}
import org.junit.runner.notification.{Failure, RunNotifier}
import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.support.ui.{TimeoutException, WebDriverWait, ExpectedCondition}

/**
 * Created by IntelliJ IDEA.
 * @author jerome@coffeebreaks.org
 * @since  12/20/10 5:07 PM
 */
trait YUITestIF {
  def getFile : File
  def getDriver : WebDriver
  def webUrlRoot : String
}

abstract class YUITest(val file: File) extends YUITestIF {
  def getFile : File = file
}

class VisibilityOfElementLocated(by: By) extends ExpectedCondition[Boolean] {
  def apply(driver: WebDriver): Boolean = {
    driver.findElement(this.by)
    true
  }
}

class YUIJunitTestRunner(testClass: Class[YUITest]) extends ParentRunner[YUITest](testClass) {
  val DEFAULT_WEBAPP_STATIC_ROOT_DIR: String = "src/main/webapp"
  val DEFAULT_YUI_TESTS_ROOT_DIR: String = DEFAULT_WEBAPP_STATIC_ROOT_DIR

  val yuiDir: File = new File(DEFAULT_YUI_TESTS_ROOT_DIR) // FIXME parametrize (annotation)
  val files = FileFilterUtils.filterSet(
      FileFilterUtils.suffixFileFilter(".html"),
      FileUtils.listFiles(yuiDir, Array("html"), true)
     );  // FIXME parametrize (annotation)

  def webappStaticRootDir = DEFAULT_WEBAPP_STATIC_ROOT_DIR

  def pageToUrl(test: YUITest, f: File) : URL = {
    var i : Int = f.getAbsolutePath.indexOf(webappStaticRootDir)
    new URL(test.webUrlRoot + f.getAbsolutePath.substring(i + webappStaticRootDir.length))
  }

  def runChild(child: YUITest, notifier: RunNotifier) = {
    val description: Description = describeChild(child)
    notifier.fireTestStarted(description)
    var reports: Option[YUIReports] = None
    try {
      reports = Some(runYUITest(child))
    } catch {
      case t: Throwable => {
        notifier.fireTestFailure(new Failure(description, t))
      }
    }
    finally {
      if (reports == None) {
        notifier.fireTestIgnored(description)
      } else {
        val yuiReports: YUIReports = reports.get
        if (yuiReports.countFailures() > 0) {
          notifier.fireTestFailure(new Failure(description, new Throwable("test failed. Check the logs")))
        } else if (yuiReports.countTests() == 0) {
          notifier.fireTestIgnored(description)
        } else {
          notifier.fireTestFinished(description)
        }
        yuiReports.getYUIReports.foreach(report => surefireDump(child, report))
      }
    }
  }

  def surefireDump(child: YUITest, report: YUIReport) = {
    val htmlFile: File = child.getFile
    val dir: File = new File("target/surefire-reports/")
    if (!dir.exists && !dir.mkdirs) {
      println("Couldn't create to " + dir.getAbsolutePath)
    } else {
      val content: String = report.results
      val file: File = new File(dir, "TEST-" + child.getClass.getName + "-" + htmlFile.getName.replace(".", "_") + ".xml")
      saveContentToFile(file, content)
    }
  }

  def saveContentToFile(file: File, content: String): Unit = {
    val writer: FileWriter = new FileWriter(file)
    try {
      IOUtils.write(content, writer)
    } catch {
      case _ => {
        println("Couldn't save " + content + " to " + file.getAbsolutePath)
      }
    } finally {
      IOUtils.closeQuietly(writer)
    }
  }

  def toResult(report: YUIReport) = {
    val result : Result = new Result()
    result
  }

  def instantiateTest(testClass : Class[YUITest], file: File) : YUITest = {
    val constructor : Constructor[YUITest] = testClass.getConstructor(file.getClass)
    constructor.newInstance(file)
  }

  def getChildren : java.util.List[YUITest] = {
    files.map(file => instantiateTest(testClass, file)).toList
  }

  def describeChild(child: YUITest) : Description = {
    Description.createTestDescription(testClass, child.getFile.getName)
  }

  def runYUITest(test: YUITest) : YUIReports = {
    val file: File = test.getFile
    var driver: WebDriver = test.getDriver

    var url : URL = pageToUrl(test, file)
    println("path: " + file.getAbsoluteFile)
    println("page: " + url)
    driver.get(url.toString)
    NB.tests.YUIReportCollector.clear
    if (hasYUITests(driver)) {
      waitForYUITestsToRun(driver, 20)
    }
    NB.tests.YUIReportCollector.getYUIReports
  }

  def exists(driver: WebDriver, condition: ExpectedCondition[Boolean]): Boolean = {
    try {
      new WebDriverWait(driver, 4).until(condition)
    } catch {
      case _ => {
        false
      }
    }
  }

  def hasYUITests(driver: WebDriver): Boolean = {
    exists(driver, new VisibilityOfElementLocated(By.id("yui-reporter-running")))
  }

  def waitForYUITestsToRun(driver: WebDriver, timeInSeconds: Int): Unit = {
    try {
      new WebDriverWait(driver, timeInSeconds).until(new VisibilityOfElementLocated(By.id("yui-reporter-ran")))
    } catch {
      case e: TimeoutException => {
        println("ERROR: Results aren't yet published after 10 seconds")
      }
    }
  }
}