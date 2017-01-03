package com.github.ngjiunnjye.kafka

import java.util.Properties
import org.apache.kafka.clients.producer.KafkaProducer

object Producer {
 val   props  : Properties = new Properties();
 props.put("bootstrap.servers", "localhost:9092");
 props.put("acks", "all");
 props.put("retries", "0");
 props.put("batch.size", "16384");
 props.put("linger.ms", "1");
 props.put("buffer.memory", "33554432");
 

 def createStringStringProducer = {
   props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
   props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
   new KafkaProducer[String, String](props);
 }
 
 def createLongStringProducer = {
   props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
   props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
   new KafkaProducer[Long, String](props);
 }
 
}