import scala.collection.mutable

val queue = new mutable.Queue[Long]()

val value = queue :+ 1 :+ 2

println(queue)
println(value)
println(value.dequeue())
println(value)
