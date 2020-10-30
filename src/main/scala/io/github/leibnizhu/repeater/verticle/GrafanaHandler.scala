package io.github.leibnizhu.repeater.verticle

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import io.github.leibnizhu.repeater.Constants.{REQ_PARAM_MENTIONED_LIST, REQ_PARAM_WECOM_BOT_TOKEN, REQ_PARAM_WECOM_BOT_TYPE}
import io.github.leibnizhu.repeater.util.ResponseUtil.{failResponse, successResponse}
import io.github.leibnizhu.repeater.wecom.message.MarkdownMessage.MarkdownBuilder
import io.github.leibnizhu.repeater.wecom.WecomBotRequest
import io.github.leibnizhu.repeater.wecom.message.{MarkdownMessage, MessageType, TextMessage}
import io.github.leibnizhu.repeater.wecom.message.MessageType.{Markdown, MessageType, Text}
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

/**
 * @author Leibniz on 2020/10/28 11:40 AM
 */
object GrafanaHandler {
  private val log = LoggerFactory.getLogger(getClass)
  private val objectReader = (new ObjectMapper() with ScalaObjectMapper).registerModule(DefaultScalaModule).readerFor(classOf[GrafanaRequest])

  def error: Handler[RoutingContext] = rc => {
    rc.response.putHeader("content-type", "application/json;charset=UTF-8")
      .end(failResponse("企业微信机器人token不能为空!", 0).toString)
  }

  def grafanaToBot: Handler[RoutingContext] = rc => {
    val startTime = System.currentTimeMillis()
    val (request, response) = (rc.request, rc.response.putHeader("content-type", "application/json;charset=UTF-8"))
    try {
      val token = request.getParam(REQ_PARAM_WECOM_BOT_TOKEN)
      if (token == null || token.trim.isEmpty) {
        response.end(failResponse("企业微信机器人token不能为空!", System.currentTimeMillis() - startTime).toString)
      } else {
        request.body(bodyAr => {
          if (bodyAr.succeeded()) {
            try {
              val grafanaRequest = objectReader.readValue[GrafanaRequest](bodyAr.result().toString())
              log.debug(s"接收到Grafana请求,token:$token,请求内容:$grafanaRequest")
              doSendWecomBot(startTime, token, rc, grafanaRequest)
            } catch {
              case e: Exception =>
                handlerException("解析Grafana请求Json", startTime, response, e)
            }
          } else {
            handlerException("解析Grafana请求体", startTime, response, bodyAr.cause())
          }
        })
      }
    } catch {
      case e: Exception =>
        handlerException("解析Grafana请求", startTime, response, e)
    }
  }

  private def doSendWecomBot(startTime: Long, token: String, rc: RoutingContext, grafanaRequest: GrafanaRequest): Unit = {
    val (request, response) = (rc.request, rc.response)
    val mentionedList = Option(request.getParam(REQ_PARAM_MENTIONED_LIST)).map(_.split(",").toList).orNull
    val msgType = Option(request.getParam(REQ_PARAM_WECOM_BOT_TYPE)).map(MessageType.withName).getOrElse(MessageType.Markdown)
    grafanaRequest.toWecomBotRequest(token, msgType, mentionedList).send(sendAr => {
      val costTime = System.currentTimeMillis() - startTime
      if (sendAr.succeeded()) {
        log.info(s"发送企业微信机器人请求成功, 耗时${costTime}毫秒,响应:{}", sendAr.result())
        val result = new JsonObject().put("message", s"发送企业微信机器人请求成功, 耗时${costTime}毫秒").put("wecomResponse", sendAr.result())
        response.end(successResponse(result, costTime).toString)
      } else {
        handlerException("发送企业微信机器人请求", startTime, response, sendAr.cause())
      }
    })
  }

  private def handlerException(errMsg: String, startTime: Long, response: HttpServerResponse, cause: Throwable) = {
    val costTime = System.currentTimeMillis() - startTime
    log.error(s"${errMsg}失败, 耗时${costTime}毫秒:" + cause.getMessage, cause)
    response.end(failResponse(cause, costTime).toString)
  }
}

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
