package stig.no.config

import java.io.File
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

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

      conf.checkValid(ConfigFactory.defaultReference(), "nems", "tasks", "data_sources", "notifications")

      val nemsConfig = conf.as[NemsConfig]("nems")

      val myNotifications = conf.as[MyNotifications]("notifications")

      val myDataSources = conf.as[Set[MyDataSource]]("data_sources")

      val myTasks = conf.as[List[MyTask]]("tasks")

      val myConfig = MyConfig(nemsConfig, myNotifications, myDataSources, myTasks)

      myConfig.tasks.foreach(println(_))

    }
  }

  case class MyConfig(nemsConfig: NemsConfig,
                      notifications: MyNotifications,
                      data_sources: Set[MyDataSource],
                      tasks: List[MyTask])

  case class MyNotifications(recipients: Set[String], notify_level: String)

  case class MyCredentials(username: String, password: String, api_key: String = "")

  case class MyDataSource(name: String, category: String, credentials: MyCredentials)

  case class MyTask(name: String, data_source_ref: String, tag_name: String, nems_data_type: String)

  case class NemsConfig(target_url: String, credentials: MyCredentials)
}
