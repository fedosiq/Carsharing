name := "Carsharing"

version := "0.1"

scalaVersion := "2.13.3"

val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.0" % Test,

  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",

  "io.monix" %% "monix" % "3.3.0",

  "com.typesafe.akka" %% "akka-actor" % "2.6.10",
  "com.typesafe.akka" %% "akka-stream" % "2.6.10",
  "com.typesafe.akka" %% "akka-http" % "10.2.1",
  "de.heikoseeberger" %% "akka-http-circe" % "1.35.2"
)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

