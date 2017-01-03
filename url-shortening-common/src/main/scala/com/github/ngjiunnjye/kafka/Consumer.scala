package com.github.ngjiunnjye.kafka

import java.util.Properties
import org.apache.kafka.clients.consumer.KafkaConsumer
import scala.collection.JavaConversions._

object Consumer {
  val props: Properties = new Properties();
  props.put("bootstrap.servers", "localhost:9092");
  props.put("enable.auto.commit", "false");
  props.put("auto.offset.reset", "earliest");
  props.put("auto.commit.interval.ms", "1000");
  props.put("session.timeout.ms", "30000");

  def createStringStringConsumer (group: String) = {
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("group.id", group);
    new KafkaConsumer[String, String](props);
  }

  def createLongStringConsumer (group: String) = {
    props.put("key.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("group.id", group);
    new KafkaConsumer[Long, String](props);
  }  
  
}