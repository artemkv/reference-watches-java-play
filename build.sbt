name := """reference-watches-java-play"""
organization := "net.artemkv"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies ++= Seq(
  javaJdbc,
  javaJpa,
  "org.hibernate" % "hibernate-core" % "5.4.2.Final",
  "com.h2database" % "h2" % "1.4.192"
)

PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"