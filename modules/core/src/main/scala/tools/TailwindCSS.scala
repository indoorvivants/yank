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

  override def process(downloaded: Path) = ProcessOp.Use

  override def readConfig(mp: Map[String, String]) =
    new Config(version = mp.getOrElse("version", ???))
}

object TailwindCSS extends Tool.Companion(new TailwindCSS) {
  class Config(val version: String)
  object Config {
    def apply(version: String) = new Config(version)
  }

}
