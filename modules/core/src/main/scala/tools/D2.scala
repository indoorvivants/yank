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

package com.indoorvivants.yank.tools

import java.nio.file.Files
import java.nio.file.Path

import com.indoorvivants.yank._

class D2 extends Tool {
  type Config = D2.Config

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

    ProcessOp.Copy(
      found.get,
      cleanup = () => {
        FileUtils.deleteRecursively(tmpDir)
      }
    )
  }

  override def readConfig(mp: Map[String, String]) =
    new Config(version = mp.getOrElse("version", ???))
}

object D2 extends Tool.Companion(new D2) {
  class Config(val version: String)
  object Config {
    def apply(version: String) = new Config(version)
  }

}
