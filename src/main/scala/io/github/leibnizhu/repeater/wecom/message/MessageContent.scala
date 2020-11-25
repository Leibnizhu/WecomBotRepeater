package io.github.leibnizhu.repeater.wecom.message

import io.github.leibnizhu.repeater.wecom.message.MessageContent.{JSON_PARAM_TOKEN, JSON_PARAM_TYPE}
import io.github.leibnizhu.repeater.wecom.message.MessageType.MessageType
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
 * @author Leibniz on 2020/10/28 2:21 PM
 */
trait MessageContent {
  def msgType(): MessageType

  def token(): String

  def wholeJson(): JsonObject = {
    val reqJson = new JsonObject()
    val msgTypeStr = MessageType.msgTypeName(msgType())
    reqJson.put("msgtype", msgTypeStr)
    reqJson.put(msgTypeStr, contentJsonObject())
    reqJson
  }

  def contentJsonObject(): JsonObject

  def serializeToJsonObject(): JsonObject = serializeToJsonObject(new JsonObject()
    .put(JSON_PARAM_TYPE, MessageType.msgTypeName(msgType()))
    .put(JSON_PARAM_TOKEN, token()))

  def serializeToJsonObject(json: JsonObject): JsonObject
}

object MessageContent {
  private val log = LoggerFactory.getLogger(getClass)
  val JSON_PARAM_TYPE = "type"
  val JSON_PARAM_TYPE_MARKDOWN = "markdown"
  val JSON_PARAM_TYPE_TEXT = "text"
  val JSON_PARAM_TOKEN = "token"
  val JSON_PARAM_CONTENT = "content"
  val JSON_PARAM_MENTION_LIST = "mentionMobileList"

  def deserializeFromJsonObject(json: JsonObject): MessageContent = {
    val token = json.getString(JSON_PARAM_TOKEN)
    val content = json.getString(JSON_PARAM_CONTENT)
    json.getString(JSON_PARAM_TYPE) match {
      case JSON_PARAM_TYPE_MARKDOWN =>
        MarkdownMessage(token, content)
      case JSON_PARAM_TYPE_TEXT =>
        if (json.getJsonArray(JSON_PARAM_MENTION_LIST) != null) {
          val mentionListBuf = new ListBuffer[String]
          json.getJsonArray(JSON_PARAM_MENTION_LIST).forEach(m => mentionListBuf += m.toString)
          TextMessage(token, content, mentionListBuf.toList)
        } else {
          TextMessage(token, content)
        }
      case _ => throw new IllegalArgumentException("暂不支持的消息:" + json)
    }
  }
}
