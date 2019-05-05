
val ScalatraVersion = "2.6.5"

organization := "org.marekjeszka"

name := "BraczSearch"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.6"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0",
  "org.scalatra" %% "scalatra" % ScalatraVersion excludeAll(
    ExclusionRule("org.apache.commons", "commons-lang3"),
    ExclusionRule("org.slf4j", "slf4j-api")),
  "com.typesafe" % "config" % "1.3.4",
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
enablePlugins(LinuxPlugin)
