name := """akka-cake-pattern-testing"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.8",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.8" % "test",
  "org.specs2" %% "specs2" % "2.4.1" % "test",
  "org.mockito"          %   "mockito-core"   % "1.10.8" % "test"
)
