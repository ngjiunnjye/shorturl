package com.github.ngjiunnjye.shorturl.creator.actor

import org.apache.kafka.clients.producer.ProducerRecord

import com.github.ngjiunnjye.kafka.{ Producer => KafkaProducer }
import com.github.ngjiunnjye.shorturl.utils.JsProtocol
import com.github.ngjiunnjye.shorturl.utils.UrlShorteningRequest
import com.github.ngjiunnjye.kafka.{ Consumer => KafkaConsumer }
import spray.json.DefaultJsonProtocol._

import spray.json.pimpAny
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.JavaConversions.seqAsJavaList
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.ngjiunnjye.shorturl.utils.UrlShorteningCommand
import spray.json.JsonParser


case class InventorySnapshot (lastRequestTime : Long, maxId : Long, preferedList : List [Long])

trait InventoryJournal {
  val journalTopic = "url.shortening.request"
  val snapShotTopic = "url.shortening.request-snapshot"
  var snapshotLastReqTime : Long = _
  final val POLL_TIMOUT_MS = 1000

  val kafkaProducer = KafkaProducer.createLongStringProducer
  implicit val snapShotFormat = jsonFormat3(InventorySnapshot)

  val consumer = KafkaConsumer.createLongStringConsumer(s"journal")
  

  def createRequestJournal(req: UrlShorteningRequest) = {
    import JsProtocol._
    snapshotLastReqTime = System.currentTimeMillis() 
    kafkaProducer.send(new ProducerRecord[Long, String](journalTopic,
    snapshotLastReqTime  , req.toJson.compactPrint))
  }

  def createRequestSnapShot(req: InventorySnapshot) = {
    kafkaProducer.send(new ProducerRecord[Long, String](snapShotTopic,
      snapshotLastReqTime, req.toJson.compactPrint))
  }  
  
}