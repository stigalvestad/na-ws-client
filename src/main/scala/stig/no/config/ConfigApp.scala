package stig.no.config

import java.io.File
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

object ConfigApp {

  def logger = LoggerFactory.getLogger(this.getClass)

  def main (args: Array[String]) = {
    logger.info("started!")

    val confFileName: String = "application.conf"
    val configFile: File = new File(confFileName)
    if (! configFile.exists()){
      logger.warn(f"Exiting, configuration file not found: $confFileName")
    }
    else {
      val conf = ConfigFactory.parseFile(configFile)

      conf.checkValid(ConfigFactory.defaultReference(), "nems-credentials", "tasks")
      logger.info("The answer is: " + conf.getString("nems-credentials.username"))

      val tasks = conf.getConfigList("tasks")
      val iterator: util.Iterator[_ <: Config] = tasks.iterator()
      while (iterator.hasNext){
        val task = iterator.next()
        val name = task.getString("name")
        println(f"Task $name")
      }

    }
  }
}
