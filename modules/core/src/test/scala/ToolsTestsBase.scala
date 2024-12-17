package com.indoorvivants.yank.tools

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import cats.effect.IO
import cats.effect.kernel.Resource
import cats.effect.std.Dispatcher
import cats.effect.std.Random
import cats.syntax.all._
import com.indoorvivants.yank._
import fs2.io.file

abstract class ToolsTestsBase(t: Tool, params: Map[String, String])
    extends weaver.IOSuite {
  type Res = Cache
  override def sharedResource: Resource[IO, Res] = {
    val rnd = Random
      .scalaUtilRandom[IO]
      .flatMap(_.nextAlphaNumeric.replicateA(10).map(_.mkString))

    val make = rnd.map { id =>
      {
        val path = Paths.get(sys.env("CACHE_BASE")).resolve(id)
        Cache.inFolder(path) -> path
      }
    }
    Resource
      .make(make)(c =>
        file.Files[IO].deleteRecursively(file.Path.fromNioPath(c._2)),
      )
      .map(_._1)
  }

  test("Download and save") { (cache, log) =>
    formatter(log).use { case (format, logs) =>
      val bootstrap = Tool.bootstrap(
        t,
        cache = cache,
        downloader =
          Downloader.basic(ProgressReporter.eachPercentStep(formatter = format)),
      )

      IO(bootstrap(t.readConfig(params))).flatMap { path =>
        val exists = expect(path.toFile.exists())
        val executable = expect(path.toFile.canExecute())

        val logsHaveTempPath =
          logs.map(_.map(_._2.destination)).map { paths =>
            forEach(paths.distinct)(p =>
              expect.all(
                p != path, // not downloading straight to cache
                !p.toFile().exists(), // temp file was removed
              ),
            )
          }

        logsHaveTempPath.map(_ && exists && executable)
      }

    }

  }

  private def formatter(log: weaver.Log[IO]) =
    Dispatcher
      .sequential[IO]
      .product(Resource.eval(IO.ref(List.empty[(Float, DownloadInProgress)])))
      .map { case (disp, ref) =>
        val f = new ProgressFormatter {
          def format(downloadedPercentage: Float, dip: DownloadInProgress) = {
            disp.unsafeRunSync(
              ref.update(_ :+ (downloadedPercentage -> dip)) *> log
                .info(s"$downloadedPercentage %"),
            )
            None
          }

        }

        f -> ref.get
      }
}
