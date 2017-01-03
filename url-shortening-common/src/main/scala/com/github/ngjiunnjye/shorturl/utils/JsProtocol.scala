package com.github.ngjiunnjye.shorturl.utils

import spray.json.DefaultJsonProtocol

case class UrlShorteningRequest(longUrl: String, shortUrl: Option[String])
case class UrlShorteningCommand(longUrl: String, shortUrlId: Long, random: Boolean)
case class UrlShorteningRespond(status : Boolean , longUrl: String, shortUrl: Option[String])

object JsProtocol extends DefaultJsonProtocol {
  implicit val requestFormat = jsonFormat2(UrlShorteningRequest)
  implicit val commandFormat = jsonFormat3(UrlShorteningCommand)
  implicit val respondFormat = jsonFormat3(UrlShorteningRespond)
}