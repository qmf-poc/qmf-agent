// {"jsonrpc": "2.0", "method": "ping", "params": "payload"}

import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json.{JsArray, JsObject, JsString, JsValue, given}

import java.util.concurrent.LinkedBlockingQueue

class QueueTest extends munit.FunSuite:
  test("queue blocking"):
    val queue = LinkedBlockingQueue[String](1)
    val t1 = Thread(()=>{
      println("1a")
      queue.put("a")
      println("1b")
      queue.put("b")
      println("1c")
      queue.put("c")
      println("put all")
    })
    val t2 = Thread(()=>{
      println(queue.take())
      println(queue.take())
      println(queue.take())
    })
    t1.start()
    t2.start()
    t2.join()
