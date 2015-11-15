package stig.no.config

import java.io.File
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

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
      val myConfig: MyConfig = readConfiguration(configFile)

//      myConfig.tasks.map(task => runTask(task))

//      val taskResults: List[Future[TaskResult]] = for {
//        task <- myConfig.tasks
//        taskResult <- runTask(task)
//      } yield taskResult


      val taskResults2: List[Future[TaskResult]] = myConfig.tasks.map(task => runTask(task))

      val x: Future[List[TaskResult]] = Future.sequence(taskResults2)

      x.map(taskRes => {
        logger.info(f"Finished with ${taskRes.size} tasks")
        taskRes.foreach(task => logger.info(f"Task 1 was: $task"))
        taskRes.foreach{ task =>
          task match {
            case TaskSuccess() => logger.info(f"Task ok: $task")
            case TaskFailure(msg) => logger.error(f"Task $task failed: $msg")
          }
        }
      })

      Await.result(x, Duration(10, SECONDS))
//      httpRetriever.getSomething()

    }
  }

  private def runTask(task: MyTask): Future[TaskResult] = {
    val random = Math.random()
    if (random > 0.6){
      Future(TaskFailure(f"Failure: $random"))
    }
    else Future(TaskSuccess())

  }

  private def readConfiguration(configFile: File): MyConfig = {
    val conf = ConfigFactory.parseFile(configFile)

    conf.checkValid(ConfigFactory.defaultReference(), "nems", "tasks", "data_sources", "notifications")

    val nemsConfig = conf.as[NemsConfig]("nems")

    val myNotifications = conf.as[MyNotifications]("notifications")

    val myDataSources = conf.as[Set[MyDataSource]]("data_sources")

    val myTasks = conf.as[List[MyTask]]("tasks")

    val myConfig = MyConfig(nemsConfig, myNotifications, myDataSources, myTasks)
    myConfig
  }

  trait TaskResult {

  }

  case class TaskSuccess() extends TaskResult

  case class TaskFailure(errorMsg: String) extends TaskResult

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
