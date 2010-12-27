package org.coffeebreaks.yui

import java.lang.String
import java.util.Hashtable

import javax.servlet.http.{HttpServlet,
  HttpServletRequest => HSReq, HttpServletResponse => HSResp}
import java.io.PrintWriter

/**
 * Created by IntelliJ IDEA.
 * @author jerome@coffeebreaks.org
 * @since  12/20/10 7:23 PM
 */

class YUIReportCollector extends HttpServlet {
  val log: Boolean = true // FIXME proper logging...

  override def doGet(req: HSReq, resp: HSResp) = {
    val writer: PrintWriter = resp.getWriter
    writer.write(this.getClass + " running")
    writer.flush
    writer.close
  }

  override def doPost(req: HSReq, resp: HSResp) = {
    val requestUrl: StringBuffer = req.getRequestURL()
    val referrer: String = req.getHeader("referer")

    val post: Hashtable[String, Array[String]] = HttpServletUtils.getPostParameters(req)

    val results: String = HttpServletUtils.getParameter("results", post)
    val useragent: String = HttpServletUtils.getParameter("useragent", post)
    val timestamp: String = HttpServletUtils.getParameter("timestamp", post)

    if (log) {
      println("User-agent: " + req.getHeader("user-agent"))
      println("Referrer: " + referrer)
      println("Page: " + requestUrl)
      println("Result: " + results)
      println("User-agent: " + useragent)
      println("Timestamp: " +timestamp)
    }

    val report: YUIReport = new YUIReport(referrer, results, timestamp, useragent)
    YUIReportCollector.addYUIReport(report)
  }
}

class YUIReports {
  var reports: List[YUIReport] = Nil
  def addYUIReport(report: YUIReport) = {
    reports = report :: reports
  }
  def getYUIReports: List[YUIReport] = {
    reports
  }
  def clear = {
    reports = Nil
  }

  def countFailures() : Int = {
    reports.map(r => r.parse.countFailures).sum
  }
  def countTests() : Int = {
    reports.map(r => r.parse.countTests).sum
  }
}

case class YUIReport(referrer: String, results: String, timestamp: String, useragent: String) {
  private var parsed : JUnitXMLReport = new JUnitXMLReport(results)

  def parse() : JUnitXMLReport = {
    parsed
  }
}

object YUIReportCollector  {
  val reports : YUIReports = new YUIReports

  def addYUIReport(report: YUIReport) = {
    reports.addYUIReport(report)
  }

  def clear = {
    reports.clear
  }

  def getYUIReports: YUIReports = {
    reports
  }
}
