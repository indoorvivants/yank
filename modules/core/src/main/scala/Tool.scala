package com.indoorvivants.yank

import com.indoorvivants.detective.Platform
import java.nio.file.Path
import java.nio.file.Files
import java.nio.file.StandardCopyOption

trait Tool {
  type Config

  def cacheKey(c: Config): String
  def url(config: Config, target: Platform.Target): String

  def process(downloaded: Path): ProcessOp
}

object Tool {
  def bootstrap(
      t: Tool,
      cache: Cache = Cache.system,
      downloader: Downloader =
        Downloader.basic(ProgressReporter.eachPercentStep(5, Printer.stderr)),
      platform: Platform.Target = Platform.target
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

      op match {
        case ProcessOp.Use =>
          Files.copy(
            downloadDestination,
            destination,
            StandardCopyOption.REPLACE_EXISTING
          )

          if (!destination.toFile().canExecute())
            destination.toFile().setExecutable(true)

        case ProcessOp.Copy(other) =>
          Files.copy(other, destination, StandardCopyOption.REPLACE_EXISTING)
      }

      if (!destination.toFile().canExecute())
        destination.toFile().setExecutable(true)

      destination
    }
  }
}
