package com.github.ngjiunnjye.cryptor

object Base62 {
  private val base62Dict = (('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z') ).toSeq
  private val base62Size = base62Dict.size
  
  def encode(num: Long): String = {
    def loop(buf: String, left: Long): String = {
      if (left < 1) buf
      else {
        loop(buf + base62Dict((left % base62Size).toInt), left / base62Size)
      }
    }
    loop("", num)
  }

  def decode(encoded: String) : Long = {
    val strR = encoded.reverse
    def loop (num: Long, str : String): Long = {

      if (str.length()==0) num
      else {
        val map = base62Dict.indexOf(str.head)
        if (map == -1) throw new Exception (s"Unsupported Character ${str.head} for base62 mapper")
        loop (num * base62Size + map, str.tail)
      }
    }
    loop (0,strR)
  }

}