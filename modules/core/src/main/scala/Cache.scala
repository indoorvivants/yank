package com.indoorvivants.yank

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files

trait Cache {
  def named(name: String): Path
}

object Cache {
  val system = new Cache {
    def named(name: String): Path = {

      val p = Paths
        .get(dev.dirs.BaseDirectories.get().cacheDir)
        .resolve("com.indoorvivants.yank")
        .resolve(name)
      Files.createDirectories(p.getParent())

      p
    }

  }

  def inFolder(path: Path) = new Cache {
    def named(name: String): Path = {

      val p = path.resolve(name)

      Files.createDirectories(p.getParent())

      p
    }
  }
}

