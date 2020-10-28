package io.github.leibnizhu.repeater.wecom

import io.github.leibnizhu.repeater.Constants
import io.github.leibnizhu.repeater.wecom.WecomBotRequest.doSendReq
import io.vertx.core.json.JsonObject
import io.vertx.core.{AsyncResult, Handler}
import io.vertx.ext.web.client.{WebClient, WebClientOptions}

/**
 * @author Leibniz on 2020/10/28 1:38 PM
 */
case class WecomBotRequest(msgContent: MessageContent) {
  def send(handler: Handler[AsyncResult[String]]): Unit = {
    val reqJson = new JsonObject()
    val msgType = MessageType.msgTypeName(msgContent.msgType())
    reqJson.put("msgtype", msgType)
    reqJson.put(msgType, msgContent.toJsonObject())
    doSendReq(reqJson, msgContent.token(), handler)
  }
}

object WecomBotRequest {
  private val client = WebClient.create(Constants.vertx, new WebClientOptions().setKeepAlive(true))

  def doSendReq(req: JsonObject, token: String, handler: Handler[AsyncResult[String]]): Unit = {
    client
      .postAbs(Constants.WECOM_BOT_API_URL + token)
      .sendJsonObject(req, sendAr =>
        handler.handle(sendAr.map(_.bodyAsString))
      )
  }
}
