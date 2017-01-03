//package com.github.ngjiunnjye.api
//
//import org.scalatest.{ Matchers, WordSpec }
//import akka.actor.ActorSystem
//import akka.actor.ActorRef
//import akka.actor.Props
//import com.github.ngjiunnjye.actor.CreateRequestActor
//import java.sql.DriverManager
//import scala.util.Try
//import scala.util.Failure
//import scala.util.Success
//import java.sql.SQLException
//
//class H2JdbcSpec extends WordSpec with Matchers {
//  //implicit val system = ActorSystem("my-system")
//
//  "Capture Exception for primary or unique constraint" in {
//    Try {
//  val jdbcConn = {
//    Class.forName("org.h2.Driver");
//    DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "", "");
//  }
//
//  val insertPS = {
//    jdbcConn.prepareStatement("insert into short_Url (id, long_url, short_url, random) values (?,?, ?, ?)")
//  }
//      
//      insertPS.setLong(1, 1)
//      insertPS.setString(2, "source")
//      insertPS.setString(3, "target")
//      insertPS.setBoolean(4, true)
//      insertPS.executeUpdate()
//
//      insertPS.setLong(1, 1)
//      insertPS.setString(2, "source")
//      insertPS.setString(3, "target")
//      insertPS.setBoolean(4, true)
//      insertPS.executeUpdate()
//    } match {
//      case Success(s) => {
//        println("Yeah")
//      }
//      case Failure(e) => {
//        e match {
//          case jse : SQLException => 
//            val state = jse.getSQLState 
//            state should be ("23505")
//          case x => x should not be x
//                       
//        }
//      }
//    }
//  }
//}