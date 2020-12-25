enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)

name := "Carsharing"

version := "0.1"

scalaVersion := "2.13.3"

//scalacOptions ++= Seq(
//  "-deprecation",
//  "-encoding", "UTF-8",
//  "-language:experimental.macros",
//  "-feature",
//  "-unchecked",
//  "-Xfatal-warnings",
//  "-Xlint",
//  "-Ywarn-numeric-widen",
//  "-Ywarn-value-discard",
//  "-Ywarn-dead-code", // Warn when dead code is identified.
//  "-Ywarn-extra-implicit"
//)

val circeVersion = "0.12.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.0" % Test,

  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",

  "io.monix" %% "monix" % "3.3.0",
  "com.rms.miu" %% "slick-cats" % "0.10.2",

  "com.typesafe.akka" %% "akka-actor" % "2.6.10",
  "com.typesafe.akka" %% "akka-stream" % "2.6.10",
  "com.typesafe.akka" %% "akka-http" % "10.2.1",
  "de.heikoseeberger" %% "akka-http-circe" % "1.35.2",

  "com.beachape" %% "enumeratum" % "1.6.1",
  "com.beachape" %% "enumeratum-circe" % "1.6.1"

)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-jackson210"
).map(_ % circeVersion)

mainClass in (Compile, run) := Some("com.fedosique.carsharing.CarsharingHttpApp")
