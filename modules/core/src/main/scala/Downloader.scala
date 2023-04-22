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

import java.io._
import java.net.URL

import scala.util.Try

trait Downloader {
  def download(link: String, to: java.nio.file.Path): Try[File]
}

object Downloader {
  def basic(reporter: ProgressReporter, bufSize: Int = 102400): Downloader =
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
        val fout = new FileOutputStream(to.toFile)
        val out = Option(new BufferedOutputStream(fout, bufSize))
        try {
          val inputStream = conn.getInputStream()
          is = Some(inputStream)
          var downloaded = 0L
          val buffer = Array.ofDim[Byte](16384)
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
          out.foreach(_.flush())

          to.toFile()
        } finally {
          is.foreach(_.close())
          out.foreach(_.close())
          fout.close()
        }

      }
  }
}
