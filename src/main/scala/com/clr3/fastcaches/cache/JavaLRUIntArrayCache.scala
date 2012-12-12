package com.clr3.fastcaches.cache

import com.clr3.fastcaches.util.IntShardsReader

class LRUCache(val maxEntries: Int) extends java.util.LinkedHashMap[Int, Array[Int]](maxEntries + 1, 1.0f, true) {

  var currentEntries = 0

  override def put(k: Int, v: Array[Int]): Array[Int] = {
    currentEntries += v.size
    super.put(k, v)
  }

  override def removeEldestEntry(eldest: java.util.Map.Entry[Int, Array[Int]]): Boolean = {
    if (currentEntries > maxEntries) {
      currentEntries -= eldest.getValue.size
      true
    } else {
      false
    }
  }
}

class JavaLRUIntArrayCache(maxEntries: Int, idToIntOffset: Array[Long], idToNumEdges: Array[Int]) {
  val cache = java.util.Collections.synchronizedMap(new LRUCache(maxEntries))
  val isr = new IntShardsReader(Array(""), 10)

  def get(id: Int): Array[Int] = {
    if (!cache.containsKey(id)) {
      val intArray = new Array[Int](idToNumEdges(id))
      isr.readIntegersFromOffsetIntoArray(id, 0, idToNumEdges(id), intArray, 0)
      cache.put(id, intArray)
      intArray
    } else {
      cache.get(id)
    }
  }
}