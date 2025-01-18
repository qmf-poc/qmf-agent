// {"jsonrpc": "2.0", "method": "ping", "params": "payload"}

import spray.json.{JsArray, JsObject, JsString, JsValue, given}

class JsonTest extends munit.FunSuite:
  test("decode jsonrpc with by method and params"):
    val message = """{"jsonrpc": "2.0", "method": "ping", "params": "payload"}"""
    val seq = message.parseJson.asJsObject.getFields("method", "params" )
    seq match
      case Seq(JsString(method), JsString(params)) =>
        assertEquals(method, "ping")
        assertEquals(params, "payload")
      case _ =>
        fail("Not parsed")

  test("decode jsonrpc with by method"):
    val message = """{"jsonrpc": "2.0", "method": "ping", "params": "payload"}"""
    //message.parseJson.asJsObject.getFields("method", "params") match
    val seq = message.parseJson.asJsObject.getFields("method", "params")
    seq match
      case Seq(JsString("ping"), JsString(params)) =>
        assertEquals(params, "payload")
      case _ =>
        fail("Not parsed")
