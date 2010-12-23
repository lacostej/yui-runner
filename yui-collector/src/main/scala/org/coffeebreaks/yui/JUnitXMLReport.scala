package org.coffeebreaks.yui

import xml.{Node, NodeSeq, Elem, XML}
import java.lang.String

/**
 * Created by IntelliJ IDEA.
 * @author jerome@coffeebreaks.org
 * @since  12/22/10 9:23 AM
 */

class JUnitXMLReport(xml: String) {
  val suites: List[TestSuite] = JUnitXMLReport.parseReport(xml)

  def countFailures() : Int = {
    suites.map(ts => ts.failures).sum
  }
  def countTests() : Int = {
    suites.map(ts => ts.tests).sum
  }
}

object JUnitXMLReport {
  def parseReport(xml: String): List[TestSuite] = {
    val root: Elem = XML.loadString(xml)
    val suites : Seq[TestSuite] = (root \ "testsuite").map(node => JUnitXMLReport.suiteFromXml(node))
    suites.asInstanceOf[List[TestSuite]]
  }

  def suiteFromXml(n : Node): TestSuite = {
    var cases: Seq[TestCase] = (n \ "testcase").map(node => JUnitXMLReport.caseFromXml(node))
    new TestSuite(attText(n, "name"), attText(n, "tests").toInt, attText(n, "failures").toInt, attText(n, "time"), cases.asInstanceOf[List[TestCase]])
  }

  def caseFromXml(n : Node): TestCase = {
    val failures: Seq[Node] = (n \ "failure")
    val failure: Option[Failure] = (if (failures.isEmpty) None else new Some(JUnitXMLReport.failureFromXml(failures(0))))
    new TestCase(attText(n, "name"), attText(n, "time"), failure)
  }

  def failureFromXml(n : Node): Failure = {
    new Failure(attText(n, "message"))
  }

  def attText(n: Node, att: String): String = {
    n.attribute(att).get.text
  }
}

class TestSuite(val name: String, val tests: Int, val failures: Int, val time: String, val cases: List[TestCase]) {
}
class TestCase(val name: String, val time: String, val failure : Option[Failure]) {
  def hasFailed : Boolean = !failure.isEmpty
}
class Failure(val message: String)
