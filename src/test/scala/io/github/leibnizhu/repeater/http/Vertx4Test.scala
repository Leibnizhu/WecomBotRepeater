package io.github.leibnizhu.repeater.http

import io.vertx.core.Vertx
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

/**
 * @author Leibniz on 2020/11/26 9:52 AM
 */
class Vertx4Test extends FunSuite {
  private val log = LoggerFactory.getLogger(getClass)

  test("vertx4ShutdownHookTest") {
    val vertx = Vertx.vertx
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      val fileName = "test.txt"
      val exist = vertx.fileSystem().existsBlocking(fileName)
      log.info("{} exists?{}", fileName, exist)
    }))
  }
}
