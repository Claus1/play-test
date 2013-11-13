package controllers

import play.api.mvc._
import model._
import play.api.libs.json._

object js_formats{

  implicit val clickFormat = Json.format[Click]

  implicit val userFormat = Json.format[User]

  implicit val folderFormat = Json.format[Folder]

  implicit val linkFormat = Json.format[Link]
}


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def OkString(str: String) = Action {Ok(JsString(str))}

  val badCode = BadRequest(Json.obj("reason" -> "Invalid link code!"))

  def okJson(seq: Seq[JsValue]) = Action{Ok(Json.toJson(seq))}

  import Database.{instance => db}

  import js_formats._

  def token(userId: String, hardSecret: String) = OkString(db.user(userId).token)

  def link(token: String, url: String, code: String , folder_id: Long) =
    OkString(db.addLink(token, url, code, folder_id))

  def postLink(code: String, referer: String, remoteIp: String) = Action{
    db.link4code(code, referer, remoteIp).map(s=>Ok(JsString(s))).getOrElse(badCode) }

  def getLink(code: String, token: String) = Action{ db.codeInfo(code).map(info =>
      Ok(Json.obj("link" -> info.url,"folder_id"-> info.idFolder, "count_clicks" -> info.clicks.length))
  ).getOrElse(badCode)}

  def folderLinks(idFolder: Long, offset: Long, limit: Long) = okJson(db.folderLinks(idFolder, offset, limit).map(Json.toJson(_)))

  def userLinks(token: String, offset: Long, limit: Long) = okJson(db.userLinks(token, offset, limit).map(Json.toJson(_)))

  def folders(token: String) = okJson(db.userFolders(token).map(Json.toJson(_)))

  def linkClicks(token: String, offset: Long, limit: Long) = okJson(db.linkClicks(token, offset, limit).map(Json.toJson(_)))

}