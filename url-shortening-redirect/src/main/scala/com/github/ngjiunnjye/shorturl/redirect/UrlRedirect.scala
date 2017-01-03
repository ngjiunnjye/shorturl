package com.github.ngjiunnjye.shorturl.redirect;

import scala.io.StdIn

import com.github.ngjiunnjye.shorturl.redirect.actor.KafkaToDbWriteActor
import com.github.ngjiunnjye.shorturl.redirect.actor.UrlResolverActor
import com.github.ngjiunnjye.shorturl.redirect.api.ResolveUrlApi

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult.route2HandlerFlow
import akka.stream.ActorMaterializer
import com.github.ngjiunnjye.shorturl.utils.Config


object UrlRedirect extends ResolveUrlApi with Config {
  implicit val system = ActorSystem("redirect")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  lazy val urlResolverActor: ActorRef = system.actorOf(Props(new UrlResolverActor), name = "UrlDecoder")
  system.actorOf(Props(new KafkaToDbWriteActor), name = "CommandWriter")
  
  def main(args: Array[String]) {
    
    val route = urlRoute

    val bindingFuture = Http().bindAndHandle(route,httpInterface, httpPort)

    println(s"Server online at http://${httpInterface}:${httpPort}/\nPress RETURN to stop...")
    StdIn.readLine() 
    bindingFuture
      .flatMap(_.unbind()) 
      .onComplete(_ => println("Shutdown")) 
  }
}