package com.github.ngjiunnjye.cryptor

object Base62 {
  private val base62Dict = (('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z') ).toSeq
  private val base62Size = base62Dict.size
  
  def encode(num: Long): String = {
    def loop(buf: String, left: Long): String = {
      if (left < 62L) base62Dict((left % base62Size).toInt) + buf
      else {
        loop( base62Dict((left % base62Size).toInt) + buf, (left / base62Size))
      }
    }
    loop("", num)
  }

  def decode(encoded: String) : Long = {
    val str = encoded
    def loop (sum: Long, str : String): Long = {
      if (str.length()==1) sum + base62Dict.indexOf(str.head)
      else {
        val map = base62Dict.indexOf(str.head)
        if (map == -1) throw new Exception (s"Unsupported Character ${str.head} for base62 mapper")
        loop (sum + scala.math.pow (base62Size,str.tail.size).toLong *  base62Dict.indexOf(str.head), str.tail)
      }
    }
    loop (0,str)
  }

}