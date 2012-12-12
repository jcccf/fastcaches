package com.clr3.fastcaches.util

object SequenceGeneration {

  /**
   * Generate a random sequence of integers from 1 to range
   * @param n Length of sequence
   * @param range Upper limit
   * @return
   */
  def randomSequence(n: Int, range: Int) = { //Timer.time("Random Sequence Generation") {
    (0 to n).map { _ =>
      IntShardsReader.rnd.nextInt(range - 1) + 1
    }
  }

  /**
   * Sample from a power law distribution
   * @param y Uniform variate \in [0, 1]
   * @param n Power
   * @param x0 Minimum x
   * @param x1 Maximum x
   * @return
   */
  def powerLawDist(y: Double, n: Double, x0: Double, x1: Double) =
    math.pow(math.pow(math.pow(x1, n+1) - math.pow(x0, n+1), y) + math.pow(x0, n+1), 1.0/(n+1))

  /**
   * Generate a power-law distributed random sequence of integers
   * @param n Maximum value of x, where the frequency of x is x**pow
   * @param pow Alpha of the power law distribution
   * @return
   */
  def powerSequence(n: Int, pow: Double, range: Int) = { //Timer.time("Power Sequence Generation") {

    (1 to n).map { i =>
      math.round(powerLawDist(scala.util.Random.nextDouble(), pow, 1, range - 1)).toInt
    }
//    scala.util.Random.shuffle(
//      (1 to n).foldLeft(Seq[Int]()){ case(arr, x) =>
//        arr ++ (0 to math.pow(x, pow).toInt).map { _ => x }
//      }
//    )
  }

  /**
   * Generate a repeated sequence of integers (ex. for node #7, the sequence is 7, 99, 7, 2, 7, 200, 7, 542, etc.)
   * @param n Number of steps for each nodes
   * @param numNodes Number of nodes to generate repeated sequences for
   * @param range Maximum value of the integers
   * @return
   */
  def repeatSequence(n: Int, numNodes: Int, range: Int) = { //Timer.time("Repeat Sequence Generation") {
    (1 to numNodes).foldLeft(Seq[Int]()) { case(arr, _) =>
      val x = scala.util.Random.nextInt(range - 1) + 1
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
