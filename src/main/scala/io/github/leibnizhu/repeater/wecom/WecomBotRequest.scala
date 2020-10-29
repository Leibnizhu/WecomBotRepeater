package io.github.leibnizhu.repeater.wecom

import io.github.leibnizhu.repeater.Constants
import io.github.leibnizhu.repeater.verticle.GrafanaHandler.getClass
import io.github.leibnizhu.repeater.wecom.WecomBotRequest.doSendReq
import io.vertx.core.json.JsonObject
import io.vertx.core.{AsyncResult, Handler}
import io.vertx.ext.web.client.{WebClient, WebClientOptions}
import org.slf4j.LoggerFactory

/**
 * @author Leibniz on 2020/10/28 1:38 PM
 */
case class WecomBotRequest(msgContent: MessageContent) {
  private val log = LoggerFactory.getLogger(getClass)

  def send(handler: Handler[AsyncResult[String]]): Unit = {
    val reqJson = msgContent.wholeJson()
    if (log.isDebugEnabled) {
      log.debug("构造企业微信机器人请求:{}", reqJson)
    }
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
