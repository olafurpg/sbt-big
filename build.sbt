import scala.xml.{Node => XmlNode, NodeSeq => XmlNodeSeq, _}
import scala.xml.transform.{RewriteRule, RuleTransformer}

lazy val Version = "1.1.6"

organization := "com.geirsson"
moduleName := "sbt"
version := Version
crossVersion := CrossVersion.disabled

libraryDependencies += "org.scala-sbt" % "sbt" % Version

// Shade settings
assemblyShadeRules.in(assembly) := Seq(
  ShadeRule.rename("fastparse.**" -> "sbt.internal.fastparse.@1").inAll
)
artifact.in(Compile, packageBin) := artifact.in(Compile, assembly).value
assemblyOption.in(assembly) ~= { _.copy(includeScala = false) }
addArtifact(artifact.in(Compile, packageBin), assembly)
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

// Publish Settings
homepage := Some(url("https://github.com/olafurpg/sbt-big"))
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
publishTo := Some(Opts.resolver.sonatypeStaging)
developers := List(
  Developer(
    "olafurpg",
    "Ólafur Páll Geirsson",
    "olafurpg@users.noreply.github.com",
    url("https://geirsson.com")
  )
)
