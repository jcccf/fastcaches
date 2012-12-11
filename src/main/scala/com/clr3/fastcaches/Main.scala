package com.clr3.fastcaches

import util.{SequenceGeneration, IntShardsReader, Timer}
import com.google.common.cache.{CacheLoader, Weigher, CacheBuilder, LoadingCache}
import cache.{FastClockIntArrayCache, FastLRUIntArrayCache}

object Main {
  def main(args: Array[String]) {
    Console.println("Hello World!")

    val maxNodes = 1000
    val isr = new IntShardsReader(Array(""), 10)

    val cacheG:LoadingCache[Int, Array[Int]] = CacheBuilder.newBuilder()
      .maximumWeight(maxNodes * 201)
      .weigher(new Weigher[Int,Array[Int]] {
      def weigh(k:Int, v:Array[Int]):Int = v.length
    })
      .asInstanceOf[CacheBuilder[Int,Array[Int]]]
      .build[Int,Array[Int]](new CacheLoader[Int,Array[Int]] {
      def load(id:Int):Array[Int] = {
        val intArray = new Array[Int](IntShardsReader.fakeIntsLengths(id))
        isr.readIntegersFromOffsetIntoArray(id, 0, IntShardsReader.fakeIntsLengths(id), intArray, 0)
        intArray
      }
    })

    val fast = FastLRUIntArrayCache(Array(""), 10, 1000000, maxNodes, maxNodes * 200, IntShardsReader.fakeIntsToIntOffset, IntShardsReader.fakeIntsLengths)
    val clock = FastClockIntArrayCache(Array(""), 10, 1000000, maxNodes, maxNodes * 200, IntShardsReader.fakeIntsToIntOffset, IntShardsReader.fakeIntsLengths)

    val sequence = SequenceGeneration.randomSequence(1000000, 10000)
    val sequence2 = SequenceGeneration.randomSequence(1000000, 1000)
    val sequence3 = SequenceGeneration.randomSequence(1000000, 100)
    val sequence4 = SequenceGeneration.powerSequence(100, 2.0)
    val sequence5 = SequenceGeneration.repeatSequence(10000, 100, 10000)

    def compare(sequence: Seq[Int]) = {
      Timer.manyTimes("FLRU Get")(10) {
        sequence.foreach { i => fast.get(i) }
      }

      Timer.manyTimes("FClock Get")(10) {
        sequence.foreach { i => clock.get(i) }
      }

      Timer.manyTimes("Guava Get")(10) {
        sequence.foreach { i => cacheG.get(i) }
      }

      println()
    }

    compare(sequence)
    compare(sequence2)
    compare(sequence3)
    compare(sequence4)
    compare(sequence5)

    // TODO Test Guava on Pref and Random

    // TODO Multithread
  }
}
