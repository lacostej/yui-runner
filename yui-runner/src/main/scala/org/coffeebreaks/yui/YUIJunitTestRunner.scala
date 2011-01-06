package org.coffeebreaks.yui

import java.io.{FileWriter, File}
import java.lang.{String}
import java.lang.reflect.Constructor
import java.net.URL
import scala.collection.JavaConversions._
import org.apache.commons.io.IOUtils
import org.junit.runners.{ParentRunner}
import org.junit.runner.{Result, Description}
import org.junit.runner.notification.{Failure => JUnitFailure, RunNotifier}
import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.support.ui.{TimeoutException, WebDriverWait, ExpectedCondition}

/**
 * Created by IntelliJ IDEA.
 * @author jerome@coffeebreaks.org
 * @since  12/20/10 5:07 PM
 */
/**
 * Parent should have a constructor
 */
trait YUITestIF {
  def getUrl : URL
  def getDriver : WebDriver
}

abstract class YUITest(val url: URL) extends YUITestIF {
  def getUrl : URL = url
}

class VisibilityOfElementLocated(by: By) extends ExpectedCondition[Boolean] {
  def apply(driver: WebDriver): Boolean = {
    driver.findElement(this.by)
    true
  }
}

class YUIJunitTestRunner(testClass: Class[YUITest]) extends ParentRunner[YUITest](testClass) {

  def runChild(child: YUITest, notifier: RunNotifier) = {
    val description: Description = describeChild(child)
    notifier.fireTestStarted(description)
    var reports: Option[YUIReports] = None
    try {
      reports = Some(runYUITest(child))
    } catch {
      case t: Throwable => {
        notifier.fireTestFailure(new JUnitFailure(description, t))
      }
    }
    finally {
      if (reports == None) {
        notifier.fireTestIgnored(description)
      } else {
        val yuiReports: YUIReports = reports.get
        if (yuiReports.countFailures() > 0) {
          notifier.fireTestFailure(new JUnitFailure(description, new Throwable("test failed. Check the logs")))
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
    val dir: File = new File("target/surefire-reports/")
    if (!dir.exists && !dir.mkdirs) {
      println("Couldn't create to " + dir.getAbsolutePath + " - file not saved!")
    } else {
      val url: URL = child.getUrl
      val file: File = new File(dir, YUIJunitTestRunner.getSurefireFileName(child))

      val content: String = report.results
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

  def instantiateTest(testClass : Class[YUITest], url: URL) : YUITest = {
    val constructor : Constructor[YUITest] = testClass.getConstructor(url.getClass)
    constructor.newInstance(url)
  }

  def getChildren : java.util.List[YUITest] = {
    val urlsFromAnnotation: java.lang.annotation.Annotation = testClass.getAnnotation(classOf[URLsFrom])
    if (urlsFromAnnotation == null) {
      throw new IllegalStateException("missing @URLsFrom annotation ")
    }
    val urlLister : URLsLister = urlsFromAnnotation.asInstanceOf[URLsFrom].value.newInstance
    urlLister.getURLs.map(url => instantiateTest(testClass, url)).toList
  }

  def describeChild(child: YUITest) : Description = {
    Description.createTestDescription(testClass, YUIJunitTestRunner.getSimpleName(child.getUrl))
  }

  def runYUITest(test: YUITest) : YUIReports = {
    val url : URL = test.getUrl
    var driver: WebDriver = test.getDriver

    // if reports aren't empty here, we have a problem. We should at least warn
    YUIReportCollector.clear
    driver.get(url.toString)
    if (hasYUITests(driver)) {
      waitForYUITestsToRun(driver, 60)
    }
    YUIReportCollector.getYUIReports
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
        println("ERROR: Results aren't yet published after " + timeInSeconds + " seconds")
      }
    }
  }
}

object YUIJunitTestRunner {
  def getSurefireFileName(child: YUITest) : String = {
    "TEST-" + child.getClass.getName + "-" + getSimpleName(child.getUrl).replace(".", "_")  + ".xml"
  }

  def getSimpleName(url: URL) : String = {
    var result = url.getPath
    if (result.startsWith("/")) {
      result = result.substring(1)
    }
    result.replace("/", "_")
  }
}