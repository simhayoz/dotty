# Scraping Example

## Using closures

```scala
import org.jsoup._
import collection.JavaConverters._
import java.io.IOException

object ScrapingDocs {
  import language.experimental.saferExceptions

  import colltest5.strawman.collections.*
  import CollectionStrawMan5.*

  def fromMutableBuffer[A](buf: scala.collection.mutable.Buffer[A]): List[A] =
    val l: scala.collection.Iterator[A] = buf.iterator
    var newList = List[A]()
    while (l.hasNext) {
      newList = Cons(l.next(), newList)
    }
    newList

  def main(args: Array[String]) = {
    val indexDoc: nodes.Document = Jsoup.connect("https://developer.mozilla.org/en-US/docs/Web/API").get()
    val links: List[nodes.Element] = fromMutableBuffer(indexDoc.select("h2#interfaces").nextAll.select("div.index a").asScala)
    val linkData: List[(String, String, String)] = links.map(link => (link.attr("href"), link.attr("title"), link.text))
    val closures: List[{*}Unit -> (String, String, String, String, List[(String, String)])] =
      linkData.map{case (url, tooltip, name) => _ => {
        println("Scraping " + name)
        val doc = Jsoup.connect("https://developer.mozilla.org" + url).get()
        val summary = doc.select("article#wikiArticle > p").asScala.headOption match {
          case Some(n) => n.text; case None => ""
        }
        val methodsAndProperties = fromMutableBuffer(doc
        .select("article#wikiArticle dl dt")
        .asScala)
        .map(el => (el.text, el.nextElementSibling match {case null => ""; case x => x.text}))
        (url, tooltip, name, summary, methodsAndProperties)
      }}
    for (closure <- closures) closure(())
  }
}
```

### Errors

- cannot directly use `Unit =>...`, had to use `{*}Unit -> ...` (even though it should be the case that `Unit =>...` === `{*}Unit -> ...`), otherwise it throws the following exception on compilation:
```console
java.lang.UnsupportedOperationException: derivedAnnotation(Tree)
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
...
```
- Had to use `closure(())` instead of `closure()`, otherwise, the compiler complains:
```console
-- Error: tests/run-custom-args/captures/colltest5/ScrapingDocs.scala:23:52 ----
23 |    val articles = for (closure <- closures) closure()
   |                                             ^^^^^^^^^
   |missing argument for parameter v1 of method apply in trait Function1: (v1: Unit): Unit
```

- When running with `-Xprint:cc`:
```console
scalac -Ycc -Xprint:cc -classpath jsoup-1.14.3.jar tests/run-custom-args/captures/colltest5/CollectionStrawManCC5_1.scala tests/run-custom-args/captures/colltest5/ScrapingDocs.scala
...
java.lang.AssertionError: NoDenotation.owner
	at dotty.tools.dotc.core.SymDenotations$NoDenotation$.owner(SymDenotations.scala:2502)
	at dotty.tools.dotc.core.SymDenotations$SymDenotation.copySymDenotation$default$2(SymDenotations.scala:1535)
	at dotty.tools.dotc.transform.Recheck$.updateInfoBetween(Recheck.scala:45)
	at dotty.tools.dotc.cc.Setup.traverse(Setup.scala:376)
	at dotty.tools.dotc.ast.Trees$Instance$TreeTraverser.apply(Trees.scala:1635)
	at dotty.tools.dotc.ast.Trees$Instance$TreeTraverser.apply(Trees.scala:1635)
	at dotty.tools.dotc.ast.Trees$Instance$TreeAccumulator.foldOver(Trees.scala:1605)
```

### Testing try/catch

1. Both
```scala
var closures: List[{*}Unit -> ...] = Nil
try {
  closures = urls.map(url => {() => ... JSoup.connect(url).get() ... })
} catch {case (e: IOException) => ...}
for(closure <- closures) closure()
```
and
```scala
val closures: List[{*}Unit -> ...] = try {
  urls.map(url => {() => ... JSoup.connect(url).get() ... })
} catch {case (e: IOException) => ...}
for(closure <- closures) closure()
```
should fail, but they don't.

2. Both
```scala
try {
  val closures: List[{*}Unit -> ...] = urls.map(url => {() => ... JSoup.connect(url).get() ... })
  for(closure <- closures) closure()
} catch {case (e: IOException) => ...}
```
and
```scala
val closures: List[{*}Unit -> ...] = urls.map(url => {() => ... JSoup.connect(url).get() ... })
try {
  for(closure <- closures) closure()
} catch {case (e: IOException) => ...}
```
compile correctly (and it should be the case)

## Using Future

```scala
import org.jsoup._
import collection.JavaConverters._
import java.io.IOException
import scala.util.{Failure, Success}
import scala.runtime.stdLibPatches.language.future
import scala.concurrent.duration._
import scala.concurrent._

object ScrapingDocs {
  import language.experimental.saferExceptions

  import scala.concurrent.ExecutionContext.Implicits.global

  import colltest5.strawman.collections.*
  import CollectionStrawMan5.*

  def fromMutableBuffer[A](buf: scala.collection.mutable.Buffer[A]): List[A] =
    val l: scala.collection.Iterator[A] = buf.iterator
    var newList = List[A]()
    while (l.hasNext) {
      newList = Cons(l.next(), newList)
    }
    newList

  def main(args: Array[String]) = {
    val indexDoc: nodes.Document = Jsoup.connect("https://developer.mozilla.org/en-US/docs/Web/API").get()
    val links: List[nodes.Element] = fromMutableBuffer(indexDoc.select("h2#interfaces").nextAll.select("div.index a").asScala)
    val linkData: List[(String, String, String)] = links.map(link => (link.attr("href"), link.attr("title"), link.text))
    val futures: List[Future[(String, String, String, String, List[(String, String)])]] =
      linkData.map{case (url, tooltip, name) => {
        Future {
          println("Scraping " + name)
          val doc = Jsoup.connect("https://developer.mozilla.org" + url).get()
          val summary = doc.select("article#wikiArticle > p").asScala.headOption match {
            case Some(n) => n.text; case None => ""
          }
          val methodsAndProperties = fromMutableBuffer(doc
          .select("article#wikiArticle dl dt")
          .asScala)
          .map(el => (el.text, el.nextElementSibling match {case null => ""; case x => x.text}))
          (url, tooltip, name, summary, methodsAndProperties)
        }
      }}
    for (future <- futures) Await.result(future, Duration(1000, MILLISECONDS))
  }
}
```

### Testing try/catch

1. Both
```scala
var futures: List[Future[...]] = Nil
try {
      future = linkData.map{case (url, tooltip, name) => {
        Future {...}}
    } catch {case (e: IOException) => ...}
    for (future <- futures) ...
```
and
```scala
val futures: List[Future[...]] = try {
  linkData.map{case (url, tooltip, name) => {
    Future {...}}
} catch {case (e: IOException) => ...}
for (future <- futures) ...
```
should fail, but they don't.

2. Both
```scala
try {
     val futures: List[Future[...]] =  linkData.map{case (url, tooltip, name) => {
        Future {...}}
	for (future <- futures) ...
} catch {case (e: IOException) => ...}
```
and
```scala
 val futures: List[Future[...]] =  linkData.map{case (url, tooltip, name) => {
    Future {...}}
try {
	for (future <- futures) ...
} catch {case (e: IOException) => ...}
```
compile correctly (and it should be the case)
