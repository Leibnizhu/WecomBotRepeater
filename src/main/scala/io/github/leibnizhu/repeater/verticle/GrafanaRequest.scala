package io.github.leibnizhu.repeater.verticle

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.leibnizhu.repeater.wecom.MarkdownMessage.MarkdownBuilder
import io.github.leibnizhu.repeater.wecom.{ MarkdownMessage, TextMessage, WecomBotRequest}
import io.vertx.core.json.JsonObject

import scala.beans.BeanProperty

/**
 * @author Leibniz on 2020/10/28 1:16 PM
 */
case class EvalMatch(@JsonProperty("value") val value: Int,
                     @JsonProperty("metric") val metric: String,
                     @JsonProperty("tags") val tags: JsonObject)

case class GrafanaRequest(@JsonProperty("dashboardId") val dashboardId: Int,
                          @JsonProperty("evalMatches") val evalMatches: List[EvalMatch],
                          @JsonProperty("imageUrl") val imageUrl: String,
                          @JsonProperty("message") val message: String,
                          @JsonProperty("orgId") val orgId: Int,
                          @JsonProperty("panelId") val panelId: Int,
                          @JsonProperty("ruleId") val ruleId: Int,
                          @JsonProperty("ruleName") val ruleName: String,
                          @JsonProperty("ruleUrl") val ruleUrl: String,
                          @JsonProperty("state") val state: String,
                          @JsonProperty("tags") val tags: JsonObject,
                          @JsonProperty("title") val title: String) {
  def toWecomBotRequest(token:String): WecomBotRequest = {
//    new WecomBotRequest(TextMessage(token, s"标题:${title},触发规则:${ruleName},信息:${message}"))
    new WecomBotRequest(MarkdownMessage(token, new MarkdownBuilder()
      .text("接收到Grafana通知,具体信息:").newLine()
      .quoted().text("标题:").colorStart("info").text(title).colorEnd().newLine()
      .quoted().text("触发规则:").colorStart("warning").text(ruleName).colorEnd().newLine()
      .quoted().text("信息:").colorStart("warning").text(message).colorEnd().newLine()
      .quoted().text("链接:").hrefLink(ruleUrl, ruleUrl).colorEnd()
      .toMarkdownString()))
  }
}
