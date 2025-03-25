package qmf.poc.agent.catalog.models

import spray.json.{JsNumber, JsObject, JsString, JsValue, JsonFormat}

import java.util.Date

class ObjectDirectory(
    val owner: String,
    val name: String,
    val `type`: String,
    val subType: String,
    val objectLevel: Int,
    val restricted: String,
    val model: String,
    val created: String,
    val modified: String,
    val lastUsed: String
)
object ObjectDirectory:
  given JsonFormat[ObjectDirectory] with
    def write(o: ObjectDirectory): JsObject =
      JsObject(
        "name" -> JsString(o.name),
        "owner" -> JsString(o.owner),
        "type" -> JsString(o.`type`),
        "objectLevel" -> JsNumber(o.objectLevel),
        "subType" -> JsString(o.subType),
        "model" -> JsString(o.model),
        "created" -> JsString(o.created),
        "lastUser" -> JsString(o.lastUsed),
        "modified" -> JsString(o.modified),
        "restricted" -> JsString(o.restricted)
      )

    def read(value: JsValue): ObjectDirectory = ???
