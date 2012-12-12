package com.clr3.fastcaches.cache

import com.clr3.fastcaches.util.IntShardsReader
import com.google.common.cache.{CacheLoader, Weigher, CacheBuilder, LoadingCache}

object GuavaLRUIntArrayCache {
  def apply(maxNodes: Int) = {
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
    cacheG
  }
}
