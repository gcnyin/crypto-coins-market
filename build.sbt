name := """crypto-coins-market"""
organization := "com.github.gcnyin"
maintainer := "guchounongyin@gmail.com"

version := "0.1.0"
scalaVersion := "2.13.6"

lazy val root = (project in file("."))
  .settings(
    name := "play-seed",
    libraryDependencies ++= Seq(
      guice,
      ws,
      caffeine,
      "com.typesafe.play" %% "play-slick" % "5.0.0",
      "org.flywaydb" %% "flyway-play" % "7.10.0",
      "org.springframework.security" % "spring-security-crypto" % "5.5.0",
      "org.postgresql" % "postgresql" % "42.2.21" % Runtime,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
    ),
    scalaVersion := "2.13.6")
  .enablePlugins(PlayScala)
  .disablePlugins(PlayFilters)

import com.typesafe.sbt.packager.docker.{DockerChmodType, DockerPermissionStrategy}

dockerBaseImage := "openjdk:11-jdk"
dockerChmodType := DockerChmodType.UserGroupWriteExecute
dockerPermissionStrategy := DockerPermissionStrategy.CopyChown
dockerExposedPorts ++= Seq(9000)
dockerUpdateLatest := true
