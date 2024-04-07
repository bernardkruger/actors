scalaVersion := "3.4.0"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.0.21",
  "dev.zio" %% "zio-streams" % "2.0.21",  
)

fork := true