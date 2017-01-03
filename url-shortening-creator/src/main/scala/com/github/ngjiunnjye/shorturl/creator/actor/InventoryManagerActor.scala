package com.github.ngjiunnjye.shorturl.creator.actor

import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.JavaConversions.seqAsJavaList
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

import com.github.ngjiunnjye.cryptor.Base62
import com.github.ngjiunnjye.shorturl.utils.Config
import com.github.ngjiunnjye.shorturl.utils.JsProtocol.requestFormat
import com.github.ngjiunnjye.shorturl.utils.UrlShorteningRequest

import akka.actor.Actor
import akka.actor.actorRef2Scala
import spray.json.JsonParser
import spray.json.ParserInput.apply

case class InsertStatus(status: Boolean, message: String)

class InventoryManagerActor extends Actor with Config  with InventoryJournal {
  
  private var maxId: Long = _
  val preferedList = new ListBuffer[Long]
  
  def getLastSnapshot : InventorySnapshot  = {
    consumer.subscribe(List(snapShotTopic))

    val records = consumer.poll(POLL_TIMOUT_MS)
    consumer.unsubscribe()
    if (records.isEmpty())
        InventorySnapshot(0, 0, List())
    else {   
      println (s" last snapshot ${records.last.value()}")
      JsonParser(records.last.value()).convertTo[InventorySnapshot]
    }
    
  
  }  
  def journalPlayback  = {
    val invSnapShot = getLastSnapshot
    val snapShotTime = invSnapShot.lastRequestTime
    println ("getLastSnapshot")
    maxId = invSnapShot.maxId
    preferedList ++= invSnapShot.preferedList
    
    consumer.subscribe(List(journalTopic))
    val records = consumer.poll(POLL_TIMOUT_MS)
    println (s"journalPlayback ${records.size}")
    records.foreach { record =>
      val kafkaMsg = record.value
      if (record.key > snapShotTime)
        processCreateRequest(JsonParser(kafkaMsg).convertTo[UrlShorteningRequest]) 
    }
    consumer.close()
  }

  override def preStart() = {
    scheduleSnapshotMessage
    journalPlayback
    println (s"maxId ${maxId}")
    println (s"preferedList ${preferedList.mkString(",")}")
  }

  def receive = {
    case req: UrlShorteningRequest => {
      createRequestJournal(req)
      sender ! processCreateRequest(req)
    }
    case "Snapshot" => {
      createRequestSnapShot(InventorySnapshot(snapshotLastReqTime, maxId, preferedList.toList))
      scheduleSnapshotMessage
    }
    case _ => println("unknown Request")
  }

  def processCreateRequest(req: UrlShorteningRequest) = {
    println (s"InventoryManager got ${req}")
    req.shortUrl match {
      case Some(target) => processCreateRequestPefered(req.longUrl, target)
      case None         => processCreateRequestRandom(req.longUrl)
    }
  }

  def processCreateRequestPefered(longUrl: String, shortUrl: String): InsertStatus = {
    val shortUrlId = Base62.decode(shortUrl)
    if (preferedList.contains(Base62.decode(shortUrl))) {
      println(s"${shortUrl} not available")
      InsertStatus(false, s"${shortUrl} not available")
    } else {
      preferedList +=(Base62.decode(shortUrl))
      finalizeSuccess(longUrl, shortUrlId, false)
    }

  }

  def processCreateRequestRandom(longUrl: String): InsertStatus = {
    val id = getNextId
    finalizeSuccess(longUrl, id, true)
  }

  def getNextId(): Long = {
    maxId += 1
    if (preferedList.contains(maxId)) getNextId
    else maxId
  }

  def finalizeSuccess(longUrl: String, shortUrlId: Long, random : Boolean): InsertStatus = {
    println (s"Request Success ${longUrl} -> ${Base62.encode(shortUrlId)} ${random}")
    InsertStatus(true, Base62.encode(shortUrlId))
  }
  
  def scheduleSnapshotMessage =
    context.system.scheduler.scheduleOnce(1.minute, self, "Snapshot")
  

}