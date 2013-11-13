package model

import org.joda.time.DateTime


trait AbstractDatabase{

  def user(userId: String): User

  def userFolders(userId: String): Seq[Folder]

  def folderLinks(idFolder: Long, offset: Long, limit: Long): Seq[Link]

  def userLinks(userToken: String, offset: Long, limit: Long): Seq[Link]

  def addLink(token: String, url: String, code: String, folder_id: Long) : String

  //calc and fix click
  def link4code(code: String, referer: String, remoteIp: String): Option[String]

  def codeInfo(code: String): Option[CodeInfo]

  def linkClicks(code: String, offset: Long, limit: Long): Seq[Click]
}


class MemoryDatabase extends AbstractDatabase{

  import scala.collection.mutable._

  import Generator.genNewId

  private val id2user = Map[String,User]()

  override def user(userId: String) = id2user.getOrElseUpdate(userId, User(genNewId))

  private val user2folders = Map[String, Seq[Long]]()

  override def userFolders(user: String) = user2folders.getOrElse(user, Seq.empty).map(i=> folder2links(i.toInt)._1)

  private val folder2links = ArrayBuffer[(Folder,Seq[Link])]()

  override def folderLinks(idFolder: Long, offset: Long, limit: Long): Seq[Link] = if (idFolder == -1) Seq.empty else
    folder2links(idFolder.toInt)._2.drop(offset.toInt).take(if (limit == 0) Config.defaultLimit else limit.toInt)

  private val user2links = Map[String,List[Link]]()

  override def userLinks(userToken: String, offset: Long, limit: Long) = user2links.getOrElse(userToken, Seq.empty).
    drop(offset.toInt).take(if (limit == 0) Config.defaultLimit else limit.toInt)

  private val code2other = Map[String,CodeInfo]()

  override def codeInfo(code: String) = code2other.get(code)

  override def addLink(token: String, url: String, code: String, folder_id: Long) = {

    val linkCode = if (code == "") genNewId else if (code2other.contains(code)) genNewId else code

    code2other += linkCode -> CodeInfo(url, folder_id, Nil)

    val link = Link(url, linkCode)

    user2links(token) = user2links.get(token).map(link :: _).getOrElse(List(link))

    linkCode
  }

  override def link4code(code: String, referer: String, remoteIp: String) =

    code2other.get(code).map(info => { info.clicks ::= Click(DateTime.now(), remoteIp); info.url})

  override def linkClicks(code: String, offset: Long, limit: Long) = code2other.get(code).map(_.clicks).getOrElse(Nil).
    drop(offset.toInt).take(if (limit == 0) Config.defaultLimit else limit.toInt)
}

object Database{
  val instance: AbstractDatabase = new MemoryDatabase
}


