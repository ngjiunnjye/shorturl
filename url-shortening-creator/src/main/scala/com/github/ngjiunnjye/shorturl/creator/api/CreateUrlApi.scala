package com.github.ngjiunnjye.shorturl.creator.api

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import com.github.ngjiunnjye.shorturl.creator.actor.InsertStatus
import com.github.ngjiunnjye.shorturl.utils.JsProtocol
import com.github.ngjiunnjye.shorturl.utils.UrlShorteningRequest

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller
import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directive.addByNameNullaryApply
import akka.http.scaladsl.server.Directive.addDirectiveApply
import akka.http.scaladsl.server.Directives.as
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Directives.decodeRequest
import akka.http.scaladsl.server.Directives.enhanceRouteWithConcatenation
import akka.http.scaladsl.server.Directives.entity
import akka.http.scaladsl.server.Directives.get
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.post
import akka.http.scaladsl.server.Directives.segmentStringToPathMatcher
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol
import com.github.ngjiunnjye.kafka.{ Producer => KafkaProducer }
import com.github.ngjiunnjye.shorturl.utils.UrlShorteningCommand
import org.apache.kafka.clients.producer.ProducerRecord
import com.github.ngjiunnjye.shorturl.utils.Config
import com.github.ngjiunnjye.shorturl.utils.JsProtocol
import spray.json.pimpAny
import com.github.ngjiunnjye.cryptor.Base62
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes
import com.github.ngjiunnjye.shorturl.utils.UrlShorteningRespond
import akka.http.scaladsl.server.StandardRoute


trait UrlApi extends DefaultJsonProtocol with Config {
  val kafkaProducer = KafkaProducer.createStringStringProducer
   
  implicit val system: ActorSystem 
  implicit val materializer: ActorMaterializer 
  import JsProtocol._
  val inventoryManager : ActorRef
  val urlRoute =
    path("url" / "create") {
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "<sourceUrl>, <targetUrl>"))
      } ~
        post {
          decodeRequest {
            entity(as[UrlShorteningRequest]) { req => processUrlShortReq(req)
            }
          }
        }
    }
  
  def processUrlShortReq (req : UrlShorteningRequest ) : StandardRoute = {

    complete {
      println(s"Request received ${req.longUrl} ->   ${
        req.shortUrl match {
          case Some(s) => s
          case None    => "NONE"
        }
      }")
      implicit val timeout = Timeout(30 seconds)
      val future = inventoryManager ? req
      val result = Await.result(future, timeout.duration).asInstanceOf[InsertStatus]

      val respond = UrlShorteningRespond(result.status, req.longUrl,
        if (result.status)
          Option(result.message)
        else
          req.shortUrl)

      if (result.status == true) {
        createCommand(req.longUrl,
          Base62.decode(result.message),
          req.shortUrl match {
            case Some(s) => false
            case None    => true
          })
        HttpResponse(entity = respond.toJson.compactPrint)
      } else
        HttpResponse(StatusCodes.BadRequest, entity = respond.toJson.compactPrint)

    }
  }
  
  def createCommand(longUrl: String, shortUrlId: Long, random : Boolean) = {
    import JsProtocol._
    val command = UrlShorteningCommand(normalizeUrl(longUrl), shortUrlId, random)
    kafkaProducer.send(new ProducerRecord[String, String]("url.shortening.command", 
        (shortUrlId%readerNodeAddresses.size).toInt, "", 
        command.toJson.compactPrint))
  }
  
  def normalizeUrl (longUrl : String) : String =
    if ((longUrl.startsWith("http://")) || (longUrl.startsWith("https://"))) longUrl  
    else s"http://${longUrl}"
}
