package qmf.poc.agent.broker.ws

import qmf.poc.agent.broker.BrokerDecoder
import qmf.poc.agent.broker.events.{ErrorResponse, PongResponse, Response}
import qmf.poc.agent.transport.ws.WebSocketMessage
import spray.json.JsonParser.ParsingException
import spray.json.{JsString, JsValue, given}

object WebSocketDecoder:
  private def optional2string(jv: Option[JsValue]): String =
    jv match
      case Some(v) => v.toString
      case None => ""

  given BrokerDecoder[WebSocketMessage] with
    def decode(webSocketMessage: WebSocketMessage): Response =
      try
        val seq = webSocketMessage.frame.parseJson.asJsObject.getFields("method", "params")
        ErrorResponse(webSocketMessage.frame, Exception("""Response does not have "method" and/or "params""""))
        seq match
          case Seq(JsString("ping"), JsString(params)) => PongResponse(params)
          case _ => ErrorResponse(webSocketMessage.frame, Exception("Unknown method and/or params type"))
      catch
        case e: ParsingException => ErrorResponse(webSocketMessage.frame, e)

      
