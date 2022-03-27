object TestRequests {
  def main(args: Array[String]) =
    val r = requests.get("https://api.github.com/users/simhayoz")
    println(r.statusCode)
    println(r.headers("content-type"))
    println(r.text())
}