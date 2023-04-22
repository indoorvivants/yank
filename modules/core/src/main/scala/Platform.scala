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

package com.indoorvivants.yank

// Lifted from detective: https://github.com/indoorvivants/detective/blob/deebfdcdd4bf2e5c3fbe510a240fc95b96702f2b/modules/platform/src/main/scala/Platform.scala

private[yank] object Platform {
  sealed abstract class OS(val string: String)
      extends Product
      with Serializable {
    import OS._
    def coursierString: String = this match {
      case Windows => "pc-win32"
      case MacOS   => "apple-darwin"
      case Linux   => "pc-linux"
      case Unknown => "unknown"
    }
  }
  object OS {
    case object Windows extends OS("windows")
    case object MacOS extends OS("osx")
    case object Linux extends OS("linux")
    case object Unknown extends OS("unknown")

    val all = List(Windows, MacOS, Linux, Unknown)
    def detect(osNameProp: String): OS = normalise(osNameProp) match {
      case p if p.startsWith("linux")                         => OS.Linux
      case p if p.startsWith("windows")                       => OS.Windows
      case p if p.startsWith("osx") || p.startsWith("macosx") => OS.MacOS
      case _                                                  => OS.Unknown
    }
    def fromProps(props: Map[String, String]): OS =
      detect(props.getOrElse("os.name", ""))
  }

  sealed abstract class Arch extends Product with Serializable
  object Arch {
    case object Intel extends Arch {}
    case object Arm extends Arch {}

    val all = List(Intel, Arm)
    def detect(osArchProp: String): Arch = normalise(osArchProp) match {
      case "amd64" | "x64" | "x8664" | "x86" => Intel
      case "aarch64" | "arm64"               => Arm
    }

    def fromProps(props: Map[String, String]): Arch =
      detect(props.getOrElse("os.arch", ""))
  }

  sealed abstract class Bits extends Product with Serializable
  object Bits {
    case object x32 extends Bits
    case object x64 extends Bits

    def detect(sunArchProp: String) =
      sunArchProp match {
        case "64" => x64
        case "32" => x32
      }

    def fromProps(props: Map[String, String]): Bits =
      detect(props.getOrElse("sun.arch.data.model", "64"))
  }

  case class Target(os: OS, arch: Arch, bits: Bits)

  private lazy val propsMap = sys.props.toMap

  lazy val os = OS.fromProps(propsMap)
  lazy val arch = Arch.fromProps(propsMap)
  lazy val bits = Bits.fromProps(propsMap)
  lazy val target = Target(os, arch, bits)

  private def normalise(s: String) =
    s.toLowerCase.replaceAll("[^a-z0-9]+", "")
}
