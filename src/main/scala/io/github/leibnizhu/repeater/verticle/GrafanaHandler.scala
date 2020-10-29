package io.github.leibnizhu.repeater.verticle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import io.github.leibnizhu.repeater.Constants.{REQ_PARAM_MENTIONED_LIST, REQ_PARAM_WECOM_BOT_TOKEN, REQ_PARAM_WECOM_BOT_TYPE}
import io.github.leibnizhu.repeater.util.ResponseUtil.{failResponse, successResponse}
import io.github.leibnizhu.repeater.wecom.MessageType
import io.vertx.core.Handler
import io.vertx.core.http.{HttpServerRequest, HttpServerResponse}
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

import scala.util.Try

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
              doSendWecomBot(startTime, token, request, grafanaRequest, response)
            } catch {
              case e: Exception =>
                response.end(failResponse(e.getMessage, System.currentTimeMillis() - startTime).toString)
            }
          } else {
            val costTime = System.currentTimeMillis() - startTime
            val cause = bodyAr.cause()
            log.error(s"解析Grafana请求体失败, 耗时${costTime}毫秒", cause)
            response.end(failResponse(cause, costTime).toString)
          }
        })
      }
    } catch {
      case e: Exception => response.end(failResponse(e.getMessage, System.currentTimeMillis() - startTime).toString)
    }
  }

  private def doSendWecomBot(startTime: Long, token: String, request: HttpServerRequest,
                             grafanaRequest: GrafanaRequest, response: HttpServerResponse): Unit = {
    val mentionedList = Option(request.getParam(REQ_PARAM_MENTIONED_LIST)).map(_.split(",").toList).orNull
    val msgType = Option(request.getParam(REQ_PARAM_WECOM_BOT_TYPE)).map(MessageType.withName).getOrElse(MessageType.Markdown)
    grafanaRequest.toWecomBotRequest(token, msgType, mentionedList).send(sendAr => {
      val costTime = System.currentTimeMillis() - startTime
      if (sendAr.succeeded()) {
        log.info(s"发送企业微信机器人请求成功, 耗时${costTime}毫秒,响应:{}", sendAr.result())
        val result = new JsonObject().put("message", s"发送企业微信机器人请求成功, 耗时${costTime}毫秒").put("wecomResponse", sendAr.result())
        response.end(successResponse(result, costTime).toString)
      } else {
        val cause = sendAr.cause()
        log.error(s"发送企业微信机器人请求失败, 耗时${costTime}毫秒", cause)
        response.end(failResponse(cause, costTime).toString)
      }
    })
  }
}
