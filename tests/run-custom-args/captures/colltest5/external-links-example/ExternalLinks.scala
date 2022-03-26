// scalac -Ycc -classpath jsoup-1.14.3.jar tests/run-custom-args/captures/colltest5/CollectionStrawManCC5_1.scala tests/run-custom-args/captures/colltest5/external-links-example/ExternalLinks.scala
import org.jsoup._
import collection.JavaConverters._
import language.experimental.saferExceptions

object ExternalLinks {

  import colltest5.strawman.collections.*
  import CollectionStrawMan5.*

  def fromMutableBuffer[A](buf: scala.collection.mutable.Buffer[A]): ListBuffer[A] =
    val l: scala.collection.Iterator[A] = buf.iterator
    val newList = ListBuffer[A]()
    while (l.hasNext) {
      newList += l.next()
    }
    newList

  extension [A](l: ListBuffer[A])
    def contains[A1 >: A](elem: A1): Boolean =
      val these: Iterator[A] = l.iterator
      while (these.hasNext) {
        if (these.next() == elem) return true
      }
      false

    // Simpler implementation of `withFilter`
    def withFilter(p: A => Boolean): ListBuffer[A] =
      val these: Iterator[A] = l.iterator
      val newList = ListBuffer[A]()
      while (these.hasNext) {
        val curr = these.next()
        if (p(curr)) {
          newList += curr
        }
      }
      newList

  def main(args: Array[String]) = {
    val start = "https://www.lihaoyi.com/"
    val seen: ListBuffer[String] = ListBuffer(start)
    val queue: ArrayBuffer[String] = ArrayBuffer(start)

    while(!queue.isEmpty){
      val current = queue(0)
      queue.trimStart(1)

      println("Crawling " + current)
      val docOpt =
        try Some(Jsoup.connect(current).get())
        catch{case e: org.jsoup.HttpStatusException => None}

      docOpt match{
        case None =>
        case Some(doc) =>
          val allLinks: ListBuffer[String] = fromMutableBuffer(doc.select("a").asScala).map(_.attr("href"))
          for(link <- allLinks if !link.startsWith("#")) {
            // ignore hash query fragment in URL
            val newUri = new java.net.URI(current).resolve(link.takeWhile(_ != '#')).normalize()
            val normalizedLink = newUri.toString
            val seenContains = seen.contains(normalizedLink)
            if (normalizedLink.startsWith(start) &&
                !seenContains &&
                link.endsWith(".html")){
              queue += normalizedLink
            }
            if (!seenContains) {
              seen += normalizedLink
            }
          }
      }
    }

    // println(seen)
    println(seen.length)
  }
}