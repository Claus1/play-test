package model

import org.joda.time.DateTime

case class User(token: String)

case class Folder(id: Long, title: String)

case class Link(url: String, code: String)

case class Click(date: DateTime, remoteIp: String)

case class CodeInfo(url: String, idFolder: Long, var clicks: List[Click])

object Config{
  val defaultLimit = 100
}

