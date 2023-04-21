package com.indoorvivants.yank

import java.nio.file.Paths

class CacheTests extends munit.FunSuite {
  test("System cache") {
    val cache = Cache.system

    assertEquals(
      cache.named("subatomic"),
      Paths
        .get { dev.dirs.BaseDirectories.get().cacheDir }
        .resolve("com.indoorvivants.yank")
        .resolve("subatomic")
    )

    assertEquals(
      cache.named("test"),
      Paths
        .get { dev.dirs.BaseDirectories.get().cacheDir }
        .resolve("com.indoorvivants.yank")
        .resolve("test")
    )

    assert(cache.named("test").getParent().toFile().isDirectory())
    assert(!cache.named("test").toFile().exists())
  }
}
