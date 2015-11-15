package stig.no.config

import dispatch._, Defaults._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}

class HttpRetriever {

  def logger = LoggerFactory.getLogger(this.getClass)

  def getSomething() = {
    val svc = url("http://www.wikipedia.org/")
    val response : Future[String] = Http.configure(_ setFollowRedirects true)(svc OK as.String.utf8)

    response onComplete {
      case Success(content) => {
        logger.info("Successful response" + content)
      }
      case Failure(t) => {
        logger.error("An error has occurred: " + t.getMessage)
      }
    }
  }
}
