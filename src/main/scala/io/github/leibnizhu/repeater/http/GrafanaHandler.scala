package io.github.leibnizhu.repeater.http

import java.io.File
import java.util.concurrent.ConcurrentHashMap

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectReader
import io.github.leibnizhu.repeater.wecom.message.MarkdownMessage.MarkdownBuilder
import io.github.leibnizhu.repeater.wecom.message.MessageType.MessageType
import io.github.leibnizhu.repeater.wecom.message.{MarkdownMessage, MessageContent, TextMessage}
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.apache.commons.io.FileUtils
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
 * @author Leibniz on 2020/10/28 11:40 AM
 */
object GrafanaHandler extends RequestBodyJsonHandler[GrafanaRequest] {
  override protected val requestName: String = "Grafana"
  override protected val log: Logger = LoggerFactory.getLogger(getClass)
  override protected val objectReader: ObjectReader = mapper.readerFor(classOf[GrafanaRequest])

  def grafanaToBot: Handler[RoutingContext] = parseRequestBodyAndSendBot

  private val alertingMapFilePath = "./alertingMap.json"
  private val alertingMap = Option({
    val alertingMapFile = new File(alertingMapFilePath)
    if (alertingMapFile.exists()) {
      val jsonFileContent = FileUtils.readFileToString(alertingMapFile)
      if (jsonFileContent != null) {
        val tmpScalaMap = mapper.readerFor(classOf[Map[String, List[String]]]).readValue[Map[String, List[String]]](jsonFileContent)
        new ConcurrentHashMap[String, Set[String]](tmpScalaMap.mapValues(_.toSet).asJava)
      } else null
    } else {
      alertingMapFile.createNewFile()
      null
    }
  }).getOrElse(new ConcurrentHashMap())

  {
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      val alertingMapFile = new File(alertingMapFilePath)
      val objectWriter = mapper.writerFor(classOf[ConcurrentHashMap[String, Set[String]]])
      val contentString = objectWriter.writeValueAsString(alertingMap)
      FileUtils.write(alertingMapFile, contentString)
    }))
  }

  def curAlerting(dashboardId: Int, ruleName: String, isOk: Boolean, isAlert: Boolean): Set[String] = {
    var alertingSet = alertingMap.computeIfAbsent(dashboardId.toString, _ => Set())
    if (isOk) {
      alertingSet -= ruleName
    } else if (isAlert) {
      alertingSet += ruleName
    }
    alertingMap.put(dashboardId.toString, alertingSet)
    alertingSet
  }
}

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
                          @JsonProperty tags: Map[String, String],
                          @JsonProperty title: String
                         ) extends RequestEntity {
  override def toWecomBotTextMessage(token: String, msgType: MessageType, mentionedList: List[String]): MessageContent = {
    val isOk = title.startsWith("[OK]")
    val isAlert = title.startsWith("[Alerting]")
    val alertSet = GrafanaHandler.curAlerting(dashboardId, ruleName, isOk, isAlert)
    val textContent = s"标题:$title,触发规则:$ruleName,信息:$message,当前Dashboard还存在的警告:${if (alertSet.isEmpty) "当前Dashboard没有警告了" else String.join(",", alertSet.asJava)}"
    TextMessage(token, textContent, mentionedList)
  }

  override def toWecomBotMarkdownMessage(token: String, msgType: MessageType, mentionedList: List[String]): MessageContent = {
    val isOk = title.startsWith("[OK]")
    val isAlert = title.startsWith("[Alerting]")
    val alertSet = GrafanaHandler.curAlerting(dashboardId, ruleName, isOk, isAlert)
    val keyColor = if (isOk) "info" else if (isAlert) "warning" else "comment"
    val messageBuilder = new MarkdownBuilder()
      .text("接收到Grafana通知,具体信息:").newLine()
      .quoted().text("触发规则:").hrefLink(ruleName, ruleUrl).newLine()
      .quoted().text("标题:").colored(keyColor, title).newLine()
      .quoted().text("信息:").colored(keyColor, message).newLine()
    if (alertSet.isEmpty) {
      messageBuilder.quoted().colored("comment", "当前Dashboard没有警告了").newLine()
    } else {
      messageBuilder.quoted().text("当前Dashboard还存在的警告:").colored("warning", String.join(",", alertSet.asJava)).newLine()
    }
    val markdownContent = messageBuilder.mentionUsers(mentionedList).toMarkdownString
    MarkdownMessage(token, markdownContent)
  }
}

case class EvalMatch(@JsonProperty value: Int,
                     @JsonProperty metric: String,
                     @JsonProperty tags: Map[String, String])
