package io.github.leibnizhu.repeater.wecom

import io.github.leibnizhu.repeater.wecom.MessageType.MessageType
import io.vertx.core.json.JsonObject

/**
 * @author Leibniz on 2020/10/28 2:21 PM
 */
trait MessageContent {
  def msgType(): MessageType;
  def toJsonObject(): JsonObject;
  def token(): String;
}
