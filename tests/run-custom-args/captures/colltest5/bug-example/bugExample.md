## Compiler version

3.1.3-RC1-bin-SNAPSHOT-nonbootstrapped-git-9154e81 on the `cc-experiment` branch with `-Ycc`

## Minimized code

<!--
This code should be self contained, compilable (with possible failures) and as small as possible.

Ideally, we should be able to just copy this code in a file and run `scalac` (and maybe `scala`) to reproduce the issue.
-->

```Scala
object BugExample {
  val urls: List[String] = List("any_url")
  val closures: List[Unit => Unit] =
    urls.map(url => _ => {
      println("Scraping " + url)
    })
  for (closure <- closures) closure(())
}
```

## Output (click arrow to expand)
<details>

Replacing `Unit => Unit` with `{*}Unit -> Unit` will compile without any crash, even though `A => B` should be equivalent to `{*}A -> B`.
It crashes with the following exception:

```scala
java.lang.UnsupportedOperationException: derivedAnnotation(Tree) while compiling .../BugExample.scala
	at dotty.tools.package$.unsupported(package.scala:25)
	at dotty.tools.dotc.cc.CaptureAnnotation.derivedAnnotation(CaptureAnnotation.scala:32)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transformAnnot(PostTyper.scala:138)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform(PostTyper.scala:413)
	at dotty.tools.dotc.ast.Trees$Instance$TreeMap.transform(Trees.scala:1455)
	at dotty.tools.dotc.transform.MacroTransform$Transformer.transform(MacroTransform.scala:40)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform$$anonfun$5(PostTyper.scala:364)
	at dotty.tools.dotc.transform.SuperAccessors.wrapDefDef(SuperAccessors.scala:223)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform(PostTyper.scala:364)
	at dotty.tools.dotc.ast.tpd$TreeMapWithPreciseStatContexts.loop$2(tpd.scala:1206)
	at dotty.tools.dotc.ast.tpd$TreeMapWithPreciseStatContexts.transformStats(tpd.scala:1206)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transformStats(PostTyper.scala:462)
	at dotty.tools.dotc.ast.tpd$TreeMapWithPreciseStatContexts.transformBlock(tpd.scala:1211)
	at dotty.tools.dotc.ast.Trees$Instance$TreeMap.transform(Trees.scala:1396)
	at dotty.tools.dotc.transform.MacroTransform$Transformer.transform(MacroTransform.scala:49)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform(PostTyper.scala:453)
	at dotty.tools.dotc.ast.Trees$Instance$TreeMap.transform$$anonfun$1(Trees.scala:1488)
	at scala.collection.immutable.List.mapConserve(List.scala:472)
	at dotty.tools.dotc.ast.Trees$Instance$TreeMap.transform(Trees.scala:1488)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.app1$1(PostTyper.scala:305)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform(PostTyper.scala:319)
	at dotty.tools.dotc.ast.Trees$Instance$TreeMap.transform(Trees.scala:1450)
	at dotty.tools.dotc.transform.MacroTransform$Transformer.transform(MacroTransform.scala:40)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform(PostTyper.scala:359)
	at dotty.tools.dotc.ast.tpd$TreeMapWithPreciseStatContexts.loop$2(tpd.scala:1206)
	at dotty.tools.dotc.ast.tpd$TreeMapWithPreciseStatContexts.transformStats(tpd.scala:1206)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transformStats(PostTyper.scala:462)
	at dotty.tools.dotc.ast.tpd$TreeMapWithPreciseStatContexts.transformStats(tpd.scala:1208)
	at dotty.tools.dotc.transform.MacroTransform$Transformer.transform(MacroTransform.scala:47)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform$$anonfun$4$$anonfun$1(PostTyper.scala:353)
	at dotty.tools.dotc.transform.SuperAccessors.wrapTemplate(SuperAccessors.scala:208)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform$$anonfun$4(PostTyper.scala:353)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.withNoCheckNews(PostTyper.scala:100)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform(PostTyper.scala:355)
	at dotty.tools.dotc.ast.Trees$Instance$TreeMap.transform(Trees.scala:1459)
	at dotty.tools.dotc.transform.MacroTransform$Transformer.transform(MacroTransform.scala:40)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform(PostTyper.scala:388)
	at dotty.tools.dotc.ast.tpd$TreeMapWithPreciseStatContexts.loop$2(tpd.scala:1206)
	at dotty.tools.dotc.ast.tpd$TreeMapWithPreciseStatContexts.transformStats(tpd.scala:1206)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transformStats(PostTyper.scala:462)
	at dotty.tools.dotc.ast.tpd$TreeMapWithPreciseStatContexts.transformStats(tpd.scala:1208)
	at dotty.tools.dotc.ast.Trees$Instance$TreeMap.transform(Trees.scala:1470)
	at dotty.tools.dotc.transform.MacroTransform$Transformer.transform(MacroTransform.scala:40)
	at dotty.tools.dotc.transform.PostTyper$PostTyperTransformer.transform(PostTyper.scala:453)
	at dotty.tools.dotc.transform.MacroTransform.run(MacroTransform.scala:18)
	at dotty.tools.dotc.core.Phases$Phase.runOn$$anonfun$1(Phases.scala:319)
	at scala.collection.immutable.List.map(List.scala:246)
	at dotty.tools.dotc.core.Phases$Phase.runOn(Phases.scala:320)
	at dotty.tools.dotc.Run.runPhases$1$$anonfun$1(Run.scala:224)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	at scala.collection.ArrayOps$.foreach$extension(ArrayOps.scala:1328)
	at dotty.tools.dotc.Run.runPhases$1(Run.scala:235)
	at dotty.tools.dotc.Run.compileUnits$$anonfun$1(Run.scala:243)
	at scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	at dotty.tools.dotc.util.Stats$.maybeMonitored(Stats.scala:68)
	at dotty.tools.dotc.Run.compileUnits(Run.scala:252)
	at dotty.tools.dotc.Run.compileSources(Run.scala:185)
	at dotty.tools.dotc.Run.compile(Run.scala:169)
	at dotty.tools.dotc.Driver.doCompile(Driver.scala:35)
	at dotty.tools.dotc.Driver.process(Driver.scala:195)
	at dotty.tools.dotc.Driver.process(Driver.scala:163)
	at dotty.tools.dotc.Driver.process(Driver.scala:175)
	at dotty.tools.dotc.Driver.main(Driver.scala:205)
	at dotty.tools.dotc.Main.main(Main.scala)
```



</details>