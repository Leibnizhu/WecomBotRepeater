package io.github.leibnizhu.repeater.verticle

import io.github.leibnizhu.repeater.wecom.WecomBotRequest
import io.github.leibnizhu.repeater.wecom.message.MessageType.{Markdown, MessageType, Text}

/**
 * @author Leibniz on 2020/10/30 7:03 PM
 */
trait RequestEntity {
  def toWecomBotRequest(token: String, msgType: MessageType, mentionedList: List[String] = null): WecomBotRequest = {
    msgType match {
      case Text =>
        toWecomBotTextRequest(token, msgType, mentionedList)
      case Markdown =>
        toWecomBotMarkdownRequest(token, msgType, mentionedList)
    }
  }

  def toWecomBotTextRequest(token: String, msgType: MessageType, mentionedList: List[String] = null): WecomBotRequest

  def toWecomBotMarkdownRequest(token: String, msgType: MessageType, mentionedList: List[String] = null): WecomBotRequest
}
