package io.github.leibnizhu.repeater.wecom

import io.github.leibnizhu.repeater.wecom.MessageType.MessageType
import io.vertx.core.json.{JsonArray, JsonObject}

import scala.collection.JavaConverters._

/**
 * @author Leibniz on 2020/10/28 3:17 PM
 */
case class MarkdownMessage(apiToken: String, content: String) extends MessageContent {
  override def msgType(): MessageType = MessageType.Markdown

  override def toJsonObject(): JsonObject = new JsonObject().put("content", content)

  override def token(): String = apiToken
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

    def colorStart(color: String): MarkdownBuilder = {
      sb.append("<font color=\"").append(color).append("\">")
      this
    }

    def colorEnd(): MarkdownBuilder = {
      sb.append("</font>")
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