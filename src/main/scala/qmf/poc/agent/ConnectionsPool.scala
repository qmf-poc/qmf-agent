package qmf.poc.agent

import zio.jdbc.ZConnectionPool.make
import zio.jdbc.{ZConnectionPool, ZConnectionPoolConfig}
import zio.{Config, Task, UIO, ULayer, ZIO, ZLayer}

object ConnectionsPool:
  val createZIOPoolConfig: ULayer[ZConnectionPoolConfig] =
    ZLayer.succeed(ZConnectionPoolConfig.default)

  private val properties = Map[String, String](
    "user" -> "db2inst1",
    "password" -> "password"
  )

  def db2(
           host: String,
           port: Int,
           database: String,
           props: Map[String, String]
         ): ZLayer[ZConnectionPoolConfig, Throwable, ZConnectionPool] =
    ZLayer.scoped {
      for {
        _ <- ZIO.attempt(Class.forName("com.ibm.db2.jcc.DB2Driver"))
        acquire = ZIO.attemptBlocking {
          val properties = new java.util.Properties
          props.foreach { case (k, v) => properties.setProperty(k, v) }

          java.sql.DriverManager
            .getConnection(s"jdbc:db2//$host:$port/$database", properties)
        }
        zenv <- make(acquire).build
      } yield zenv.get[ZConnectionPool]
    }
