package com.indoorvivants.yank.tools

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import cats.effect.IO
import cats.effect.kernel.Resource
import cats.effect.std.Dispatcher
import cats.effect.std.Random
import cats.syntax.all._
import com.indoorvivants.yank._
import fs2.io.file

object TailwindTests
    extends ToolsTestsBase(new TailwindCSS, Map("version" -> "3.2.7"))

object D2Tests extends ToolsTestsBase(new D2, Map("version" -> "0.4.1"))

object ScalaHighlightTests
    extends ToolsTestsBase(new ScalaHighlight, Map("version" -> "0.0.2"))
