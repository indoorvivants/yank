package com.indoorvivants.yank

import java.nio.file.Path

sealed trait ProcessOp extends Product with Serializable

object ProcessOp {
  case object Use extends ProcessOp
  case class Copy(path: Path) extends ProcessOp
}
