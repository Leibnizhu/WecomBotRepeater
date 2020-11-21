package io.github.leibnizhu.repeater.verticle

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectReader
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
  override protected val objectReader: ObjectReader = mapper.readerFor(classOf[SentryRequest])

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
    val textContent = s"接收到${level}级别的Sentry通知,异常信息:$message,根本原因:$culprit,请求路径:${event.request.url}.\n详细信息参见:$url."
    new WecomBotRequest(TextMessage(token, textContent, mentionedList))
  }

  override def toWecomBotMarkdownRequest(token: String, msgType: MessageType, mentionedList: List[String]): WecomBotRequest = {
    val keyColor = if (level.equalsIgnoreCase("error")) "warning" else "comment"
    val markdownContent = new MarkdownBuilder()
      .text("接收到").colored(keyColor, level).text("级别的").hrefLink("Sentry通知", url).text("具体信息:").newLine()
      .quoted().text("异常信息:").colored(keyColor, message).newLine()
      .quoted().text("根本原因:").text(culprit).newLine()
      .quoted().text("请求路径:").text(event.request.url).newLine()
      //      .quoted().hrefLink("详细信息链接", url).newLine()
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
