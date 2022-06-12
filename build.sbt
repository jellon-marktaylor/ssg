ThisBuild / scalaVersion := "2.13.8"

ThisBuild / organization := "jellon"

ThisBuild / version := "0.2.0-SNAPSHOT"

enablePlugins(Antlr4Plugin)

mainClass in (Compile, packageBin) := Some("jellon.ssg.engine.flagship.GuiceApp")
mainClass in (Compile, run) := Some("jellon.ssg.engine.flagship.GuiceApp")

lazy val root = (project in file("."))
  .aggregate(ioApi, ioImpl, ioTest, nodeImpl, nodeTest, parserApi, parserImpl, parserTest, engineFlagshipApi, engineFlagshipImpl, engineFlagshipTest, engineFlagshipAppGuice, engineFlagshipAppSpring)
  .settings(
    name := "ssg"
  )



lazy val ioApi = (project in file("io/api"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "io-api",
    libraryDependencies ++= Dependencies.slf4j,
  )

lazy val ioSpi = (project in file("io/spi"))
  .dependsOn(ioApi)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "io-spi",
    libraryDependencies ++= Dependencies.slf4j,
  )

lazy val ioImpl = (project in file("io/impl"))
  .dependsOn(ioSpi)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "io-impl",
    libraryDependencies ++= Dependencies.dependencyInjection
      ++ Dependencies.slf4j,
  )

lazy val ioTest = (project in file("io/test"))
  .dependsOn(ioApi, ioSpi, ioImpl)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "io-test",
    libraryDependencies ++= Dependencies.testLibs,
  )



lazy val nodeImpl = (project in file("node/spi"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "node-spi",
    libraryDependencies ++= Dependencies.slf4j,
  )

lazy val nodeTest = (project in file("node/test"))
  .enablePlugins(JavaAppPackaging)
  .dependsOn(nodeImpl)
  .settings(
    name := "node-test",
    libraryDependencies ++= Dependencies.slf4j
      ++ Dependencies.testLibs,
  )



lazy val parserApi = (project in file("node/parser/api"))
  .dependsOn(nodeImpl)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "node-parser-api",
    libraryDependencies ++= Dependencies.slf4j,
  )

lazy val parserImpl = (project in file("node/parser/impl"))
  .dependsOn(nodeImpl, ioSpi, parserApi)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "node-parser-impl",
    libraryDependencies ++= Dependencies.dependencyInjection
      ++ Dependencies.jackson
      ++ Dependencies.yamlSnake
      ++ Dependencies.slf4j,
  )

lazy val parserTest = (project in file("node/parser/test"))
  .dependsOn(parserImpl, ioImpl % "test->compile")
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "node-parser-test",
    libraryDependencies ++= Dependencies.dependencyInjection
      ++ Dependencies.jackson
      ++ Dependencies.yamlSnake
      ++ Dependencies.slf4j
      ++ Dependencies.testLibs,
  )



lazy val engineFlagshipApi = (project in file("engine/flagship/api"))
  .dependsOn(nodeImpl, ioSpi)
  .settings(
    name := "engine-flagship-api",
    libraryDependencies ++= Dependencies.dependencyInjection
      ++ Dependencies.slf4j,
  )

lazy val engineFlagshipImpl = (project in file("engine/flagship/impl"))
  .dependsOn(nodeImpl, ioSpi, ioImpl, engineFlagshipApi)
  .settings(
    name := "engine-flagship-impl",
    libraryDependencies ++= Dependencies.dependencyInjection
      ++ Dependencies.antlr
      ++ Dependencies.stringTemplate4
      ++ Dependencies.slf4j,
  )

lazy val engineFlagshipAppGuice = (project in file("engine/flagship/guice"))
  .dependsOn(ioSpi, ioImpl, nodeImpl, parserApi, parserImpl, engineFlagshipApi, engineFlagshipImpl)
  .settings(
    name := "engine-flagship-app-guice",
    libraryDependencies ++= Dependencies.dependencyInjection
      ++ Dependencies.jackson
      ++ Dependencies.yamlSnake
      ++ Dependencies.stringTemplate4
      ++ Dependencies.guice
      ++ Dependencies.slf4j
      ++ Dependencies.testLibs,
  )

lazy val engineFlagshipAppSpring = (project in file("engine/flagship/spring"))
  .dependsOn(ioSpi, ioImpl, nodeImpl, parserApi, parserImpl, engineFlagshipApi, engineFlagshipImpl)
  .settings(
    name := "engine-flagship-app-spring",
    libraryDependencies ++= Dependencies.dependencyInjection
      ++ Dependencies.jackson
      ++ Dependencies.yamlSnake
      ++ Dependencies.stringTemplate4
      ++ Dependencies.guice
      ++ Dependencies.slf4j
      ++ Dependencies.testLibs,
  )

lazy val engineFlagshipTest = (project in file("engine/flagship/test"))
  .dependsOn(ioTest, nodeImpl, parserImpl, engineFlagshipImpl, engineFlagshipAppGuice)
  .settings(
    name := "engine-flagship-test",
    libraryDependencies ++= Dependencies.dependencyInjection
      ++ Dependencies.antlr
      ++ Dependencies.stringTemplate4
      ++ Dependencies.slf4j
      ++ Dependencies.testLibs,
  )

lazy val engineFlagship = (project in file("engine/flagship"))
  .aggregate(engineFlagshipApi, engineFlagshipImpl, engineFlagshipAppGuice, engineFlagshipAppSpring, engineFlagshipTest)
  .settings(
    name := "engine-flagship"
  )
