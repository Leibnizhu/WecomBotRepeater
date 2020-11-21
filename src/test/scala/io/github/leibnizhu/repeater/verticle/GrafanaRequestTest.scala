package io.github.leibnizhu.repeater.verticle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import io.github.leibnizhu.repeater.wecom.message.MessageType
import io.vertx.core.json.JsonArray
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
 * @author Leibniz on 2020/10/30 6:25 PM
 */
class GrafanaRequestTest extends FunSuite {
  private val log = LoggerFactory.getLogger(getClass)
  private val objectReader = (new ObjectMapper() with ScalaObjectMapper).registerModule(DefaultScalaModule).readerFor(classOf[GrafanaRequest])
  private val grafanaRequestJson = "{\n    \"dashboardId\": 1,\n    \"evalMatches\": [\n        {\n            \"value\": 100,\n            \"metric\": \"High value\",\n            \"tags\": null\n        },\n        {\n            \"value\": 200,\n            \"metric\": \"Higher Value\",\n            \"tags\": null\n        }\n    ],\n    \"imageUrl\": \"https://grafana.com/assets/img/blog/mixed_styles.png\",\n    \"message\": \"Someone is testing the alert notification within Grafana.\",\n    \"orgId\": 0,\n    \"panelId\": 1,\n    \"ruleId\": 0,\n    \"ruleName\": \"Test notification\",\n    \"ruleUrl\": \"https://www.baidu.com/\",\n    \"state\": \"alerting\",\n    \"tags\": {},\n    \"title\": \"[Alerting] Test notification\"\n}"

  test("textMessageTest") {
    val grafanaRequest = objectReader.readValue[GrafanaRequest](grafanaRequestJson)
    val reqJson = grafanaRequest.toWecomBotRequest("12345678980", MessageType.Text, List()).msgContent.wholeJson()
    log.info("requset json:{}", reqJson)
    assert(reqJson.getString("msgtype") == "text")
    assert(reqJson.getJsonObject("text") != null)
    assert(reqJson.getJsonObject("text").getString("content") == "标题:[Alerting] Test notification,触发规则:Test notification,信息:Someone is testing the alert notification within Grafana.")
  }

  test("textMessageWithMentionTest") {
    val grafanaRequest = objectReader.readValue[GrafanaRequest](grafanaRequestJson)
    val reqJson = grafanaRequest.toWecomBotRequest("12345678980", MessageType.Text, List("18888888888")).msgContent.wholeJson()
    log.info("requset json:{}", reqJson)
    assert(reqJson.getString("msgtype") == "text")
    assert(reqJson.getJsonObject("text") != null)
    assert(reqJson.getJsonObject("text").getString("content") == "标题:[Alerting] Test notification,触发规则:Test notification,信息:Someone is testing the alert notification within Grafana.")
    assert(reqJson.getJsonObject("text").getJsonArray("mentioned_mobile_list") == new JsonArray(List("18888888888").asJava))
  }

  test("markdownMessageTest") {
    val grafanaRequest = objectReader.readValue[GrafanaRequest](grafanaRequestJson)
    val reqJson = grafanaRequest.toWecomBotRequest("12345678980", MessageType.Markdown, List()).msgContent.wholeJson()
    log.info("requset json:{}", reqJson)
    assert(reqJson.getString("msgtype") == "markdown")
    assert(reqJson.getJsonObject("markdown") != null)
    assert(reqJson.getJsonObject("markdown").getString("content") == "接收到Grafana通知,具体信息:\n> 触发规则:[Test notification](https://www.baidu.com/)\n> 标题:<font color=\"warning\">[Alerting] Test notification</font>\n> 信息:<font color=\"warning\">Someone is testing the alert notification within Grafana.</font>\n> 当前Dashboard还存在的警告:<font color=\"warning\">Test notification</font>\n")
  }

  test("markdownMessageWithMentionTest") {
    val grafanaRequest = objectReader.readValue[GrafanaRequest](grafanaRequestJson)
    val reqJson = grafanaRequest.toWecomBotRequest("12345678980", MessageType.Markdown, List("test@google.com")).msgContent.wholeJson()
    log.info("requset json:{}", reqJson)
    assert(reqJson.getString("msgtype") == "markdown")
    assert(reqJson.getJsonObject("markdown") != null)
    assert(reqJson.getJsonObject("markdown").getString("content") == "接收到Grafana通知,具体信息:\n> 触发规则:[Test notification](https://www.baidu.com/)\n> 标题:<font color=\"warning\">[Alerting] Test notification</font>\n> 信息:<font color=\"warning\">Someone is testing the alert notification within Grafana.</font>\n> 当前Dashboard还存在的警告:<font color=\"warning\">Test notification</font>\n<@test@google.com>")
  }
}
