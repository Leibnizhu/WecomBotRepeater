package io.github.leibnizhu.repeater.verticle

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.{ObjectMapper, ObjectReader}
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import io.github.leibnizhu.repeater.wecom.WecomBotRequest
import io.github.leibnizhu.repeater.wecom.message.MarkdownMessage.MarkdownBuilder
import io.github.leibnizhu.repeater.wecom.message.MessageType.MessageType
import io.github.leibnizhu.repeater.wecom.message.{MarkdownMessage, TextMessage}
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.{Logger, LoggerFactory}

/**
 * @author Leibniz on 2020/10/28 11:40 AM
 */
object GrafanaHandler extends RequestBodyJsonHandler[GrafanaRequest] {
  override protected val requestName: String = "Grafana"
  override protected val log: Logger = LoggerFactory.getLogger(getClass)
  override protected val objectReader: ObjectReader = (new ObjectMapper() with ScalaObjectMapper).registerModule(DefaultScalaModule).readerFor(classOf[GrafanaRequest])

  def grafanaToBot: Handler[RoutingContext] = parseRequestBodyAndSendBot
}

case class EvalMatch(@JsonProperty value: Int,
                     @JsonProperty metric: String,
                     @JsonProperty tags: JsonObject)

case class GrafanaRequest(@JsonProperty dashboardId: Int,
                          @JsonProperty evalMatches: List[EvalMatch],
                          @JsonProperty imageUrl: String,
                          @JsonProperty message: String,
                          @JsonProperty orgId: Int,
                          @JsonProperty panelId: Int,
                          @JsonProperty ruleId: Int,
                          @JsonProperty ruleName: String,
                          @JsonProperty ruleUrl: String,
                          @JsonProperty state: String,
                          @JsonProperty tags: Map[String, Object],
                          @JsonProperty title: String
                         ) extends RequestEntity {
  override def toWecomBotTextRequest(token: String, msgType: MessageType, mentionedList: List[String]): WecomBotRequest = {
    val textContent = s"标题:$title,触发规则:$ruleName,信息:$message"
    new WecomBotRequest(TextMessage(token, textContent, mentionedList))
  }

  override def toWecomBotMarkdownRequest(token: String, msgType: MessageType, mentionedList: List[String]): WecomBotRequest = {
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
