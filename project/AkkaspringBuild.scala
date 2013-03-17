import sbt._
import sbt.Keys._

object AkkaspringBuild extends Build {

  lazy val akkaspring = Project(
    id = "akka-spring",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "akka-spring",
      organization := "org.typesafe",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.1"
    )
  )
}
