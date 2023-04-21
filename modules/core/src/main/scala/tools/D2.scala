package com.indoorvivants.yank.tools

import com.indoorvivants.yank._

import com.indoorvivants.detective.Platform
import java.nio.file.Path
import java.nio.file.Files

class D2 extends Tool {
  type Config = TailwindCSS.Config

  override def cacheKey(c: Config) = s"d2-${c.version}"
  override def url(c: Config, t: Platform.Target): String =
    binaryUrl(c, t)

  private def binaryUrl(config: Config, target: Platform.Target) =
    s"https://github.com/terrastruct/d2/releases/download/v${config.version}/${binaryName(config.version, target)}.tar.gz"

  private def binaryName(version: String, target: Platform.Target) = {
    val prefix = "d2"
    import Platform.OS._
    import Platform.Arch._
    val os = target.os match {
      case Linux   => "linux"
      case MacOS   => "macos"
      case Unknown => "unknown"
      case Windows => "windows"
    }

    val arch = target.arch match {
      case Arm if target.bits == Platform.Bits.x64 => "arm64"
      case Intel                                   => "amd64"
      case _                                       => ???
    }

    s"$prefix-v$version-$os-$arch"
  }
  def process(downloaded: Path) = {

    val tmpDir = Files.createTempDirectory("com.indoorvivants.yank.d2")

    val cmd = Seq(
      "tar",
      "zvxf",
      downloaded.toString(),
      "-C",
      tmpDir.toString()
    )

    val proc = new java.lang.ProcessBuilder(cmd: _*)

    val started = proc.start()

    val exitCode = started.waitFor()

    assert(
      exitCode == 0,
      s"Unpacking D2 failed with code $exitCode. Full command: ${cmd.mkString(" ")}"
    )

    var found = Option.empty[Path]

    Files.walk(tmpDir).filter(_.getFileName().toString() == "d2").forEach { p =>
      found = Some(p)
    }

    // val ext = target.os match {
    //   case Platform.OS.Windows => ".exe"
    //   case _                   => ""
    // }

    ProcessOp.Copy(found.get)
  }
}

object D2 {
  class Config(val version: String)
  object Config {
    def apply(version: String) = new Config(version)
  }

}
