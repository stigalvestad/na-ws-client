import com.github.retronym.SbtOneJar._

name := "TryConfig"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/"
)

oneJarSettings

libraryDependencies += "com.typesafe" % "config" % "1.3.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.9"
libraryDependencies += "net.ceedubs" %% "ficus" % "1.1.2"
libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
libraryDependencies += "org.scala-lang.modules" % "scala-async_2.11" % "0.9.6-RC2"
libraryDependencies += "org.apache.commons" % "commons-email" % "1.4"