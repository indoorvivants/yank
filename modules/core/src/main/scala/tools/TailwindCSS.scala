package com.indoorvivants.yank.tools

import com.indoorvivants.yank._

import com.indoorvivants.detective.Platform
import java.nio.file.Path

class TailwindCSS extends Tool {
  type Config = TailwindCSS.Config

  override def cacheKey(c: Config) = s"tailwindcss-${c.version}"
  override def url(c: Config, t: Platform.Target): String =
    binaryUrl(c, t)

  private def binaryUrl(config: Config, target: Platform.Target) =
    s"https://github.com/tailwindlabs/tailwindcss/releases/download/v${config.version}/${binaryName(target)}"

  private def binaryName(target: Platform.Target) = {
    import Platform.OS._
    import Platform.Arch._
    val prefix = "tailwindcss"
    val ext = target.os match {
      case Platform.OS.Windows => ".exe"
      case _                   => ""
    }
    val os = target.os match {
      case Linux   => "linux"
      case MacOS   => "macos"
      case Unknown => "unknown"
      case Windows => "windows"
    }

    val arch = target.arch match {
      case Arm if target.bits == Platform.Bits.x64 => "arm64"
      case Arm                                     => "armv7"
      case Intel                                   => "x64"
    }

    s"$prefix-$os-$arch$ext"
  }

  def process(downloaded: Path) = ProcessOp.Use
}

object TailwindCSS {
  class Config(val version: String)
  object Config {
    def apply(version: String) = new Config(version)
  }

}
