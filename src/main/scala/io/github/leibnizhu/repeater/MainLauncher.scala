package io.github.leibnizhu.repeater

import io.github.leibnizhu.repeater.http.HttpVerticle
import io.github.leibnizhu.repeater.wecom.SendWecomBotVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.{AsyncResult, DeploymentOptions, Vertx}
import org.slf4j.LoggerFactory

object MainLauncher {
  private val log = LoggerFactory.getLogger(classOf[MainLauncher])

  def main(args: Array[String]): Unit = {
    //Force to use slf4j
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")
//    System.setProperty("vertx.disableFileCaching", "true")
    val vertx = Vertx.vertx
    val fs = vertx.fileSystem()
    val configFile = if (args.length > 0) args(0) else "config.json" //配置文件路径,如果没有输入参数则读取当前目录的config.json
    if (fs.existsBlocking(configFile)) { //配置文件若存在则读取,否则使用默认配置
      fs.readFile(configFile).onComplete((ar: AsyncResult[Buffer]) => {
        if (ar.succeeded()) {
          val configJson = new JsonObject(ar.result())
          log.info("读取配置文件{}成功,配置内容:{},准备启动Verticle.", Array(configFile, configJson): _*)
          val options = new DeploymentOptions().setConfig(configJson)
          vertx.deployVerticle(s"scala:${classOf[HttpVerticle].getName}", options)
          vertx.deployVerticle(s"scala:${classOf[SendWecomBotVerticle].getName}", options)
        } else {
          log.error("读取配置文件失败.", ar.cause())
          System.exit(1)
        }
      })
    } else {
      vertx.deployVerticle(s"scala:${classOf[HttpVerticle].getName}")
      vertx.deployVerticle(s"scala:${classOf[SendWecomBotVerticle].getName}")
    }
  }
}

class MainLauncher //java的main方法要用