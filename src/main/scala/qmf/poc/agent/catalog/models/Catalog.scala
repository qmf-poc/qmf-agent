package qmf.poc.agent.catalog.models

import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsObject, JsString, JsValue, JsonFormat, RootJsonFormat, given}
import spray.json.enrichAny

import scala.collection.mutable.ArrayBuffer

class Catalog(val objectData: Seq[ObjectData],
              val objectRemarks: Seq[ObjectRemarks],
              val objectDirectories: Seq[ObjectDirectory])

object CatalogJsonFormat extends DefaultJsonProtocol {
  given JsonFormat[ObjectData] with {
    def write(o: ObjectData): JsObject =
      JsObject(
        "name" -> JsString(o.name),
        "owner" -> JsString(o.owner),
        "seq" -> JsNumber(o.seq),
      )

    def read(value: JsValue): ObjectData = ???
  }
  given JsonFormat[ObjectRemarks] with {
    def write(o: ObjectRemarks): JsObject =
      JsObject(
        "name" -> JsString(o.name),
        "owner" -> JsString(o.owner),
        "type" -> JsString(o.`type`),
        "remarks" -> JsString(o.remarks),
      )

    def read(value: JsValue): ObjectRemarks = ???
  }
  given JsonFormat[ObjectDirectory] with {
    def write(o: ObjectDirectory): JsObject =
      JsObject(
        "name" -> JsString(o.name),
        "owner" -> JsString(o.owner),
        "type" -> JsString(o.`type`),
        "objectLevel" -> JsNumber(o.objectLevel),
        "subType" -> JsString(o.subType),
        "model" -> JsString(o.model),
        "model" -> JsString(o.created),
        "lastUsed" -> JsString(o.lastUsed),
        "modified" -> JsString(o.modified),
        "restricted" -> JsString(o.restricted),
      )

    def read(value: JsValue): ObjectDirectory = ???
  }
  given JsonFormat[Catalog] with {
    def write(o: Catalog): JsObject =
      JsObject(
        // "name" -> JsArray(o.objectData),
        "objectData" -> JsArray(o.objectData.map(_.toJson).toVector),
        "objectRemarks" -> JsArray(o.objectRemarks.map(_.toJson).toVector),
        "objectDirectories" -> JsArray(o.objectDirectories.map(_.toJson).toVector),
        //"owner" -> JsString(o.objectRemarks),
        // "seq" -> JsNumber(o.objectDirectory),
      )

    def read(value: JsValue): Catalog = ???
  }
}