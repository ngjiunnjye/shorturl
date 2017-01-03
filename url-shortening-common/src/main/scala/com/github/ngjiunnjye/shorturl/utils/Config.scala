package com.github.ngjiunnjye.shorturl.utils

import com.typesafe.config.ConfigFactory

trait Config {
  private val config = ConfigFactory.load("shorturl")
  private val httpConfig = config.getConfig("http")

  val httpInterface = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")
  
  private val dbConfig = config.getConfig("db")
  val h2ServerUrls = dbConfig.getList("h2ServerUrls").unwrapped()

  private val clusterConfig = config.getConfig("cluster")
  val nodeId = clusterConfig.getInt("id")
  val readerNodeAddresses = clusterConfig.getList("readerNodeAddressess").unwrapped()
  
}