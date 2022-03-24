object BugExample {
  import colltest5.strawman.collections.*
  import CollectionStrawMan5.*

  def main(args: Array[String]) = {
    val urls: List[String] = Cons(("any_url"), Nil)
    val closures: List[Unit => Unit] =
      urls.map(url => _ => {
        println("Scraping " + url)
      })
    for (closure <- closures) closure(())
  }
}
