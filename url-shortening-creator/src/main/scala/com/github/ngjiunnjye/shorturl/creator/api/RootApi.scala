package com.github.ngjiunnjye.shorturl.creator.api

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

trait RootApi {
  val root =
    path("") {
        get {
          complete("url/create")
        }
      } 
}
