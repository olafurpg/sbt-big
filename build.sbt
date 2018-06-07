import scala.xml.{Node => XmlNode, NodeSeq => XmlNodeSeq, _}
import scala.xml.transform.{RewriteRule, RuleTransformer}

lazy val Version = "1.1.1"

organization := "com.geirsson"
moduleName := "sbt-big"
version := Version
crossVersion := CrossVersion.disabled

libraryDependencies += "org.scala-sbt" % "sbt" % Version

assemblyShadeRules.in(assembly) := Seq(
  ShadeRule.rename("fastparse.**" -> "sbt.internal.fastparse.@1").inAll
)
assemblyJarName.in(assembly) :=
  name.value + "_" + scalaVersion.value + "-" + version.value + "-assembly.jar"

assemblyOption.in(assembly) ~= { _.copy(includeScala = false) }

Keys.`package`.in(Compile) := {
  val slimJar = Keys.`package`.in(Compile).value
  val fatJar =
    new File(crossTarget.value + "/" + assemblyJarName.in(assembly).value)
  val _ = assembly.value
  IO.copy(
    List(fatJar -> slimJar),
    overwrite = true,
    preserveLastModified = false,
    preserveExecutable = false
  )
  slimJar
}

packagedArtifact.in(Compile).in(packageBin) := {
  val temp = packagedArtifact.in(Compile).in(packageBin).value
  val (art, slimJar) = temp
  val fatJar =
    new File(crossTarget.value + "/" + assemblyJarName.in(assembly).value)
  val _ = assembly.value
  IO.copy(
    List(fatJar -> slimJar),
    overwrite = true,
    preserveLastModified = false,
    preserveExecutable = false
  )
  (art, slimJar)
}

pomPostProcess := { node =>
  new RuleTransformer(new RewriteRule {
    override def transform(node: XmlNode): XmlNodeSeq = node match {
      case e: Elem if node.label == "dependency" =>
        Comment(
          "the dependency that was here has been absorbed via sbt-assembly"
        )
      case _ => node
    }
  }).transform(node).head
}

homepage := Some(url("https://github.com/scalameta/metadoc"))
publishTo := Some {
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
}
licenses := Seq(
  "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
)
apiURL := Some(url("https://github.com/olafurpg/sbt-big"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/olafurpg/sbt-big"),
    "scm:git:git@github.com:olafurpg/sbt-big.git"
  )
)

publishTo := Some {
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
}
developers := List(
  Developer(
    "olafurpg",
    "Ólafur Páll Geirsson",
    "olafurpg@users.noreply.github.com",
    url("https://geirsson.com")
  )
)
