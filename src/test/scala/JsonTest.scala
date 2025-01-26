// {"jsonrpc": "2.0", "method": "ping", "params": "payload"}

import spray.json.{JsArray, JsObject, JsString, JsValue, given}

class JsonTest extends munit.FunSuite:
  test("decode jsonrpc with by method and params"):
    val message = """{"jsonrpc": "2.0", "method": "ping", "params": "payload"}"""
    val seq = message.parseJson.asJsObject.getFields("method", "params")
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
      case Seq(JsString("ping"), JsString(params)) => assertEquals(params, "payload")
      case _ => fail("Not parsed")

  test("decode  {\"jsonrpc\": \"2.0\", \"method\": \"requestSnapshot\", \"params\": {connectionString: \"poc agent\", user: \"db2inst1\", password: \"password\"}}"):
    val message = """ {"jsonrpc": "2.0", "method": "requestSnapshot", "params": {"connectionString": "poc agent", "user": "db2inst1", "password": "password"}}"""
    val seq = message.parseJson.asJsObject.getFields("method", "params")
    assertEquals(seq, Seq(JsString("requestSnapshot"), JsObject(Map(
      "connectionString" -> JsString("poc agent"),
      "password" -> JsString("password"),
      "user" -> JsString("db2inst1"),
    ))))

  test("match  {\"jsonrpc\": \"2.0\", \"method\": \"requestSnapshot\", \"params\": {connectionString: \"poc agent\", user: \"db2inst1\", password: \"password\"}}"):
    val message = """ {"jsonrpc": "2.0", "method": "requestSnapshot", "params": {"connectionString": "poc agent", "user": "db2inst1", "password": "password"}}"""
    val seq = message.parseJson.asJsObject.getFields("method", "params")
    seq match
      case Seq(JsString("requestSnapshot"), JsObject(params)) =>
        println(params)
        val seq2 = params.toSeq
        println(seq2)
        seq2 match
          case Seq(("connectionString", JsString(cs)),("password", JsString(password)),("user", JsString(user))) =>
            assert(true)
            assertEquals(cs, "poc agent")
            assertEquals(password, "password")
            assertEquals(user, "db2inst1")
          case _ => fail(s"params not parsed $seq2")
      /*
      params match
        case Map() => assert(true)
        case _ => fail("Not parsed")
       */
      case _ => fail("Not parsed")
