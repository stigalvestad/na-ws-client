import com.github.retronym.SbtOneJar._

name := "TryConfig"

version := "1.0"

scalaVersion := "2.11.7"


oneJarSettings

libraryDependencies += "com.typesafe" % "config" % "1.3.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.9"