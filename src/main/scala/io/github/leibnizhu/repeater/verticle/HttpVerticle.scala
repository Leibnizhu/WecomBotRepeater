package io.github.leibnizhu.repeater.verticle

import io.github.leibnizhu.repeater.Constants
import io.github.leibnizhu.repeater.Constants._
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import org.slf4j.LoggerFactory

class HttpVerticle extends AbstractVerticle {
  private val log = LoggerFactory.getLogger(getClass)
  private var mainRouter: Router = _
  private var server: HttpServer = _

  override def start(): Unit = {
    //初始化工具类/组件
    Constants.init(vertx.getOrCreateContext())
    this.mainRouter = Router.router(vertx)
    this.server = vertx.createHttpServer
    mountRouters() //挂载所有子路由
    startServer(); //启动服务器
  }

  def mountRouters(): Unit = {
    mainRouter.post(s"/grafana/:$REQ_PARAM_WECOM_BOT_TOKEN/:$REQ_PARAM_WECOM_BOT_TYPE/:$REQ_PARAM_MENTIONED_LIST").handler(GrafanaHandler.grafanaToBot)
    mainRouter.post(s"/grafana/:$REQ_PARAM_WECOM_BOT_TOKEN/:$REQ_PARAM_WECOM_BOT_TYPE").handler(GrafanaHandler.grafanaToBot)
    mainRouter.post(s"/grafana/:$REQ_PARAM_WECOM_BOT_TOKEN").handler(GrafanaHandler.grafanaToBot)
    mainRouter.post(s"/grafana").handler(GrafanaHandler.error)
    mainRouter.get("/*").handler(StaticHandler.create.setWebRoot("static"))
  }

  /**
   * 启动服务器
   */
  private def startServer(): Unit = {
    val port = config.getInteger("serverPort", 8083)
    server.requestHandler(mainRouter.handle(_)).listen(port).onComplete(ar => if (ar.succeeded()) {
      log.info("监听{}端口的HTTP服务器启动成功", port)
    } else {
      log.error("监听{}端口的HTTP服务器失败，原因：{}", Seq[AnyRef](port, ar.cause().getLocalizedMessage): _*)
    })
  }

  override def stop(): Unit = {
    server.close(res => log.info("HTTP服务器关闭" + (if (res.succeeded) "成功" else "失败")))
    super.stop()
  }
}
