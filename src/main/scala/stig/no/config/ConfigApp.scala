package stig.no.config

import java.io.File

import com.typesafe.config.ConfigFactory
import mail.Mail
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.slf4j.LoggerFactory

object ConfigApp {

  def logger = LoggerFactory.getLogger(this.getClass)
  private val clientController = new ClientController
  private val confFileName = "application.conf"

  def main (args: Array[String]):Unit = {
    logger.debug("started!")

    val configFile: File = new File(confFileName)
    if (! configFile.exists()){
      logger.warn(f"Exiting, configuration file not found: $confFileName")
    }
    else {
      val configuration = readConfiguration(configFile)
      logger.debug("Configuration file read successfully")
      clientController.start(configuration)
    }
  }

  private def readConfiguration(configFile: File): MyConfig = {
    val conf = ConfigFactory.parseFile(configFile)

    conf.checkValid(ConfigFactory.defaultReference(), "nems", "tasks", "data_sources", "notifications", "email")

    val nemsConfig = conf.as[CfgNems]("nems")

    val myNotifications = conf.as[CfgNotifications]("notifications")

    val myDataSources = conf.as[Set[CfgDataSource]]("data_sources")

    val myTasks = conf.as[List[CfgTask]]("tasks")

    val myMail = conf.as[CfgMail]("email")

    val myConfig = MyConfig(nemsConfig, myNotifications, myDataSources, myTasks, myMail)
    myConfig
  }

  trait TaskResult{
    def isOk: Boolean
  }
  trait Reporter {
    def sendReport(results: List[TaskResult]): Boolean
  }

  trait MailSender {
    def a(mail: Mail)
  }

  case class TaskSuccess(task: CfgTask) extends TaskResult{
    override def isOk: Boolean = true
  }

  case class TaskFailure(task: CfgTask, errorMsg: String) extends TaskResult {
    override def isOk: Boolean = false
  }

  case class MyConfig(nemsConfig: CfgNems,
                      notifications: CfgNotifications,
                      data_sources: Set[CfgDataSource],
                      tasks: List[CfgTask],
                      mail: CfgMail)

  case class CfgMail(from_name: String, from_email: String, servers: List[CfgMailServer])

  case class CfgMailServer(name: String, host: String, port: Int, auth: Boolean, protocol: String, credentials: CfgCredentials)

  case class CfgNotifications(recipients: Set[String], notify_level: String, use: Set[String])

  case class CfgCredentials(username: String, password: String, api_key: String = "")

  case class CfgDataSource(name: String, category: String, credentials: CfgCredentials)

  case class CfgTask(name: String, data_source_ref: String, tag_name: String, nems_data_type: String)

  case class CfgNems(target_url: String, credentials: CfgCredentials)
}
