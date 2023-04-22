package com.indoorvivants.yank

import java.nio.file.Paths

object CacheTests extends weaver.FunSuite {
  test("System cache") {
    val cache = Cache.system

    val cacheDir = { dev.dirs.BaseDirectories.get().cacheDir }
    expect.all(
      cache.named("subatomic") ==
        Paths
          .get(cacheDir)
          .resolve("com.indoorvivants.yank")
          .resolve("subatomic"),
      cache.named("test") ==
        Paths
          .get { cacheDir }
          .resolve("com.indoorvivants.yank")
          .resolve("test"),
      cache.named("test").getParent().toFile().isDirectory(),
      !cache.named("test").toFile().exists()
    )
  }
}
