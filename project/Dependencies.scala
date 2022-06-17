import DependencyVersions._
import sbt._

object Dependencies {
  val slf4j = Seq(
    "org.slf4j" % "slf4j-api" % slf4j_version,
    "org.slf4j" % "slf4j-simple" % slf4j_version,
    "org.clapper" %% "grizzled-slf4j" % "1.3.4"
  )

  val dependencyInjection = Seq(
    "org.springframework" % "spring-context" % spring_version,
    "javax.inject" % "javax.inject" % inject_version,
    "javax.inject" % "javax.inject-tck" % inject_version
  )

  val guice = Seq("com.google.inject" % "guice" % "4.1.0")

  val jackson = Seq(
    "com.fasterxml.jackson.core" % "jackson-core" % jackson_version,
    "com.fasterxml.jackson.core" % "jackson-databind" % jackson_version
  )

  val yamlSnake = Seq("org.yaml" % "snakeyaml" % "1.26")

  val antlr = Seq("org.antlr" % "antlr4-runtime" % "4.3")
  val stringTemplate4 = Seq("org.antlr" % "ST4" % "4.3")
  val velocity = Seq("org.apache.velocity" % "velocity-engine-core" % "2.3")

  // TEST
  val testLibs = Seq(
    "org.scalatest" %% "scalatest" % "3.2.7" % Test
  )
}
