package io.github.leibnizhu.repeater.util

import io.vertx.core.json.JsonObject

/**
 * @author Leibniz on 2020/10/28 11:59 AM
 */
object ResponseUtil {
  def successResponse(result: Any, costTime: Long): JsonObject =
    new JsonObject().
      put("status", "success")
      .put("results", result)
      .put("cost", costTime)

  def failResponse(cause: Throwable, costTime: Long): JsonObject =
    new JsonObject()
      .put("status", "error")
      .put("message", s"${cause.getClass.getName}:${cause.getMessage}")
      .put("cost", costTime)

  def failResponse(errMsg: String, costTime: Long): JsonObject =
    new JsonObject()
      .put("status", "error")
      .put("message", errMsg)
      .put("cost", costTime)
}
