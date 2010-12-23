package org.coffeebreaks.yui

import org.junit.Test
import org.junit.Assert._
import xml.Node

class JUnitXMLReportTest {
  val xml1: String = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><testsuites><testsuite name=\"Go Fishing\" tests=\"4\" failures=\"0\" time=\"4.07\"><testcase name=\"testForm\" time=\"0\"></testcase><testcase name=\"testEmail\" time=\"0.01\"></testcase><testcase name=\"testPhishingPhraseKO\" time=\"2.005\"></testcase><testcase name=\"testPhishingPhraseOK\" time=\"2.008\"></testcase></testsuite></testsuites>"
  val xml2: String = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><testsuites><testsuite name=\"Go Fishing\" tests=\"4\" failures=\"1\" time=\"4.07\"><testcase name=\"testForm\" time=\"0\"><failure message=\"BOUM\"><![CDATA[BOUM]]></failure></testcase><testcase name=\"testEmail\" time=\"0.01\"></testcase><testcase name=\"testPhishingPhraseKO\" time=\"2.005\"></testcase><testcase name=\"testPhishingPhraseOK\" time=\"2.008\"></testcase></testsuite></testsuites>"
  val xml3: String = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><testsuites></testsuites>"

  @Test def testFullDeserializationFromXML1OKReport: Unit  = {
    val report : JUnitXMLReport = new JUnitXMLReport(xml1)

    assertEquals(1, report.suites.size)

    val suite: TestSuite = report.suites(0)
    assertEquals("Go Fishing", suite.name)
    assertEquals(0, suite.failures)
    assertEquals(4, suite.tests)
    assertEquals("4.07", suite.time)
    val cases: List[TestCase] = suite.cases
    assertEquals(4, cases.size)

    assertTestCaseMatches(cases(0), "testForm", "0")
    assertTestCaseMatches(cases(1), "testEmail", "0.01")
    assertTestCaseMatches(cases(2), "testPhishingPhraseKO", "2.005")
    assertTestCaseMatches(cases(3), "testPhishingPhraseOK", "2.008")

    assertEquals(0, report.countFailures)
    assertEquals(4, report.countTests)
  }

  @Test def testFullDeserializationFromXML2WithFailure: Unit  = {
    val report : JUnitXMLReport = new JUnitXMLReport(xml2)

    assertEquals(1, report.suites.size)

    val suite: TestSuite = report.suites(0)
    assertEquals("Go Fishing", suite.name)
    assertEquals(1, suite.failures)
    assertEquals(4, suite.tests)
    assertEquals("4.07", suite.time)
    val cases: List[TestCase] = suite.cases
    assertEquals(4, cases.size)

    assertTestCaseMatches(cases(0), "testForm", "0", true)
    assertEquals("BOUM", cases(0).failure.get.message)
    assertTestCaseMatches(cases(1), "testEmail", "0.01")
    assertTestCaseMatches(cases(2), "testPhishingPhraseKO", "2.005")
    assertTestCaseMatches(cases(3), "testPhishingPhraseOK", "2.008")

    assertEquals(1, report.countFailures)
    assertEquals(4, report.countTests)
  }

  @Test def testFullDeserializationFromXML3EmptyReport: Unit  = {
    val report : JUnitXMLReport = new JUnitXMLReport(xml3)
    assertEquals(0, report.countTests)
    assertEquals(0, report.countFailures)
    assertEquals(0, report.suites.size)
  }

  @Test def testJUnitXMLReportParseFailedCase() = {
    // val xml : Node = <testsuites><testsuite name="Go Fishing" tests="4" failures="1" time="4.065"><testcase name="testForm" time="0.001"><failure message="BOUM"><![CDATA[BOUM]]></failure></testcase><testcase name="testEmail" time="0.003"></testcase><testcase name="testPhishingPhraseKO" time="2.005"></testcase><testcase name="testPhishingPhraseOK" time="2.007"></testcase></testsuite></testsuites>
    val node : Node = <testcase name="testForm" time="0.001"><failure message="BOUM"><![CDATA[BOUM]]></failure></testcase>
    val testcase : TestCase = JUnitXMLReport.caseFromXml(node)
    assertTrue(testcase.hasFailed)
  }

  def assertTestCaseMatches(testCase: TestCase, name: String, time: String, hasFailed : Boolean = false): Unit = {
    assertEquals(name, testCase.name)
    assertEquals(time, testCase.time)
    assertEquals(hasFailed, testCase.hasFailed)
  }
}