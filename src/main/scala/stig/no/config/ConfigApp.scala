package stig.no.config

import java.io.File

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import ch.qos.logback.core.util.StatusPrinter
import ch.qos.logback.classic.LoggerContext

object ConfigApp {

  def logger = LoggerFactory.getLogger(this.getClass)

  def main (args: Array[String]) = {
    logger.info("started!")

    val confFileName: String = "application.con"
    val configFile: File = new File(confFileName)
    if (! configFile.exists()){
      logger.warn(f"Exiting, configuration file not found: $confFileName")
    }
    else {
      val conf =  ConfigFactory.parseFile(configFile)

      logger.info("The answer is: " + conf.getString("simple-app.answer"))
    }
  }
}
