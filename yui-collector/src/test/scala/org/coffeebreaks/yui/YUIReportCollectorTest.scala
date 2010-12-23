package org.coffeebreaks.yui

import org.junit.Test
import org.junit.Assert._

class YUIReportCollectorTest {

  @Test def collectingReportsShouldAllowCountingOverallFailuresAndTests: Unit  = {
    val useragent : String = "user-agent 007";
    val timestamp: String = "Monday 20th December 2010";
    val results1: String = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><testsuites><testsuite name=\"Go Fishing\" tests=\"4\" failures=\"0\" time=\"4.07\"><testcase name=\"testForm\" time=\"0\"></testcase><testcase name=\"testEmail\" time=\"0.01\"></testcase><testcase name=\"testPhishingPhraseKO\" time=\"2.005\"></testcase><testcase name=\"testPhishingPhraseOK\" time=\"2.008\"></testcase></testsuite></testsuites>"
    val results2: String = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><testsuites><testsuite name=\"Go Fishing\" tests=\"4\" failures=\"1\" time=\"4.07\"><testcase name=\"testForm\" time=\"0\"><failure message=\"BOUM\"><![CDATA[BOUM]]></failure></testcase><testcase name=\"testEmail\" time=\"0.01\"></testcase><testcase name=\"testPhishingPhraseKO\" time=\"2.005\"></testcase><testcase name=\"testPhishingPhraseOK\" time=\"2.008\"></testcase></testsuite></testsuites>"

    assertTrue(YUIReportCollector.getYUIReports.getYUIReports.isEmpty)

    YUIReportCollector.addYUIReport(new YUIReport("http://localhost:8080/test1.html", results1, timestamp, useragent))
    YUIReportCollector.addYUIReport(new YUIReport("http://localhost:8080/test1.html", results2, timestamp, useragent))

    val reports: YUIReports = YUIReportCollector.getYUIReports
    assertEquals(2, reports.getYUIReports.size)

    assertEquals(1, reports.countFailures)
    assertEquals(8, reports.countTests)
  }

}