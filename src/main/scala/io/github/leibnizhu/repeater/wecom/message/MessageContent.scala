package io.github.leibnizhu.repeater.wecom.message

import io.github.leibnizhu.repeater.wecom.message.MessageType.MessageType
import io.vertx.core.json.JsonObject

/**
 * @author Leibniz on 2020/10/28 2:21 PM
 */
trait MessageContent {
  def msgType(): MessageType
  def contentJsonObject(): JsonObject
  def token(): String
  def wholeJson(): JsonObject = {
    val reqJson = new JsonObject()
    val msgTypeStr = MessageType.msgTypeName(msgType())
    reqJson.put("msgtype", msgTypeStr)
    reqJson.put(msgTypeStr, contentJsonObject())
    reqJson
  }
}
