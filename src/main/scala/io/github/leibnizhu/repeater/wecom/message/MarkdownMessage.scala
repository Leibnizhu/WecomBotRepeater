package io.github.leibnizhu.repeater.wecom.message

import io.github.leibnizhu.repeater.wecom.message.MessageContent.{JSON_PARAM_CONTENT, JSON_PARAM_TOKEN, JSON_PARAM_TYPE, JSON_PARAM_TYPE_MARKDOWN}
import io.github.leibnizhu.repeater.wecom.message.MessageType.MessageType
import io.vertx.core.json.JsonObject

/**
 * @author Leibniz on 2020/10/28 3:17 PM
 */
case class MarkdownMessage(apiToken: String, content: String) extends MessageContent {
  override def msgType(): MessageType = MessageType.Markdown

  override def contentJsonObject(): JsonObject = new JsonObject().put("content", content)

  override def token(): String = apiToken

  override def toJsonObject(): JsonObject = new JsonObject()
    .put(JSON_PARAM_TYPE, JSON_PARAM_TYPE_MARKDOWN)
    .put(JSON_PARAM_TOKEN, apiToken)
    .put(JSON_PARAM_CONTENT, content)

}

object MarkdownMessage {

  class MarkdownBuilder {
    private val sb = new StringBuilder

    def text(text: String): MarkdownBuilder = {
      sb.append(text)
      this
    }

    def newLine(): MarkdownBuilder = {
      sb.append("\n")
      this
    }

    def quoted(): MarkdownBuilder = {
      sb.append("> ")
      this
    }

    def colored(color: String, text: String): MarkdownBuilder = {
      sb.append("<font color=\"").append(color).append("\">").append(text).append("</font>")
      this
    }

    def hrefLink(text: String, url: String): MarkdownBuilder = {
      sb.append("[").append(text).append("](").append(url).append(")")
      this
    }

    def mentionUsers(users: List[String]): MarkdownBuilder = {
      if(users != null && users.nonEmpty) {
        users.foreach(sb.append("<@").append(_).append(">"))
      }
      this
    }

    def toMarkdownString: String = sb.toString()
  }

}