package stig.no.config

import mail.Mail
import org.slf4j.LoggerFactory
import stig.no.config.ConfigApp._
import scala.concurrent.blocking

class MailReporter(cfgMail: CfgMailServer, send: MailSender) extends Reporter {

  def logger = LoggerFactory.getLogger(this.getClass)

  def getSubject(ok: Int, failed: Int) = {
    if (failed > 0){
      f"Failed: $failed tasks failed, while $ok tasks were run successfully."
    }
    else f"Success: $ok tasks were run successfully, no failures"
  }

  override def sendReport(results: List[TaskResult]): Boolean = {

    val buffer = new StringBuilder

    results.foreach {
      case taskRes@TaskSuccess(task) => buffer.append(f"Task ${taskRes.task.name}: OK")
      case taskRes@TaskFailure(task, msg) => buffer.append(f"Task ${taskRes.task.name}: Failed. Details: $msg")
    }

    val countOk = results.count(_.isOk)
    val countFailed = results.count(! _.isOk)

    val mailSubject = getSubject(countOk, countFailed)

    val mailContent = buffer.toString

    blocking {
      send a new Mail (
        from = "john.smith@mycompany.com" -> "John Smith",
        to = Seq("dev@mycompany.com", "marketing@mycompany.com"),
        subject = mailSubject,
        message = mailContent,
        richMessage = Some("Here's the <blink>latest</blink> <strong>Strategy</strong>..."),
        mailConfig = cfgMail
      )
    }
    true

  }

}
