package stig.no.config

import scala.xml.{Elem,XML}

class SoapClient {
  private def error(msg: String) = {
    println("SoapClient error: " + msg)
  }

  def wrap(xml: Elem) : String = {
    val buf = new StringBuilder
    buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n")
    buf.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n")
    buf.append("<SOAP-ENV:Body>\n")
    buf.append(xml.toString)
    buf.append("\n</SOAP-ENV:Body>\n")
    buf.append("</SOAP-ENV:Envelope>\n")
    buf.toString
  }

  def sendMessage(host: String, req: Elem) : Option[Elem] = {
    val url = new java.net.URL(host)
    val outs = wrap(req).getBytes
    val conn = url.openConnection.asInstanceOf[java.net.HttpURLConnection]
    try {
      conn.setRequestMethod("POST")
      conn.setDoOutput(true)
      conn.setRequestProperty("Content-Length", outs.length.toString)
      conn.setRequestProperty("Content-Type", "text/xml")
      conn.getOutputStream.write(outs)
      conn.getOutputStream.close
      Some(XML.load(conn.getInputStream))
    }
    catch {
      case e: Exception => error("post: " + e)
        error("post:" + scala.io.Source.fromInputStream(conn.getErrorStream).mkString)
        None
    }
  }
}

object SoapTest {
  def doTest1 {
    val host = "https://apitest.authorize.net/soap/v1/Service.asmx"
    val req  = <IsAlive xmlns="https://api.authorize.net/soap/v1/"/>
    val cli = new SoapClient
    println("##### request:\n" + cli.wrap(req))
    val resp = cli.sendMessage(host, req)
    if (resp.isDefined) {
      println("##### response:\n" + resp.get.toString)
    }
  }

  def doTestFahrenheitToCelsius {
    val host = "http://www.w3schools.com/webservices/tempconvert.asmx"
    val req  =
      <ns1:FahrenheitToCelsius xmlns:ns1='http://www.w3schools.com/webservices/'>
        <ns1:Fahrenheit>1</ns1:Fahrenheit>
      </ns1:FahrenheitToCelsius>
    val cli = new SoapClient
    println("##### request:\n" + cli.wrap(req))
    val resp = cli.sendMessage(host, req)
    if (resp.isDefined) {
      println("##### response:\n" + resp.get.toString)
    }
  }

  def doCalculator{
    val host = "http://ws1.parasoft.com/glue/calculator"
    val req  =
      <ns1:add xmlns:ns1='http://www.parasoft.com/wsdl/calculator/'>
        <ns1:x>2</ns1:x>
        <ns1:y>3</ns1:y>
      </ns1:add>
    val cli = new SoapClient
    println("##### request:\n" + cli.wrap(req))
    val resp = cli.sendMessage(host, req)
    if (resp.isDefined) {
      println("##### response:\n" + resp.get.toString)
    }
  }

  def doTest2 {
    val host = "http://ws.cdyne.com/WeatherWS/Weather.asmx"
    val req  = <GetCityForecastByZIP xmlns="http://ws.cdyne.com/WeatherWS/">
      <ZIP>77058</ZIP>
    </GetCityForecastByZIP>
    val cli = new SoapClient
    println("##### request:\n" + cli.wrap(req))
    val resp = cli.sendMessage(host, req)
    if (resp.isDefined) {
      println("##### response:\n")
      (resp.get  \\ "Forecast").foreach(elem => {
        println("#########\n" + elem.toString)
      })
    }
  }

  def main(args: Array[String]) {
//    doTest1
    doTestFahrenheitToCelsius
//    doCalculator
    //    doTest2
  }
}