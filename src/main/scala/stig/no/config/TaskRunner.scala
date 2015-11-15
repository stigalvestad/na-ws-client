package stig.no.config

import org.slf4j.LoggerFactory
import stig.no.config.ConfigApp.{TaskSuccess, TaskFailure, TaskResult, CfgTask}

import scala.concurrent.{Future, ExecutionContext}

class TaskRunner {

  def logger = LoggerFactory.getLogger(this.getClass)

  def run(task: CfgTask)(implicit ec: ExecutionContext): Future[TaskResult] = {
    logger.debug(f"Running task: ${task.name}")
    val random = Math.random()
    if (random > 0.6){
      Future(TaskFailure(task, f"Failure: $random"))
    }
    else Future(TaskSuccess(task))

  }
}
