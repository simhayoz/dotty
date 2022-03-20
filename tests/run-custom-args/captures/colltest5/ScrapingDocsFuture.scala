import org.jsoup._
import collection.JavaConverters._
import java.io.IOException
import scala.util.{Failure, Success}
import scala.runtime.stdLibPatches.language.future
import scala.concurrent.duration._
import scala.concurrent._

object ScrapingDocsFuture {
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
    val futures: List[Future[(String, String, String, String, List[(String, String)])]] = linkData.map{case (url, tooltip, name) => {
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
