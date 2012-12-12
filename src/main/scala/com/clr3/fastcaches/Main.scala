package com.clr3.fastcaches

import util.{ThreadUtils, SequenceGeneration, IntShardsReader, Timer}
import com.google.common.cache.{CacheLoader, Weigher, CacheBuilder, LoadingCache}
import cache._

object Main {
  def main(args: Array[String]) {

    val maxNodes = 1000

    IntShardsReader.fakeIntsLengths

    def compareParallel(desc: String, sequences: Seq[Seq[Int]]) = {
      val fast = FastLRUIntArrayCache(Array(""), 10, 1000000, maxNodes, maxNodes * 200, IntShardsReader.fakeIntsToIntOffset, IntShardsReader.fakeIntsLengths)
      val clock = FastClockIntArrayCache(Array(""), 10, 1000000, maxNodes, maxNodes * 200, IntShardsReader.fakeIntsToIntOffset, IntShardsReader.fakeIntsLengths)
      val rand = LocklessRandomizedIntArrayCache(Array(""), 10, 1000000, maxNodes, maxNodes * 200, IntShardsReader.fakeIntsToIntOffset, IntShardsReader.fakeIntsLengths)
      val javaLru = new JavaLRUIntArrayCache(maxNodes * 201, IntShardsReader.fakeIntsToIntOffset, IntShardsReader.fakeIntsLengths)
      val cacheG = GuavaLRUIntArrayCache(maxNodes)

      val lruSeq = sequences.map { seq =>
        () => { val f = fast.getThreadSafeChild; seq.foreach { i => f.get(i) }}
      }
      Timer.manyTimes(desc + "\tFLRU")(1) {
        ThreadUtils.parallel(lruSeq)
      }

      val clockSeq = sequences.map { seq =>
        () => { val f = clock.getThreadSafeChild; seq.foreach { i => f.get(i) }}
      }
      Timer.manyTimes(desc + "\tFClock")(1) {
        ThreadUtils.parallel(clockSeq)
      }

      val randSeq = sequences.map { seq =>
        () => { val f = rand.getThreadSafeChild; seq.foreach { i => f.get(i) }}
      }
      Timer.manyTimes(desc + "\tLRand")(1) {
        ThreadUtils.parallel(randSeq)
      }

      val guaSeq = sequences.map { seq =>
        () => { seq.foreach { i => cacheG.get(i) }}
      }
      Timer.manyTimes(desc + "\tGuava")(1) {
        ThreadUtils.parallel(guaSeq)
      }

      val javaLruSeq = sequences.map { seq =>
        () => { seq.foreach { i => javaLru.get(i) }}
      }
      Timer.manyTimes(desc + "\tJavaLRU")(1) {
        ThreadUtils.parallel(javaLruSeq)
      }
    }

    def compare(desc: String, sequence: Seq[Int]) = {
      val fast = FastLRUIntArrayCache(Array(""), 10, 1000000, maxNodes, maxNodes * 200, IntShardsReader.fakeIntsToIntOffset, IntShardsReader.fakeIntsLengths)
      val clock = FastClockIntArrayCache(Array(""), 10, 1000000, maxNodes, maxNodes * 200, IntShardsReader.fakeIntsToIntOffset, IntShardsReader.fakeIntsLengths)
      val rand = LocklessRandomizedIntArrayCache(Array(""), 10, 1000000, maxNodes, maxNodes * 200, IntShardsReader.fakeIntsToIntOffset, IntShardsReader.fakeIntsLengths)
      val javaLru = new JavaLRUIntArrayCache(maxNodes * 201, IntShardsReader.fakeIntsToIntOffset, IntShardsReader.fakeIntsLengths)
      val cacheG = GuavaLRUIntArrayCache(maxNodes)

      Timer.manyTimes(desc + "\tFLRU")(1) {
        sequence.foreach { i => fast.get(i) }
      }

      Timer.manyTimes(desc + "\tFClock")(1) {
        sequence.foreach { i => clock.get(i) }
      }

      Timer.manyTimes(desc + "\tLRand")(1) {
        sequence.foreach { i => rand.get(i) }
      }

      Timer.manyTimes(desc + "\tGuava")(1) {
        sequence.foreach { i => cacheG.get(i) }
      }

      Timer.manyTimes(desc + "\tJavaLRU")(1) {
        sequence.foreach { i => javaLru.get(i) }
      }
    }

    println("==Parallel==")
    compareParallel("Random", (1 until 4).map { _ => SequenceGeneration.randomSequence(1000000, 100000) })
    compareParallel("Small", (1 until 4).map { _ => SequenceGeneration.randomSequence(1000000, 1000) })
    compareParallel("Power", (1 until 4).map { _ => SequenceGeneration.powerSequence(1000000, 2, 100000) })
    compareParallel("Repeat", (1 until 4).map { _ => SequenceGeneration.repeatSequence(10000, 100, 100000) })

    println("==Non-Parallel==")
    val sequence = SequenceGeneration.randomSequence(1000000, 100000)
    val sequence3 = SequenceGeneration.randomSequence(1000000, 1000)
    val sequence4 = SequenceGeneration.powerSequence(1000000, 2, 100000)
    val sequence5 = SequenceGeneration.repeatSequence(10000, 100, 100000)
    compare("Random", sequence)
    compare("Small", sequence3)
    compare("Power", sequence4)
    compare("Repeat", sequence5)

    // TODO Test Guava on Pref and Random

  }
}
