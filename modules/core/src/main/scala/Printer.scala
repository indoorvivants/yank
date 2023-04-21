package com.indoorvivants.yank

import java.nio.file.Path

trait Printer {
  def printLine(str: String): Unit

  def highlightURL(url: String): String = url
  def highlightDestination(destination: Path): String = destination.toString()
}

object Printer {
  val stderr =
    new Printer {
      override def printLine(str: String): Unit = System.err.println(str)
    }
}

