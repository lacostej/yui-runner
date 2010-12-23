package utils

import org.mortbay.jetty.Server
import org.mortbay.jetty.webapp.WebAppContext


/**
 * Created by IntelliJ IDEA.
 * @author jerome@coffeebreaks.org
 * @since  12/21/10 8:20 AM
 */

class JettyUtilsObject(var baseUrl: String, var server:Server) {
}

object JettyUtils {
  val o: JettyUtilsObject = new JettyUtilsObject(null, null)

  def getServer : Server = {
    o.server
  }

  def getBaseUrl : String = {
    o.baseUrl
  }

  def startServer(port: Int, webAppDir: String) : Unit = synchronized {
    if (o.server == null) {
       val server : Server = new Server(port)
       server.addHandler(new WebAppContext(webAppDir, "/"))

       server.start

       val actualPort : Int = server.getConnectors()(0).getLocalPort
       val baseUrl : String = "http://localhost:" + actualPort

      o.server = server
      o.baseUrl = baseUrl
    }
  }

  def main(args: Array[String]) = {
    JettyUtils.startServer(8080, "src/main/webapp")
    println(JettyUtils.getBaseUrl)
  }
}
