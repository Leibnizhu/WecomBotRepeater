package io.github.leibnizhu.repeater.verticle

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.{ObjectMapper, ObjectReader}
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import io.github.leibnizhu.repeater.wecom.WecomBotRequest
import io.github.leibnizhu.repeater.wecom.message.MarkdownMessage.MarkdownBuilder
import io.github.leibnizhu.repeater.wecom.message.MessageType.MessageType
import io.github.leibnizhu.repeater.wecom.message.{MarkdownMessage, TextMessage}
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.slf4j.{Logger, LoggerFactory}

/**
 * @author Leibniz on 2020/10/30 3:07 PM
 */
object SentryHandler extends RequestBodyJsonHandler[SentryRequest] {
  override protected val requestName: String = "Sentry"
  override protected val log: Logger = LoggerFactory.getLogger(getClass)
  override protected val objectReader: ObjectReader = (new ObjectMapper() with ScalaObjectMapper).registerModule(DefaultScalaModule).readerFor(classOf[SentryRequest])

  def sentryToBot: Handler[RoutingContext] = parseRequestBodyAndSendBot
}

//TODO
case class SentryRequest(@JsonProperty("project_name") projectName: String,
                         @JsonProperty message: String,
                         @JsonProperty id: String,
                         @JsonProperty culprit: String,
                         @JsonProperty("project_slug") projectSlug: String,
                         @JsonProperty url: String,
                         @JsonProperty level: String,
                         @JsonProperty("triggering_rules") triggeringRules: List[Any],
                         @JsonProperty event: Map[Any,Any],
                         @JsonProperty project: String,
                         @JsonProperty logger: Object,
                        ) extends RequestEntity {
  override def toWecomBotTextRequest(token: String, msgType: MessageType, mentionedList: List[String]): WecomBotRequest = {
    val textContent = "TODO"
    new WecomBotRequest(TextMessage(token, textContent, mentionedList))
  }

  override def toWecomBotMarkdownRequest(token: String, msgType: MessageType, mentionedList: List[String]): WecomBotRequest = {
    val keyColor = "TODO"
    val markdownContent = new MarkdownBuilder()
      .text("接收到Sentry通知,具体信息:").newLine()
      .quoted().text("触发规则:").text("ruleName").newLine()
      .quoted().text("标题:").colored(keyColor, "title").newLine()
      .quoted().text("信息:").colored(keyColor, message).newLine()
      .quoted().text("链接:").hrefLink("", "ruleUrl").newLine()
      .mentionUsers(mentionedList)
      .toMarkdownString
    new WecomBotRequest(MarkdownMessage(token, markdownContent))
  }
}