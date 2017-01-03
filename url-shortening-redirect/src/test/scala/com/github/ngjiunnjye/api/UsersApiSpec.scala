//package com.github.ngjiunnjye.api
//
//import akka.http.scaladsl.testkit.ScalatestRouteTest
//import org.scalatest.{Matchers, WordSpec}
//import akka.actor.ActorSystem
//import akka.actor.ActorRef
//import akka.actor.Props
//import com.github.ngjiunnjye.actor.CreateRequestActor
//
//class UrlApiSpec extends WordSpec with Matchers with ScalatestRouteTest with UrlApi {
//  //implicit val system = ActorSystem("my-system")
//  val createReqActor: ActorRef = system.actorOf(Props[CreateRequestActor], name = "createRequest")
//  
//  "return a greeting for GET requests to the root path" in {
//    Post("/url/create") ~> urlRoute ~> check {
//      val response = responseAs[String]
//
//      response should not be ""
//      response.length should be(64)
//    }
//  }
//}