object Example {

  import colltest5.strawman.collections.*
  import CollectionStrawMan5.*

  def main(args: Array[String]) = {   
    // Strange bug
    var queue: Iterator[String] = Iterator(start)
    println(queue.getClass())
    var i = 0
    while(queue.hasNext){
      val current = queue.next()
      println(current)
      queue ++= Iterator("test")
      queue ++= Iterator("test")
      i += 1
    }
  }
}
