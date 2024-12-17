/*
 * Copyright 2023 Anton Sviridov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.indoorvivants.yank

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

trait Tool {
  type Config

  def cacheKey(c: Config): String
  def url(config: Config, target: Platform.Target): String

  def process(downloaded: Path): ProcessOp

  def readConfig(params: Map[String, String]): Config
}

object Tool {
  def bootstrap(
      t: Tool,
      cache: Cache = Cache.system,
      downloader: Downloader =
        Downloader.basic(ProgressReporter.eachPercentStep(5, Printer.stderr)),
      platform: Platform.Target = Platform.target,
  ): t.Config => Path = (conf: t.Config) => {
    val cacheKey = t.cacheKey(conf)
    val destination = cache.named(cacheKey)
    if (destination.toFile().exists()) {
      if (destination.toFile().canExecute()) destination
      else {
        destination.toFile().setExecutable(true)
        destination
      }
    } else {
      val tempDir = Files.createTempDirectory(s"com.indoorvivants.yank")
      val url = t.url(conf, platform)
      val downloadDestination =
        downloader.download(url, tempDir.resolve(cacheKey)).get.toPath()
      val op = t.process(downloadDestination)

      downloadDestination.toFile().deleteOnExit()

      op match {
        case ProcessOp.Use =>
          Files.copy(
            downloadDestination,
            destination,
            StandardCopyOption.REPLACE_EXISTING,
          )

          if (!destination.toFile().canExecute())
            destination.toFile().setExecutable(true)

        case ProcessOp.Copy(other, cleanup) =>
          try {
            Files.copy(other, destination, StandardCopyOption.REPLACE_EXISTING)
          } finally {
            cleanup()
          }
      }

      if (!destination.toFile().canExecute())
        destination.toFile().setExecutable(true)

      downloadDestination.toFile().delete()

      destination
    }
  }

  private[yank] class Companion[T <: Tool](val default: T) {

    def bootstrap(config: default.Config): Path =
      Tool.bootstrap(default)(config)

  }
}
