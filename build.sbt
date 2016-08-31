name := """style-web"""

version := "0.4.37"

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

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1" withSources() withJavadoc()

libraryDependencies += "com.typesafe.akka" % "akka-kernel_2.11" % "2.4.1" withSources() withJavadoc()

libraryDependencies += "com.typesafe.akka" % "akka-slf4j_2.11" % "2.4.1" withSources() withJavadoc()

libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.4.1" withSources() withJavadoc()

libraryDependencies += "redis.clients" % "jedis" % "2.8.1"


resolvers ++= Seq(
  "Apache" at "https://repo1.maven.org/maven2/"
)
javacOptions += "-Xlint:deprecation"
javacOptions += "-Xlint:unchecked"

routesGenerator := InjectedRoutesGenerator
