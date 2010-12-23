package org.coffeebreaks.yui

import javax.servlet.ServletInputStream
import java.util.{StringTokenizer, Hashtable}
import java.io.IOException
import javax.servlet.http.HttpServletRequest

/**
 * Created by IntelliJ IDEA.
 * Roughly taken http://java.sun.com/developer/onlineTraining/Programming/JDCBook/aucserv.html
 * @author jerome@coffeebreaks.org
 * @since  12/21/10 4:58 PM
 */
protected class HttpServletUtils {
}
object HttpServletUtils {
  private def parsePostData(length: Int, instream: ServletInputStream): Hashtable[String, Array[String]] = {
    var valArray: Array[String] = null
    var inputLen: Int = 0
    var offset: Int = 0
    var postedBytes: Array[Byte] = null
    var dataRemaining: Boolean = true
    var postedBody: String = null
    var ht: Hashtable[String, Array[String]] = new Hashtable[String, Array[String]]
    var sb: StringBuffer = new StringBuffer
    if(length <= 0) {
      return null
    }
    postedBytes = new Array[Byte](length)
    try {
      offset = 0
      while(dataRemaining) {
        inputLen = instream.read(postedBytes, offset, length - offset)
        if(inputLen <= 0) {
          throw new IOException("read error")
        }
        offset += inputLen
        if((length - offset) == 0) {
          dataRemaining = false
        }
      }
    }
    catch {
      case e: IOException => {
        System.out.println("Exception =" + e)
        return null
      }
    }
    postedBody = new String(postedBytes)
    var st: StringTokenizer = new StringTokenizer(postedBody, "&")
    var key: String = null
    var value: String = null
    while(st.hasMoreTokens) {
      var pair: String = st.nextToken.asInstanceOf[String]
      var pos: Int = pair.indexOf('=')
      if(pos == -1) {
        throw new IllegalArgumentException
      }
      try {
        key = java.net.URLDecoder.decode(pair.substring(0, pos))
        value = java.net.URLDecoder.decode(pair.substring(pos + 1, pair.length))
      }
      catch {
        case e: Exception => {
          throw new IllegalArgumentException
        }
      }
      if(ht.containsKey(key)) {
        var oldVals: Array[String] = ht.get(key).asInstanceOf[Array[String]]
        valArray = oldVals :+ value
      }
      else {
        valArray = new Array[String](1)
        valArray(0) = value
      }
      ht.put(key, valArray)
    }
    return ht
  }
  def getPostParameters(request: HttpServletRequest): Hashtable[String, Array[String]] = {
    if(request.getMethod.equals("POST") && request.getContentType.equals("application/x-www-form-urlencoded")) {
      return parsePostData(request.getContentLength, request.getInputStream)
    }
    return null
  }

  def getParameter(name: String, parameters: Hashtable[String, Array[String]]): String = {
    var vals : Array[String] = parameters.get(name).asInstanceOf[Array[String]]
    if(vals == null) {
      return null
    }
    vals.mkString(",")
  }
}
