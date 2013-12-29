releaseSettings

scalaVersion := "2.10.3"

organization := "com.catamorphic"

name := "github-client"

libraryDependencies ++= Seq(
      "org.scalaz" % "scalaz-core_2.10" % "7.0.0",
      "org.scalaz" % "scalaz-concurrent_2.10" % "7.0.0",
      "com.typesafe.play" %% "play" % "2.2.1",
      "com.catamorphic" %% "playz" % "0.1",
      "com.damnhandy" % "handy-uri-templates" % "1.1.7"
    )

publishTo := Some("catamorphic-releases" at "http://catamorphic-repository.bitnamiapp.com/artifactory/releases")    

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Catamorphic Public" at "http://dl.bintray.com/catamorphic/public",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)
