name := """style-web"""

version := "0.2.4"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  filters
)

libraryDependencies += "com.squareup.okhttp" % "okhttp" % "2.7.2"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.4" withSources() withJavadoc()

libraryDependencies += "com.github.mumoshu" %% "play2-memcached-play24" % "0.7.0" withSources() withJavadoc()

resolvers ++= Seq(
  "Apache" at "https://repo1.maven.org/maven2/"
)
javacOptions += "-Xlint:deprecation"
javacOptions += "-Xlint:unchecked"

routesGenerator := InjectedRoutesGenerator
