releaseSettings

scalaVersion := "2.10.3"

organization := "com.catamorphic"

name := "github-client"

libraryDependencies ++= Seq(
      "org.scalaz" % "scalaz-core_2.10" % "7.0.0",
      "org.scalaz" % "scalaz-concurrent_2.10" % "7.0.0",
      "com.typesafe.play" %% "play" % "2.2.1",
      "com.catamorphic" %% "playz" % "0.2",
      "com.damnhandy" % "handy-uri-templates" % "1.1.7"
    )

publishTo := Some("catamorphic-releases" at "http://catamorphic-repository.bitnamiapp.com/artifactory/releases")    

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Catamorphic Public" at "http://dl.bintray.com/catamorphic/public",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

initialCommands in console := 
"""
import com.catamorphic.external.github._
import scalaz._, Scalaz._
import com.catamorphic.playz._
import scala.concurrent._, duration._ 
import ExecutionContext.Implicits.global
"""