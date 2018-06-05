# sbt-big: faster and more stable sbt

This project is a proposal to create a new module in sbt/sbt called `sbt-big`
that is a fat jar with all sbt dependencies shaded under an internal namespace.
By updating the sbt launcher to use `org.scala-sbt:sbt-big` instead of
`org.scala-sbt:sbt`, we gain

- faster startup time, by making the launcher download 1 fat jar containing all
  dependencies instead of resolving and downloading 71 jars for dependencies.
  This speedup should be particularly noticable in clean CI and docker environments and from locations with high latency such as Asia or Australia.
- more stable binary compatibility, by shading sbt dependencies under the
  `sbt.internal.*` package so that sbt plugins don't conflict with the sbt boot
  classpath.
  See [sbt/zinc#546](https://github.com/sbt/zinc/issues/546) for an example issue.
  By using `sbt-big`, the sbt project is safe to upgrade to the latest versions of its
  dependencies without breaking the sbt plugin ecosystem.


To try out `sbt-big` locally,

```
git clone https://github.com/olafurpg/sbt-big.git
cd sbt-big
sbt publishM2
```

What happens during publish is that:

- sbt-assembly includes the full dependency tree of sbt
```
$ du -h $HOME/.m2/repository/org/scala-sbt/sbt-big/1.1.0/sbt-big-1.1.0.jar
 21M    /Users/ollie/.m2/repository/org/scala-sbt/sbt-big/1.1.0/sbt-big-1.1.0.jar
```

- the published artifact contains 0 dependencies
```
$ cat $HOME/.m2/repository/org/scala-sbt/sbt-big/1.1.0/sbt-big-1.1.0.pom/sbt-big-1.1.0.pom | grep dependencies
<dependencies>
  <!--the dependency that was here has been absorbed via sbt-assembly-->
  <!--the dependency that was here has been absorbed via sbt-assembly-->
</dependencies>
```

- the fastparse dependency has been shaded into the `sbt.internal.fastparse` package
```
$ jar tf $HOME/.m2/repository/org/scala-sbt/sbt-big/1.1.0/sbt-big-1.1.0.jar | grep fastparse | head
sbt/internal/fastparse/
sbt/internal/fastparse/core/
sbt/internal/fastparse/parsers/
sbt/internal/fastparse/utils/
...
```
