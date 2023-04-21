package com.indoorvivants.yank

import java.io._
import scala.util.Try
import java.net.URL

trait Downloader {
  def download(link: String, to: java.nio.file.Path): Try[File]
}

object Downloader {
  def basic(reporter: ProgressReporter, bufSize: Int = 102_400): Downloader =
    new Basic(
      bufSize,
      reporter
    )
  private class Basic(bufSize: Int, reporter: ProgressReporter)
      extends Downloader {
    def download(link: String, to: java.nio.file.Path) =
      Try {
        val url = new URL(link)
        val conn = url.openConnection()
        val contentLength = conn.getContentLengthLong()
        var is = Option.empty[InputStream]
        val out = Option(new FileOutputStream(to.toFile))
        try {
          val inputStream = conn.getInputStream()
          is = Some(inputStream)
          var downloaded = 0L
          val buffer = Array.ofDim[Byte](bufSize)
          var length = 0

          // Looping until server finishes
          var percentage = 0f
          val report = reporter(new DownloadInProgress(link, to))
          while ({ length = inputStream.read(buffer); length } != -1) {
            // Writing data
            out.foreach(_.write(buffer, 0, length))
            downloaded += length
            val newPercentage = (downloaded * 100f) / contentLength
            if (newPercentage != percentage) {
              report(newPercentage)
              percentage = newPercentage
            }

          }

          to.toFile()
        } finally {
          is.foreach(_.close())
          out.foreach(_.close())
        }

      }
  }
}
