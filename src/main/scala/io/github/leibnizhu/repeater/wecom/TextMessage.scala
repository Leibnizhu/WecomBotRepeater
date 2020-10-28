package io.github.leibnizhu.repeater.wecom

import io.github.leibnizhu.repeater.wecom.MessageType.MessageType
import io.vertx.core.json.{JsonArray, JsonObject}
import collection.JavaConverters._

/**
 * @author Leibniz on 2020/10/28 2:48 PM
 */
case class TextMessage(apiToken: String, text: String, mentionedList: List[String] = List()) extends MessageContent {
  override def msgType(): MessageType = MessageType.Text

  override def toJsonObject(): JsonObject = {
    val json = new JsonObject().put("content", text)
    if (mentionedList != null && mentionedList.nonEmpty) {
      json.put("mentioned_mobile_list", mentionedList.asJava)
    }
    json
  }

  override def token(): String = apiToken
}
