package io.github.leibnizhu.repeater

import java.io.File

import io.vertx.core.{Context, Vertx}
import io.vertx.core.json.JsonObject

object Constants {
  /**
   * Vert.X变量
   */
  var vertx: Vertx = _
  private var vertxContext: Context = _
  private var config: JsonObject = _

  /**
   * 从配置文件读取到的配置
   */


  /**
   * Lucene文档(Document)中的key,基本不用动
   */
  val REQ_PARAM_WECOM_BOT_TOKEN: String = "wecomBotToken"
  val WECOM_BOT_API_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key="

  def init(ctx: Context): Unit = {
    this.vertxContext = ctx
    this.vertx = ctx.owner
    this.config = vertxContext.config()
  }
}
