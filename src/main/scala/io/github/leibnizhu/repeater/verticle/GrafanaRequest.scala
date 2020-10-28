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
  def toWecomBotRequest(token: String, msgType: MessageType, mentionedMobileList: List[String] = List()): WecomBotRequest = {
    msgType match {
      case Text =>
        new WecomBotRequest(TextMessage(token, s"标题:${title},触发规则:${ruleName},信息:${message}", mentionedMobileList))
      case Markdown =>
        new WecomBotRequest(MarkdownMessage(token, new MarkdownBuilder()
          .text("接收到Grafana通知,具体信息:").newLine()
          .quoted().text("标题:").colorStart("info").text(title).colorEnd().newLine()
          .quoted().text("触发规则:").colorStart("warning").text(ruleName).colorEnd().newLine()
          .quoted().text("信息:").colorStart("warning").text(message).colorEnd().newLine()
          .quoted().text("链接:").hrefLink(ruleUrl, ruleUrl)
          .toMarkdownString, mentionedMobileList))
    }
  }
}
