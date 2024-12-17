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

import java.nio.file.Path

import com.indoorvivants.yank._

class ScalaHighlight extends Tool {
  type Config = ScalaHighlight.Config

  override def cacheKey(c: Config) = s"scala-highlight-${c.version}"
  override def url(c: Config, t: Platform.Target): String =
    binaryUrl(c, t)

  private def binaryUrl(config: Config, target: Platform.Target) =
    s"https://github.com/keynmol/scala-treesitter-highlighting/releases/download/v${config.version}/${binaryName(target)}"

  private def binaryName(target: Platform.Target) = {
    val prefix = "scala-highlight"
    import Platform.OS._
    import Platform.Arch._
    val os = target.os match {
      case Linux   => "pc-linux"
      case MacOS   => "apple-darwin"
      case Unknown => "unknown"
      case Windows => "pc-windows"
    }

    val arch = target.arch match {
      case Arm if target.bits == Platform.Bits.x64 => "aarch64"
      case Intel                                   => "x86_64"
      case _                                       => ???
    }

    s"$prefix-$arch-$os"
  }
  def process(downloaded: Path) = {
    ProcessOp.Use
  }

  override def readConfig(mp: Map[String, String]) =
    new Config(version = mp.getOrElse("version", ???))
}

object ScalaHighlight extends Tool.Companion(new ScalaHighlight) {
  class Config(val version: String)
  object Config {
    def apply(version: String) = new Config(version)
  }

}
