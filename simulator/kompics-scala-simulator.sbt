name := "Kompics-Scala-Simulator"

organization := "se.sics.kompics"

version := "0.9.4-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation","-feature")


resolvers += Resolver.mavenLocal
resolvers += "Kompics Releases" at "http://kompics.sics.se/maven/repository/"
resolvers += "Kompics Snapshots" at "http://kompics.sics.se/maven/snapshotrepository/"

libraryDependencies += "se.sics.kompics" %% "kompics-scala" % "0.9.4-SNAPSHOT"
libraryDependencies += "se.sics.kompics.simulator" % "core" % "0.9.4-SNAPSHOT"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.3"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % "test"

parallelExecution in Test := false

publishMavenStyle := true
publishTo := {
  val name = "Kompics-Repository"
  val url = "kompics.i.sics.se"
  val prefix = "/home/maven/"
  import java.io.File
  val privateKeyFile: File = new File(sys.env("HOME") + "/.ssh/id_rsa")
  if (isSnapshot.value)
    Some(Resolver.ssh(name, url, prefix + "snapshotrepository") as("root", privateKeyFile) withPermissions("0644")) 
  else
    Some(Resolver.ssh(name, url, prefix + "repository") as("root", privateKeyFile) withPermissions("0644")) 
}
resolvers ++= Seq(
    {
        import java.io.File
        val privateKeyFile: File = new File(sys.env("HOME") + "/.ssh/id_rsa")
        Resolver.ssh("Kompics Repository", "kompics.i.sics.se") as("root", privateKeyFile) withPermissions("0644")
    }
)