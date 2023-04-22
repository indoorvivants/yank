Global / excludeLintKeys += logManager

inThisBuild(
  List(
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := scalaBinaryVersion.value,
    organization := "com.indoorvivants",
    organizationName := "Anton Sviridov",
    homepage := Some(
      url("https://github.com/indoorvivants/mdoc-d2")
    ),
    startYear := Some(2023),
    licenses := List(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "keynmol",
        "Anton Sviridov",
        "velvetbaldmime@protonmail.com",
        url("https://blog.indoorvivants.com")
      )
    )
  )
)

val Versions = new {
  val Scala213 = "2.13.10"
  val Scala212 = "2.12.17"
  val Scala3 = "3.2.2"
  val scalaVersions = Seq(Scala3, Scala212, Scala213)

  val dirs = "26"
  val weaver = "0.8.3"
  val fs2 = "3.6.1"
}

lazy val root = project
  .in(file("."))
  .aggregate(core.projectRefs*)
  .settings(publish / skip := true, publishLocal / skip := true)

lazy val core = projectMatrix
  .in(file("modules/core"))
  .settings(
    name := "yank",
    Test / scalacOptions ~= filterConsoleScalacOptions,
    libraryDependencies += "dev.dirs" % "directories" % Versions.dirs,
    libraryDependencies += "com.disneystreaming" %% "weaver-cats" % Versions.weaver % Test,
    libraryDependencies += "co.fs2" %% "fs2-io" % Versions.fs2 % Test,
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    buildInfoPackage := "com.indoorvivants.yank.internal",
    buildInfoKeys := Seq[BuildInfoKey](
      version,
      scalaVersion,
      scalaBinaryVersion
    ),
    Test / envVars ++= Map("CACHE_BASE" -> (Test / target).value.toString),
    Test / fork := true,
    Test / parallelExecution := true
  )
  .jvmPlatform(Versions.scalaVersions)
  .enablePlugins(BuildInfoPlugin)

lazy val docs = project
  .in(file("myproject-docs"))
  .settings(
    scalaVersion := Versions.Scala213,
    mdocVariables := Map(
      "VERSION" -> version.value
    ),
    publish / skip := true,
    publishLocal / skip := true
  )
  .dependsOn(core.jvm(Versions.Scala213))
  .enablePlugins(MdocPlugin)

val scalafixRules = Seq(
  "OrganizeImports",
  "DisableSyntax",
  "LeakingImplicitClassVal",
  "NoValInForComprehension"
).mkString(" ")

val CICommands = Seq(
  "clean",
  "compile",
  "test",
  "docs/mdoc",
  "scalafmtCheckAll",
  "scalafmtSbtCheck",
  s"scalafix --check $scalafixRules",
  "headerCheck",
  "undeclaredCompileDependenciesTest",
  "unusedCompileDependenciesTest",
  "missinglinkCheck"
).mkString(";")

val PrepareCICommands = Seq(
  s"scalafix --rules $scalafixRules",
  "scalafmtAll",
  "scalafmtSbt",
  "headerCreate",
  "undeclaredCompileDependenciesTest"
).mkString(";")

addCommandAlias("ci", CICommands)

addCommandAlias("preCI", PrepareCICommands)
