package stig.no.config

import mail.send
import org.slf4j.LoggerFactory
import stig.no.config.ConfigApp._

import scala.async.Async._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Await, Future}
import ExecutionContext.Implicits.global

class ClientController {

  def logger = LoggerFactory.getLogger(this.getClass)

  private val taskRunner = new TaskRunner

  def start(config: MyConfig): Unit = {

    logger.debug(f"Config contains ${config.tasks.size} tasks")

    val taskResults: Future[List[TaskResult]] = runTasks(config)
    reportResults(config, taskResults)

    //TODO not good, because we're waiting on the wrong future
    Await.result(taskResults, Duration.Inf)
  }


  private def reportResults(config: MyConfig, futureResults: Future[List[TaskResult]]) = {
    async {
      val taskResults = await(futureResults)
      config.mail.servers.map { mailServer =>
        val reporter = new MailReporter(mailServer, new send)
        val doneSending = reporter.sendReport(taskResults)
        (mailServer, doneSending)
      }
    } onFailure { case e =>
      logger.error(f"Something failed: $e")
    }

  }

  private def runTasks(config: MyConfig): Future[List[TaskResult]] = {
    Future.sequence(config.tasks.map(task => taskRunner run task))
  }
}
