package com.clr3.fastcaches.util

object IntShardsReader {
  val rnd = new scala.util.Random
  val fakeInts = new Array[Array[Int]](10000)
  val fakeIntsToIntOffset: Array[Long] = new Array[Long](10000)
  val fakeIntsLengths = new Array[Int](10000)
  Timer.time("Random Node Lists Generation") {
    for (i <- 1 to fakeInts.length - 1) {
      val arrayLength = math.pow(rnd.nextInt(25), 2).toInt + 1
      fakeIntsLengths(i) = arrayLength
      val array = new Array[Int](arrayLength)
      for (j <- 0 to arrayLength - 1) {
        array(j) = rnd.nextInt(1000000)
      }
      fakeInts(i) = array
    }
  }
}

class IntShardsReader(val shardDirectories: Array[String], val numShards: Int) {

  def readIntegersFromOffsetIntoArray(nodeId:Int, intOffset:Long, numInts:Int, intArray:Array[Int], intArrayOffset:Int):Unit = {
    Array.copy(IntShardsReader.fakeInts(nodeId), 0, intArray, 0, numInts)
  }

}