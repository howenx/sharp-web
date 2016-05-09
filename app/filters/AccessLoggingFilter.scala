package filters
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import play.api.mvc._

/**
  * create by hxdhan @20150602
  * daumkakao china
  */

class AccessLoggingFilter extends Filter {

  val accessLogger = Logger("access")

  def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    val resultFuture = next(request)

    resultFuture.foreach(result => {
      request.method match {
        case "HEAD" =>
          None
        case _ =>
          val msg = s"method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress}" +
            s" status=${result.header.status}";
          accessLogger.info(msg)
      }

    })

    resultFuture
  }
}
