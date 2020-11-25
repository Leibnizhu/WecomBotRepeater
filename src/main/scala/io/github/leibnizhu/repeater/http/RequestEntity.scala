package io.github.leibnizhu.repeater.http

import io.github.leibnizhu.repeater.wecom.message.MessageContent
import io.github.leibnizhu.repeater.wecom.message.MessageType.{Markdown, MessageType, Text}

/**
 * @author Leibniz on 2020/10/30 7:03 PM
 */
trait RequestEntity {
  def toWecomBotMessage(token: String, msgType: MessageType, mentionedList: List[String] = null): MessageContent = {
    msgType match {
      case Text =>
        toWecomBotTextMessage(token, msgType, mentionedList)
      case Markdown =>
        toWecomBotMarkdownMessage(token, msgType, mentionedList)
    }
  }

  def toWecomBotTextMessage(token: String, msgType: MessageType, mentionedList: List[String] = null): MessageContent

  def toWecomBotMarkdownMessage(token: String, msgType: MessageType, mentionedList: List[String] = null): MessageContent
}
