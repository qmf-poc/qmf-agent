package qmf.poc.agent.catalog.models

import spray.json.{JsNumber, JsObject, JsString, JsValue, JsonFormat}

class ObjectData(val owner: String, val name: String, val `type`: String, val seq: Short, val appldata: String)

object ObjectData:
  given JsonFormat[ObjectData] with
    def write(o: ObjectData): JsObject =
      JsObject(
        "name" -> JsString(o.name),
        "owner" -> JsString(o.owner),
        "seq" -> JsNumber(o.seq),
        "type" -> JsString(o.`type`),
        "appldata" -> JsString(o.appldata)
      )

    //noinspection NotImplementedCode
    def read(value: JsValue): ObjectData = ???
