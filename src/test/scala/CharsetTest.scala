// {"jsonrpc": "2.0", "method": "ping", "params": "payload"}

import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json.{JsArray, JsObject, JsString, JsValue, given}

import java.nio.charset.Charset

class CharsetTest extends munit.FunSuite:
  test("charset for ibm-037"):
    val cs = Charset.forName("IBM-037")
    assertNotEquals(cs, null)
    assertEquals(cs.name(), "IBM037")
  test("charset for ibm037"):
    val cs = Charset.forName("IBM037")
    assertNotEquals(cs, null)
    assertEquals(cs.name(), "IBM037")
  test("charset for ibm1047"):
    val cs = Charset.forName("IBM1047")
    assertNotEquals(cs, null)
    assertEquals(cs.name(), "IBM1047")
  test("charset for ibm-1047"):
    val cs = Charset.forName("IBM-1047")
    assertNotEquals(cs, null)
    assertEquals(cs.name(), "IBM1047")
  test("charset for utf-8"):
    val cs = Charset.forName("utf-8")
    assertNotEquals(cs, null)
    assertEquals(cs.name(), "UTF-8")
