package com.indoorvivants.yank

import tools._
import java.nio.file.Path

class ToolsTests extends munit.FunSuite {

  test("Tailwind") {
    val (format, getLogs) = formatter
    val bootstrap = Tool.bootstrap(
      new TailwindCSS,
      cache = cache,
      downloader =
        Downloader.basic(ProgressReporter.eachPercentStep(formatter = format))
    )

    println(bootstrap(TailwindCSS.Config(version = "3.2.7")))
    println(getLogs())
  }

  test("D2") {
    val (format, getLogs) = formatter
    val bootstrap = Tool.bootstrap(
      new D2,
      cache = cache,
      downloader =
        Downloader.basic(ProgressReporter.eachPercentStep(formatter = format))
    )

    println(bootstrap(TailwindCSS.Config(version = "0.4.1")))
    println(getLogs())
  }

  private val cache = Cache.inFolder(Path.of(sys.env("CACHE_BASE")))
  private def formatter = {

    val b = Vector.newBuilder[(Float, DownloadInProgress)]

    val f = new ProgressFormatter {
      def format(downloadedPercentage: Float, dip: DownloadInProgress) = {
        b += (downloadedPercentage -> dip)
        None
      }

    }

    f -> (() => b.result())
  }
}
