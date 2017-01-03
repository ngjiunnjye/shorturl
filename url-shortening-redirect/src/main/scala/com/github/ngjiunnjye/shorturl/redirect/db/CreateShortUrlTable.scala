package com.github.ngjiunnjye.shorturl.redirect.db

import java.sql.Connection

trait CreateShortUrlTable {
  def createShortUrlTableIfNotExists(jdbcConn: Connection) {
    jdbcConn.createStatement().execute("""
      create table if not exists Short_Url (
      |id  bigint primary Key,
      |long_Url varchar,
      |random boolean )
      """.stripMargin)
  }

}