package stig.no.config

import mail.send
import org.slf4j.LoggerFactory
import stig.no.config.ConfigApp._

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class ClientController {

  def logger = LoggerFactory.getLogger(this.getClass)

  private val taskRunner = new TaskRunner

  def start(config: MyConfig): Unit = {

    logger.debug(f"Config contains ${config.tasks.size} tasks")

    val taskResults: List[TaskResult] = runTasks(config)
    reportResults(config, taskResults)

  }

  private def reportResults(config: MyConfig, taskResults: List[TaskResult]) = {
      config.mail.servers.map { mailServer =>
        val reporter = new MailReporter(mailServer, new send)
        reporter.sendReport(taskResults)
      }

  }

  private def runTasks(config: MyConfig): List[TaskResult] = {
    config.tasks.map(task => taskRunner run task)
  }
}
