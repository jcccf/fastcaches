name := "fastcaches"

version := "0.1"

scalaVersion := "2.8.1"

retrieveManaged := true

resolvers += "Twitter Maven Repo" at "http://maven.twttr.com"

libraryDependencies += "com.twitter" % "ostrich" % "4.8.0"

libraryDependencies += "org.scala-tools.testing" % "specs_2.8.1" % "1.6.6" % "test" withSources()

libraryDependencies += "com.google.guava" % "guava" % "13.0.1" withSources()

libraryDependencies += "com.google.code.findbugs" % "jsr305" % "1.3.+"
