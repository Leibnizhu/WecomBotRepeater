package io.github.leibnizhu.repeater.verticle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import io.github.leibnizhu.repeater.Constants.REQ_PARAM_WECOM_BOT_TOKEN
import io.github.leibnizhu.repeater.util.ResponseUtil
import io.github.leibnizhu.repeater.util.ResponseUtil.{failResponse, successResponse}
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.{AsyncResult, Future, Handler}
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
    val token = request.getParam(REQ_PARAM_WECOM_BOT_TOKEN)
    if (token == null || token.trim.isEmpty) {
      response.end(failResponse("企业微信机器人token不能为空!", System.currentTimeMillis() - startTime).toString)
    } else {
      request.body(bodyAr => {
        if (bodyAr.succeeded()) {
          val grafanaRequest = objectReader.readValue[GrafanaRequest](bodyAr.result().toString())
          log.debug(s"接收到Grafana请求,token:${token},请求内容:${grafanaRequest}")
          doSendWecomBot(startTime, token, grafanaRequest, response)
        } else {
          val costTime = System.currentTimeMillis() - startTime
          val cause = bodyAr.cause()
          log.error(s"解析Grafana请求体失败, 耗时${costTime}毫秒", cause)
          response.end(failResponse(cause, costTime).toString)
        }
      })
    }
  }

  private def doSendWecomBot(startTime: Long, token: String, grafanaRequest: GrafanaRequest, response: HttpServerResponse): Unit = {
    grafanaRequest.toWecomBotRequest(token).send(sendAr => {
      val costTime = System.currentTimeMillis() - startTime
      if (sendAr.succeeded()) {
        log.info(s"发送企业微信机器人请求成功, 耗时${costTime}毫秒,响应:{}", sendAr.result())
        response.end(successResponse(s"发送企业微信机器人请求成功, 耗时${costTime}毫秒", costTime).toString)
      } else {
        val cause = sendAr.cause()
        log.error(s"发送企业微信机器人请求失败, 耗时${costTime}毫秒", cause)
        response.end(failResponse(cause, costTime).toString)
      }
    })
  }
}
