package com.github.ngjiunnjye.shorturl.redirect.actor

import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.JavaConversions.seqAsJavaList
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import org.apache.kafka.common.TopicPartition

import com.github.ngjiunnjye.kafka.{ Consumer => KafkaConsumer }
import com.github.ngjiunnjye.shorturl.utils.Config
import com.github.ngjiunnjye.shorturl.utils.JsProtocol.commandFormat
import com.github.ngjiunnjye.shorturl.utils.UrlShorteningCommand

import akka.actor.Actor
import spray.json.JsonParser
import spray.json.ParserInput.apply
import com.github.ngjiunnjye.shorturl.redirect.db.CreateShortUrlTable


class KafkaToDbWriteActor  extends Actor with Config with CreateShortUrlTable{
  val jdbcConn = {
    Class.forName("org.h2.Driver");
    DriverManager.getConnection(s"jdbc:h2:tcp://${h2ServerUrls.get(nodeId)}", "", "");
  }
    
  val insertPS: PreparedStatement = {
    Try {
      createShortUrlTableIfNotExists (jdbcConn)
        
      jdbcConn.prepareStatement("insert into short_Url (id, long_url, random) values (?,?, ?)")
      
    } match {
      case Success(ps) => ps
      case Failure(e) => throw e
    }     
  }

  val consumer = KafkaConsumer.createStringStringConsumer(s"redirect")
  val topic: String = "url.shortening.command";
  val partition: TopicPartition = new TopicPartition(topic, nodeId);
  consumer.assign(List(partition));
  final val POLL_TIMOUT_MS = 1000
  println("KafkaToDbWriteActor started ")

  def receive = {
    case "FetchKafkaMessage" =>
      val fetchSize = fetch
      if (fetchSize > 0) consumer.commitSync
      
  }

  override def preStart() = {
    scheduleFetchMessage
  }

  override def postStop(): Unit = consumer.close

  def fetch = {
    val records = consumer.poll(POLL_TIMOUT_MS)
    records.foreach { record =>
      val kafkaMsg = record.value
      //TODO commit after write val offset = record.offset()
      JsonParser(kafkaMsg).convertTo[UrlShorteningCommand] match {
        case cmd: UrlShorteningCommand =>
          insert (cmd)
        case _ => println (s"????") 
      }
    }
    scheduleFetchMessage
    records.size    
  }

  def scheduleFetchMessage =
    context.system.scheduler.scheduleOnce(1.second, self, "FetchKafkaMessage")

  def close: Unit = {
    consumer.close()
  }
  
  def insert (cmd : UrlShorteningCommand) = {
      Try {        
        insertPS.setLong(1, cmd.shortUrlId)
        insertPS.setString(2, cmd.longUrl)
        insertPS.setBoolean(3, cmd.random)
        insertPS.executeUpdate()
      } match{
        case Success(s) =>
          println(s"ShortUrl Entry Created ID: id:${cmd.shortUrlId} longUrl:${cmd.longUrl} random:${cmd.random}")

        case Failure(e) =>
          println(s"ShortUrl Entry Fail ID: id:${cmd.shortUrlId} longUrl:${cmd.longUrl} random:${cmd.random} ${e.getMessage}")
          
      }
  }

}