package qmf.poc.agent.catalog

import qmf.poc.agent.catalog.models.{ObjectData, ObjectRemarks}
import qmf.poc.agent.catalog.models.CatalogJsonFormat.given
import spray.json.{JsArray, JsObject, given}

class CatalogTest extends munit.FunSuite:
  test("get connection pool"):
    val ts = System.currentTimeMillis()
    ConnectionPool("jdbc:db2://qmfdb2.s4y.solutions:50000/sample", "db2inst1", "password").test()
    println(System.currentTimeMillis() - ts)

  test("get connection pool 2"):
    val ts = System.currentTimeMillis()
    ConnectionPool("jdbc:db2://qmfdb2.s4y.solutions:50000/sample", "db2inst1", "password").test()
    println(System.currentTimeMillis() - ts)

  test("get connection pool cache"):
    val ts = System.currentTimeMillis()
    ConnectionPool.memo("jdbc:db2://qmfdb2.s4y.solutions:50000/sample", "db2inst1", "password").test()
    println(System.currentTimeMillis() - ts)
    val ts1 = System.currentTimeMillis()
    ConnectionPool.memo("jdbc:db2://qmfdb2.s4y.solutions:50000/sample", "db2inst1", "password").test()
    println(System.currentTimeMillis() - ts1)

  test("json catalog object data"):
    val o = ObjectData("owner", "name", "type", 1, Array[Byte]())
    val ast = o.toJson
    val json = ast.compactPrint
    println(json)
    
  test("json catalog object remarks"):
    val o = ObjectRemarks("owner", "name", "type", "remarks")
    val ast = o.toJson
    val json = ast.compactPrint
    println(json)

  test("get catalogs seq"):
    val o1 = ObjectData("owner", "name", "type", 1, Array[Byte]())
    val o2 = ObjectData("owner", "name", "type", 1, Array[Byte]())
    val ast = Seq(o1, o2).toJson
    val json = ast.compactPrint
    println(json)
    
  test("json catalogs"):
    val pool = ConnectionPool("jdbc:db2://qmfdb2.s4y.solutions:50000/sample", "db2inst1", "password")
    val ts = System.currentTimeMillis()
    val catalog = CatalogProvider(pool).catalog match
      case Some(catalog) =>
        println(System.currentTimeMillis() - ts)
        println(catalog.toJson)
        assert(true)
      case _ => fail("No connection")
    println(catalog.toJson)

  test("get catalog"):
    val pool = ConnectionPool("jdbc:db2://qmfdb2.s4y.solutions:50000/sample", "db2inst1", "password")
    val ts = System.currentTimeMillis()
    CatalogProvider(pool).catalog match
      case Some(catalog) =>
        println(System.currentTimeMillis() - ts)
        println(catalog.toJson)
        assert(true)
      case _ => fail("No connection")

