package io.github.leibnizhu.repeater.verticle

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.leibnizhu.repeater.wecom.MarkdownMessage.MarkdownBuilder
import io.github.leibnizhu.repeater.wecom.MessageType.{Markdown, MessageType, Text}
import io.github.leibnizhu.repeater.wecom.{MarkdownMessage, TextMessage, WecomBotRequest}
import io.vertx.core.json.JsonObject

/**
 * @author Leibniz on 2020/10/28 1:16 PM
 */
case class EvalMatch(@JsonProperty("value") value: Int,
                     @JsonProperty("metric") metric: String,
                     @JsonProperty("tags") tags: JsonObject)

case class GrafanaRequest(@JsonProperty("dashboardId") dashboardId: Int,
                          @JsonProperty("evalMatches") evalMatches: List[EvalMatch],
                          @JsonProperty("imageUrl") imageUrl: String,
                          @JsonProperty("message") message: String,
                          @JsonProperty("orgId") orgId: Int,
                          @JsonProperty("panelId") panelId: Int,
                          @JsonProperty("ruleId") ruleId: Int,
                          @JsonProperty("ruleName") ruleName: String,
                          @JsonProperty("ruleUrl") ruleUrl: String,
                          @JsonProperty("state") state: String,
                          @JsonProperty("tags") tags: JsonObject,
                          @JsonProperty("title") title: String) {
  def toWecomBotRequest(token: String, msgType: MessageType, mentionedList: List[String] = null): WecomBotRequest = {
    msgType match {
      case Text =>
        val textContent = s"标题:${title},触发规则:${ruleName},信息:${message}"
        new WecomBotRequest(TextMessage(token, textContent, mentionedList))
      case Markdown =>
        val keyColor = if (title.startsWith("[OK]")) "info" else if (title.startsWith("[Alerting]")) "warning" else "comment"
        val markdownContent = new MarkdownBuilder()
          .text("接收到Grafana通知,具体信息:").newLine()
          .quoted().text("触发规则:").text(ruleName).newLine()
          .quoted().text("标题:").colored(keyColor, title).newLine()
          .quoted().text("信息:").colored(keyColor, message).newLine()
          .quoted().text("链接:").hrefLink(ruleUrl, ruleUrl).newLine()
          .mentionUsers(mentionedList)
          .toMarkdownString
        new WecomBotRequest(MarkdownMessage(token, markdownContent))
    }
  }
}
