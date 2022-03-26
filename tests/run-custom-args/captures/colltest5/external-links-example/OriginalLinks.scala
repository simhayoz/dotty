import org.jsoup._
import collection.JavaConverters._

object OriginalLinks {

  def main(args: Array[String]) = {
    val start = "https://www.lihaoyi.com/"
    val seen = collection.mutable.Set(start)
    val queue = collection.mutable.ArrayDeque(start)

    while(queue.nonEmpty){
      val current = queue.removeHead()
      println("Crawling " + current)
      val docOpt =
        try Some(Jsoup.connect(current).get())
        catch{case e: org.jsoup.HttpStatusException => None}

      docOpt match{
        case None =>
        case Some(doc) =>
          val allLinks = doc.select("a").asScala.map(_.attr("href"))
          for(link <- allLinks if !link.startsWith("#")){
            // ignore hash query fragment in URL
            val newUri = new java.net.URI(current).resolve(link.takeWhile(_ != '#')).normalize()
            val normalizedLink = newUri.toString
            if (normalizedLink.startsWith(start) &&
                !seen.contains(normalizedLink) &&
                link.endsWith(".html")){
              queue.append(normalizedLink)
            }
            seen.add(normalizedLink)
          }
      }
    }

    // println(seen)
    println(seen.size)
  }
}