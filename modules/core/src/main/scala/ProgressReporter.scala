package com.indoorvivants.yank

trait ProgressReporter extends (DownloadInProgress => Float => Unit)

object ProgressReporter {
  def eachPercentStep(
      step: Float = 5,
      print: Printer = Printer.stderr,
      formatter: ProgressFormatter = ProgressFormatter.autoColor
  ): ProgressReporter = dip => {
    var prev = 0f
    i =>
      if (i - prev >= step) {
        prev = i
        formatter.format(i, dip).foreach(print.printLine)
      }
  }

}
