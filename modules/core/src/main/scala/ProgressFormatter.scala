package com.indoorvivants.yank

trait ProgressFormatter {
  def format(
      downloadedPercentage: Float,
      dip: DownloadInProgress
  ): Option[String]
}

object ProgressFormatter {
  val autoColor = new ProgressFormatter {
    def format(
        downloadedPercentage: Float,
        dip: DownloadInProgress
    ) =
      Some(
        s"Downloaded ${downloadedPercentage}% of ${dip.url} into ${dip.destination}"
      )
  }
}

