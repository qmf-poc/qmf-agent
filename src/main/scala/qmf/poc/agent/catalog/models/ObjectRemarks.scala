package qmf.poc.agent.catalog.models

import spray.json.{JsObject, JsString, JsValue, JsonFormat}

class ObjectRemarks(
    val owner: String,
    val name: String,
    val `type`: String,
    val remarks: String
)

object ObjectRemarks:
  given JsonFormat[ObjectRemarks] with
    def write(o: ObjectRemarks): JsObject =
      JsObject(
        "name" -> JsString(o.name),
        "owner" -> JsString(o.owner),
        "type" -> JsString(o.`type`),
        "remarks" -> JsString(o.remarks)
      )

    //noinspection NotImplementedCode
    def read(value: JsValue): ObjectRemarks = ???
