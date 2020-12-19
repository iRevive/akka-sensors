import sbt._
import Keys._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import xerial.sbt.Sonatype.SonatypeKeys._

object Publish {

  val SuppressJavaDocsAndSources = Seq(
    sources in doc := Seq(),
    publishArtifact in packageDoc := false,
    publishArtifact in packageSrc := false
  )

  protected val nexus = "https://oss.sonatype.org/"
  protected val ossStaging = "Sonatype OSS Staging" at nexus + "service/local/staging/deploy/maven2/"

  val ReleaseToSonatype = Seq(
    credentials ++= Seq(
      Credentials(
      "Sonatype Nexus Repository Manager",
      "oss.sonatype.org",
      sys.env.getOrElse("USERNAME", ""),
      sys.env.getOrElse("PASSWORD", "")
    ),
      Credentials(
        "GnuPG Key ID",
        "gpg",
        "303489A85EBB77F6E93E2A254CCF1479F92AE2B7", // key identifier
        "ignored" // this field is ignored; passwords are supplied by pinentry
      )
    ),
    sonatypeProfileName := "nl.pragmasoft",
    licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    homepage := Some(url("https://github.com/jacum/akka-sensors")),
    scmInfo := Some(ScmInfo(
      browseUrl = url("https://github.com/jacum/akka-sensors"),
      connection = "scm:git@github.com:jacum/akka-sensors.git")),
    pomExtra := (
      <developers>
        <developer>
          <id>PragmaSoft</id>
          <name>PragmaSoft</name>
        </developer>
      </developers>
    ),
    publishMavenStyle := true,
    publishTo := version(_ => Some(ossStaging)).value,
    publishArtifact in Test := false,
    publishArtifact in packageDoc := true,
    publishArtifact in packageSrc := true,
    pomIncludeRepository := (_ => false),
    releaseCrossBuild := true,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
//      setReleaseVersion,
//      commitReleaseVersion,
//      tagRelease,
      releaseStepCommandAndRemaining("+publishSigned"),
//      setNextVersion,
//     commitNextVersion,
      releaseStepCommand("sonatypeReleaseAll")
//      pushChanges
    )
  )

  val settings =
    if ( sys.env.contains("USERNAME")) {
      println(s"Releasing to Sonatype as ${sys.env.contains("USERNAME")}")
      ReleaseToSonatype
    }
    else SuppressJavaDocsAndSources

}