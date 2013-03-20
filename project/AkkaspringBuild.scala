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
      libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.1",
      libraryDependencies += "org.springframework" % "spring-context" % "3.2.2.RELEASE",
      // only needed when @Configured is used
      libraryDependencies += "org.springframework" % "spring-aspects" % "3.2.2.RELEASE",
      // only needed when @Configured is used together with javaagent
      libraryDependencies += "org.springframework" % "spring-instrument" % "3.2.2.RELEASE",
      libraryDependencies += "javax.inject" % "javax.inject" % "1",
      Keys.fork in run := true,
      javaOptions in run <++= (update) map { (u) =>
          val f = u.matching(configurationFilter("compile") && moduleFilter(name = "spring-instrument")).head
          Seq("-javaagent:" + f.getAbsolutePath)
        }

    )
  )
}
