package io.github.leibnizhu.repeater.http

import com.fasterxml.jackson.databind.{ObjectMapper, ObjectReader}
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import io.github.leibnizhu.repeater.Constants._
import io.github.leibnizhu.repeater.util.ResponseUtil
import io.github.leibnizhu.repeater.util.ResponseUtil.{failResponse, handlerException}
import io.github.leibnizhu.repeater.wecom.message.MessageType
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.core.{AsyncResult, Handler}
import io.vertx.ext.web.RoutingContext
import org.slf4j.Logger

/**
 * 请求需要有以下参数(可以是路径参数,只要在router配置好参数名)：
 * 1. io.github.leibnizhu.repeater.Constants#REQ_PARAM_WECOM_BOT_TOKEN
 * 2. io.github.leibnizhu.repeater.Constants#REQ_PARAM_WECOM_BOT_TYPE
 * 3. io.github.leibnizhu.repeater.Constants#REQ_PARAM_MENTIONED_LIST
 * 请求体是json
 *
 * @author Leibniz on 2020/10/30 6:52 PM
 */
trait RequestBodyJsonHandler[Req <: RequestEntity] {
  protected val mapper: ObjectMapper = (new ObjectMapper() with ScalaObjectMapper).registerModule(DefaultScalaModule)
  protected val requestName: String
  protected val log: Logger
  protected val objectReader: ObjectReader

  def parseRequestBodyAndSendBot: Handler[RoutingContext] = rc => {
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
              val entityRequest = objectReader.readValue[Req](bodyAr.result().toString())
              log.debug("接收到{}请求,token:{},请求内容:{}", requestName, token, entityRequest)
              doSendWecomBot(startTime, token, rc, entityRequest)
            } catch {
              case e: Exception =>
                handlerException(s"解析${requestName}请求Json", startTime, response, e)
            }
          } else {
            handlerException(s"解析${requestName}请求体", startTime, response, bodyAr.cause())
          }
        })
      }
    } catch {
      case e: Exception =>
        handlerException(s"解析${requestName}请求", startTime, response, e)
    }
  }

  private def doSendWecomBot(startTime: Long, token: String, rc: RoutingContext, entityRequest: Req): Unit = {
    val (request, response) = (rc.request, rc.response)
    val mentionedList = Option(request.getParam(REQ_PARAM_MENTIONED_LIST)).map(_.split(",").toList).orNull
    val msgType = Option(request.getParam(REQ_PARAM_WECOM_BOT_TYPE)).map(MessageType.withName).getOrElse(MessageType.Markdown)
    val wecomBotMsg = entityRequest.toWecomBotMessage(token, msgType, mentionedList)
    vertx.eventBus().request(SEND_WECOM_BOT_EVENTBUS_ADDR, wecomBotMsg.toJsonObject())
      .onComplete((ar: AsyncResult[Message[JsonObject]]) => {
        if (ar.succeeded()) {
          response.end(ar.result().body().toString)
        } else {
          response.end(ResponseUtil.failResponse(ar.cause(), System.currentTimeMillis() - startTime).toString)
        }
      })
  }
}
