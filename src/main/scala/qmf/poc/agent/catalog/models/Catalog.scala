package qmf.poc.agent.catalog.models

import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsObject, JsString, JsValue, JsonFormat, enrichAny, given}

class Catalog(val objectData: Seq[ObjectData], val objectRemarks: Seq[ObjectRemarks], val objectDirectories: Seq[ObjectDirectory])

object Catalog:
  given JsonFormat[Catalog] with
    def write(o: Catalog): JsObject =
      JsObject(
        "objectData" -> JsArray(o.objectData.map(_.toJson).toVector),
        "objectRemarks" -> JsArray(o.objectRemarks.map(_.toJson).toVector),
        "objectDirectories" -> JsArray(o.objectDirectories.map(_.toJson).toVector)
      )

    //noinspection NotImplementedCode
    def read(value: JsValue): Catalog = ???
