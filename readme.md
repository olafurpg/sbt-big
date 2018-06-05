# sbt-big - faster and more stable sbt

This project is a proposal to create a new module in sbt/sbt called `sbt-big`
that is a fat jar with all sbt dependencies shaded under an internal namespace.
By updating the sbt launcher to use `org.scala-sbt:sbt-big` instead of
`org.scala-sbt:sbt`, we gain better

- startup time, by making the launcher download 1 fat jar containing all
  dependencies instead of resolving and downloading 71 jars for dependencies.
  In clean CI and docker environments, this should significantly speed up sbt
  launch times.
- binary compatibility stability, by shading sbt dependencies under the
  `sbt.internal.*` package so that sbt plugins don't conflict with the sbt boot
  classpath, see https://github.com/sbt/zinc/issues/546.
  By using `sbt-big`, sbt can upgrade to the latest versions of its
  dependencies without breaking the sbt plugin ecosystem.


