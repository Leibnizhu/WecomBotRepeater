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

case class SentryRequest(@JsonProperty("project_name") projectName: String,
                         @JsonProperty message: String,
                         @JsonProperty id: String,
                         @JsonProperty culprit: String,
                         @JsonProperty("project_slug") projectSlug: String,
                         @JsonProperty url: String,
                         @JsonProperty level: String,
                         @JsonProperty("triggering_rules") triggeringRules: List[Any],
                         @JsonProperty event: Event,
                         @JsonProperty project: String,
                         @JsonProperty logger: Object,
                        ) extends RequestEntity {
  override def toWecomBotTextRequest(token: String, msgType: MessageType, mentionedList: List[String]): WecomBotRequest = {
    //TODO
    val textContent = "TODO"
    new WecomBotRequest(TextMessage(token, textContent, mentionedList))
  }

  override def toWecomBotMarkdownRequest(token: String, msgType: MessageType, mentionedList: List[String]): WecomBotRequest = {
    //TODO
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

case class Event(@JsonProperty stacktrace: EventStackTrace,
                 @JsonProperty("use_rust_normalize") useRustNormalize: Boolean,
                 @JsonProperty extra: EventExtra,
                 @JsonProperty modules: Map[String, String],
                 @JsonProperty("_ref_version") refVersion: Int,
                 @JsonProperty("_ref") ref: Int,
                 @JsonProperty culprit: String,
                 @JsonProperty title: String,
                 @JsonProperty("event_id") eventId: String,
                 @JsonProperty platform: String,
                 @JsonProperty version: String,
                 @JsonProperty location: Object,
                 @JsonProperty template: EventTemplate,
                 @JsonProperty logger: String,
                 @JsonProperty `type`: String,
                 @JsonProperty metadata: Map[String, String],
                 @JsonProperty tags: List[List[String]],
                 @JsonProperty timestamp: Double,
                 @JsonProperty user: EventUser,
                 @JsonProperty fingerprint: List[String],
                 @JsonProperty hashes: List[String],
                 @JsonProperty received: Double,
                 @JsonProperty level: String,
                 @JsonProperty contexts: EventContext,
                 @JsonProperty request: EventRequest,
                 @JsonProperty logentry: Map[String, String]
                )

case class EventStackTrace(@JsonProperty frames: List[EventStackTraceFrame])

case class EventStackTraceFrame(@JsonProperty function: String,
                                @JsonProperty("abs_path") absPath: String,
                                @JsonProperty("pre_context") preContext: List[String],
                                @JsonProperty("post_context") postContext: List[String],
                                @JsonProperty vars: Map[String, Object],
                                @JsonProperty module: String,
                                @JsonProperty filename: String,
                                @JsonProperty lineno: Int,
                                @JsonProperty("in_app") inApp: Boolean,
                                @JsonProperty data: Map[String, Object],
                                @JsonProperty("context_line") contextLine: String)

case class EventExtra(@JsonProperty emptyList: List[Any],
                      @JsonProperty unauthorized: Boolean,
                      @JsonProperty emptyMap: Map[String, Any],
                      @JsonProperty url: String,
                      @JsonProperty results: List[String],
                      @JsonProperty length: Long,
                      @JsonProperty session: Map[String, String])

case class EventTemplate(@JsonProperty("abs_path") absPath: String,
                         @JsonProperty("pre_context") preContext: List[String],
                         @JsonProperty("post_context") postContext: List[String],
                         @JsonProperty filename: String,
                         @JsonProperty lineno: Int,
                         @JsonProperty("context_line") contextLine: String)

case class EventUser(@JsonProperty username: String,
                     @JsonProperty name: String,
                     @JsonProperty("ip_address") ipAddress: String,
                     @JsonProperty email: String,
                     @JsonProperty geo: Map[String, String],
                     @JsonProperty id: String)

case class EventContext(@JsonProperty os: EventContextEntity,
                        @JsonProperty browser: EventContextEntity)

case class EventContextEntity(@JsonProperty version: String,
                              @JsonProperty name: String)

case class EventRequest(@JsonProperty cookies: List[List[String]],
                        @JsonProperty url: String,
                        @JsonProperty headers: List[List[String]],
                        @JsonProperty env: Map[String, String],
                        @JsonProperty("query_string") queryString: List[List[String]],
                        @JsonProperty data: Map[String, String],
                        @JsonProperty method: String,
                        @JsonProperty("inferred_content_type") inferredContentType: String)