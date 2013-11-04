import sbt._
import Keys._
import Defaults._

//import sbtandroid.AndroidPlugin._
//import sbtrobovm.RobovmPlugin._

import sbtassembly.Plugin._
import AssemblyKeys._

object Settings {
  lazy val scalameter = new TestFramework("org.scalameter.ScalaMeterFramework")

  lazy val common = Defaults.defaultSettings ++ Seq(
    version := "0.1",
    scalaVersion := "2.10.3",
    javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.6", "-target", "1.6"),
    scalacOptions ++= Seq("-Xlint", "-unchecked", "-deprecation", "-feature"),
    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
      "com.github.axel22" %% "scalameter" % "0.3" % "test",
      "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test"
    ),
    parallelExecution in Test := false,
    testFrameworks in Test += scalameter,
    testOptions in Test ++= Seq(
      Tests.Argument(scalameter, "-preJDK7"),
      Tests.Argument(TestFrameworks.ScalaTest, "-o", "-u", "target/test-reports")
    ),
    unmanagedBase <<= baseDirectory(_/"libs"),
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies ++= Seq(
      "com.badlogicgames.gdx" % "gdx" % "0.9.9-SNAPSHOT",
      "com.badlogicgames.gdx" % "gdx-tools" % "0.9.9-SNAPSHOT"
    )
  )

  lazy val desktop = common ++ assemblySettings ++ Seq(
    unmanagedResourceDirectories in Compile += file("common/assets"),
    fork in Compile := true,
    libraryDependencies ++= Seq(
      "com.badlogicgames.gdx" % "gdx-backend-lwjgl" % "0.9.9-SNAPSHOT",
      "com.badlogicgames.gdx" % "gdx-platform" % "0.9.9-SNAPSHOT" classifier "natives-desktop"
    )
  )

  

  lazy val assemblyOverrides = Seq(
    mainClass in assembly := Some("my.game.pkg.Main"),
    AssemblyKeys.jarName in assembly := "testgame-0.1.jar"
  )

  lazy val nativeExtractions = SettingKey[Seq[(String, NameFilter, File)]]("native-extractions", "(jar name partial, sbt.NameFilter of files to extract, destination directory)")
  lazy val extractNatives = TaskKey[Unit]("extract-natives", "Extracts native files")
  lazy val natives = Seq(
    ivyConfigurations += config("natives"),
    nativeExtractions := Seq.empty,
    extractNatives <<= (nativeExtractions, update) map { (ne, up) =>
      val jars = up.select(configurationFilter("natives"))
      ne foreach { case (jarName, fileFilter, outputPath) =>
        jars find(_.getName.contains(jarName)) map { jar =>
            IO.unzip(jar, outputPath, fileFilter)
        }
      }
    },
    compile in Compile <<= (compile in Compile) dependsOn (extractNatives)
  )
}

object LibgdxBuild extends Build {
  lazy val common = Project(
    "common",
    file("common"),
    settings = Settings.common)

  lazy val desktop = Project(
    "desktop",
    file("desktop"),
    settings = Settings.desktop)
    .dependsOn(common)
    .settings(Settings.assemblyOverrides: _*)



  lazy val all = Project(
    "all-platforms",
    file("."),
    settings = Settings.common
  ) aggregate(common, desktop)
}
