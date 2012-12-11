package com.clr3.fastcaches.util

object SequenceGeneration {

  def randomSequence(n: Int, range: Int) = Timer.time("Random Sequence Generation") {
    val sequence = new Array[Int](n)
    for (j <- 0 to sequence.length - 1) {
      sequence(j) = IntShardsReader.rnd.nextInt(range - 1) + 1
    }
    sequence
  }

  def powerSequence(n: Int, pow: Double) = Timer.time("Power Sequence Generation") {
    scala.util.Random.shuffle(
      (1 to n).foldLeft(Seq[Int]()){ case(arr, x) =>
        arr ++ (0 to math.pow(x, pow).toInt).map { _ => x }
      }
    )
  }

  def repeatSequence(n: Int, numNodes: Int, range: Int) = Timer.time("Repeat Sequence Generation") {
    (1 to numNodes).foldLeft(Seq[Int]()) { case(arr, x) =>
      val arr2 = (1 to n).map { i =>
        if (i % 2 == 0) {
          x
        } else {
          scala.util.Random.nextInt(range - 1) + 1
        }
      }
      arr ++ arr2
    }
  }

}
