package io.github.leibnizhu.repeater.verticle

import java.util.concurrent.ConcurrentHashMap

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectReader
import io.github.leibnizhu.repeater.Constants
import io.github.leibnizhu.repeater.wecom.WecomBotRequest
import io.github.leibnizhu.repeater.wecom.message.MarkdownMessage.MarkdownBuilder
import io.github.leibnizhu.repeater.wecom.message.MessageType.MessageType
import io.github.leibnizhu.repeater.wecom.message.{MarkdownMessage, TextMessage}
import io.vertx.core.buffer.Buffer
import io.vertx.core.{Handler, Vertx}
import io.vertx.ext.web.RoutingContext
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
 * @author Leibniz on 2020/10/28 11:40 AM
 */
object GrafanaHandler extends RequestBodyJsonHandler[GrafanaRequest] {
  private val alertingMapFilePath = "./alertingMap.json"

  override protected val requestName: String = "Grafana"
  override protected val log: Logger = LoggerFactory.getLogger(getClass)

  override protected val objectReader: ObjectReader = mapper.readerFor(classOf[GrafanaRequest])

  private def saveAlertingMap(): Unit = {
    val objectWriter = mapper.writerFor(classOf[ConcurrentHashMap[String, Set[String]]])
    Constants.vertx.fileSystem().writeFileBlocking(alertingMapFilePath, Buffer.buffer(objectWriter.writeValueAsString(alertingMap)))
  }

  private def loadAlertingMap(): ConcurrentHashMap[String, Set[String]] = {
    if (Constants.vertx == null) {
      Constants.vertx = Vertx.vertx()
    }
    val fs = Constants.vertx.fileSystem()
    if (fs.existsBlocking(alertingMapFilePath)) {
      val jsonFileBuf = fs.readFileBlocking(alertingMapFilePath)
      if (jsonFileBuf != null) {
        val tmpScalaMap = mapper.readerFor(classOf[Map[String, List[String]]]).readValue[Map[String, List[String]]](jsonFileBuf.toString())
        return new ConcurrentHashMap[String, Set[String]](tmpScalaMap.mapValues(_.toSet).asJava)
      }
    }
    null
  }

  private val alertingMap: ConcurrentHashMap[String, Set[String]] = {
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      saveAlertingMap()
    }))
    Option(loadAlertingMap()).getOrElse(new ConcurrentHashMap())
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

  def grafanaToBot: Handler[RoutingContext] = parseRequestBodyAndSendBot
}

case class EvalMatch(@JsonProperty value: Int,
                     @JsonProperty metric: String,
                     @JsonProperty tags: Map[String, String])

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
  override def toWecomBotTextRequest(token: String, msgType: MessageType, mentionedList: List[String]): WecomBotRequest = {
    val textContent = s"标题:$title,触发规则:$ruleName,信息:$message"
    new WecomBotRequest(TextMessage(token, textContent, mentionedList))
  }

  override def toWecomBotMarkdownRequest(token: String, msgType: MessageType, mentionedList: List[String]): WecomBotRequest = {
    val isOk = title.startsWith("[OK]")
    val isAlert = title.startsWith("[Alerting]")
    val alertSet = GrafanaHandler.curAlerting(dashboardId, ruleName, isOk, isAlert)
    val keyColor = if (isOk) "info" else if (isAlert) "warning" else "comment"
    val markdownContent = new MarkdownBuilder()
      .text("接收到Grafana通知,具体信息:").newLine()
      .quoted().text("触发规则:").hrefLink(ruleName, ruleUrl).newLine()
      .quoted().text("标题:").colored(keyColor, title).newLine()
      .quoted().text("信息:").colored(keyColor, message).newLine()
      .quoted().text("当前Dashboard还存在的警告:").colored("warning", String.join(",", alertSet.asJava)).newLine()
      .mentionUsers(mentionedList)
      .toMarkdownString
    new WecomBotRequest(MarkdownMessage(token, markdownContent))
  }
}
