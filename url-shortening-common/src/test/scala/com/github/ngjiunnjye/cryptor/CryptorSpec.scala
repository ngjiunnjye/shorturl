package com.github.ngjiunnjye.cryptor

import org.scalatest.{Matchers, WordSpec}
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import scala.util.Try
import scala.util.Failure
import scala.util.Success


class CryptorSpec extends WordSpec with Matchers {
  
  "Test Encode Decode 1" in {
    val encode = Base62.encode(1)
    Base62.decode(encode) should be (1)    
  }
  
  "Test Decode Encode 1" in {
    val short = "1"
    val decode = Base62.decode(short)
    Base62.encode(decode) should be (short)    
  }  

  "Test Decode Encode google" in {
    val short = "google"
    val decode = Base62.decode(short)
    Base62.encode(decode) should be (short)    
  } 

  "Test Decode Encode Google" in {
    val short = "Google"
    val decode = Base62.decode(short)
    Base62.encode(decode) should be (short)    
  }  
  
  "Test Decode Encode Google is not google" in {
    val decode = Base62.decode("Google")
    Base62.encode(decode) should not be ("google")    
  }  

  "Test Decode Encode underscore" in {
    Try {
      Base62.decode("____")

    } match {
      case Success(s) =>
        s"decode ___" should not be (s)
      case Failure(e) =>
        e.getMessage should be("Unsupported Character _ for base62 mapper")
    }
  }  
}