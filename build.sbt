ThisBuild / resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)

name := "queryable-state-interactor"

version := "0.1-SNAPSHOT"

organization := "neoflex"

ThisBuild / scalaVersion := "2.12.11"

val flinkVersion = "1.10.0"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion
)

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies
  )

assembly / mainClass := Some("neoflex.QuaryableStateInteractor")

initialize := {
  val _ = initialize.value
  val required = "1.8"
  val current  = sys.props("java.specification.version")
  assert(current == required, s"Wrong JDK version: java.specification.version $current != $required")
}