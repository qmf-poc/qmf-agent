package qmf.poc.agent.catalog.models

import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsObject, JsString, JsValue, JsonFormat, enrichAny, given}

class Catalog(val objectData: Seq[ObjectData], val objectRemarks: Seq[ObjectRemarks], val objectDirectories: Seq[ObjectDirectory])

object CatalogJsonFormat extends DefaultJsonProtocol {
  import java.util.Base64

  given JsonFormat[ObjectData] with {
    def write(o: ObjectData): JsObject =
      JsObject(
        "name" -> JsString(o.name),
        "owner" -> JsString(o.owner),
        "seq" -> JsNumber(o.seq),
        "type" -> JsString(o.`type`),
        "appldata" -> JsString(Base64.getMimeEncoder(-1, Array.empty[Byte]).encodeToString(o.appldata))
      )

    def read(value: JsValue): ObjectData = ???
  }
  given JsonFormat[ObjectRemarks] with {
    def write(o: ObjectRemarks): JsObject =
      JsObject(
        "name" -> JsString(o.name),
        "owner" -> JsString(o.owner),
        "type" -> JsString(o.`type`),
        "remarks" -> JsString(o.remarks)
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
        "created" -> JsString(o.created),
        "lastUser" -> JsString(o.lastUsed),
        "modified" -> JsString(o.modified),
        "restricted" -> JsString(o.restricted)
      )

    def read(value: JsValue): ObjectDirectory = ???
  }
  given JsonFormat[Catalog] with {
    def write(o: Catalog): JsObject =
      JsObject(
        "objectData" -> JsArray(o.objectData.map(_.toJson).toVector),
        "objectRemarks" -> JsArray(o.objectRemarks.map(_.toJson).toVector),
        "objectDirectories" -> JsArray(o.objectDirectories.map(_.toJson).toVector)
      )

    def read(value: JsValue): Catalog = ???
  }
}
