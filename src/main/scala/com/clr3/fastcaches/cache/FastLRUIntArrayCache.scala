package com.clr3.fastcaches.cache

import com.clr3.fastcaches.util.{LinkedIntIntMap, IntShardsReader}
import concurrent.Lock
import com.twitter.ostrich.stats.Stats

object FastLRUIntArrayCache {
  /**
   * Array-based LRU algorithm implementation. Elements are
   * evicted in a least-recently-used order.
   *
   * @param shardDirectories Directories where edge shards live
   * @param numShards Number of edge shards
   * @param maxId Maximum id that will be requested
   * @param cacheMaxNodes Maximum number of nodes the cache can have
   * @param cacheMaxEdges Maximum number of edges the cache can have
   * @param idToIntOffset Array of node id -> offset in a shard
   * @param idToNumEdges Array of node id -> number of edges
   */
  def apply(shardDirectories: Array[String], numShards: Int,
            maxId: Int, cacheMaxNodes: Int, cacheMaxEdges: Long,
            idToIntOffset: Array[Long], idToNumEdges: Array[Int]) = {

    new FastLRUIntArrayCache(shardDirectories, numShards,
      maxId, cacheMaxNodes, cacheMaxEdges,
      idToIntOffset, idToNumEdges,
      new IntShardsReader(shardDirectories, numShards),
      new Array[Array[Int]](cacheMaxNodes + 1),
      new LinkedIntIntMap(maxId, cacheMaxNodes),
      new IntArrayCacheNumbers,
      new Lock
    )
  }
}

class FastLRUIntArrayCache private(shardDirectories: Array[String], numShards: Int,
                                   maxId: Int, cacheMaxNodes: Int, cacheMaxEdges: Long,
                                   idToIntOffset: Array[Long], idToNumEdges: Array[Int],
                                   val reader: IntShardsReader,
                                   val indexToArray: Array[Array[Int]],
                                   val linkedMap: LinkedIntIntMap,
                                   val numbers: IntArrayCacheNumbers,
                                   val lock: Lock) extends IntArrayCache {

  def getThreadSafeChild = new FastLRUIntArrayCache(shardDirectories, numShards,
    maxId, cacheMaxNodes, cacheMaxEdges,
    idToIntOffset, idToNumEdges,
    new IntShardsReader(shardDirectories, numShards),
    indexToArray, linkedMap, numbers, lock)

  def get(id: Int): Array[Int] = {
    lock.acquire
    if (linkedMap.contains(id)) {
      numbers.hits += 1
      val idx = linkedMap.getIndexFromId(id)
      linkedMap.moveIndexToHead(idx)
      val a = indexToArray(idx)
      lock.release
      a
    }
    else Stats.time("fastlru_miss") {
      lock.release
      val numEdges = idToNumEdges(id)
      if (numEdges == 0) {
        throw new NullPointerException("FastLRUIntArrayCache idToIntOffsetAndNumEdges %s".format(id))
      }
      else {
        // Read in array
        val intArray = new Array[Int](numEdges)

        reader.readIntegersFromOffsetIntoArray(id, idToIntOffset(id), numEdges, intArray, 0)

        lock.acquire
        if (linkedMap.contains(id)) {
          val idx = linkedMap.getIndexFromId(id)
          linkedMap.moveIndexToHead(idx)
          val a = indexToArray(idx)
          lock.release
          a
        }
        else {
          numbers.misses += 1
          // Evict from cache
          numbers.currRealCapacity += numEdges
          while (linkedMap.getCurrentSize == cacheMaxNodes || numbers.currRealCapacity > cacheMaxEdges) {
            val oldIndex = linkedMap.getTailIndex
            numbers.currRealCapacity -= indexToArray(oldIndex).length
            // indexToArray(oldIndex) = null // Don't need this because it will get overwritten
            linkedMap.removeFromTail()
          }

          linkedMap.addToHeadAndNotExists(id)
          indexToArray(linkedMap.getHeadIndex) = intArray
          lock.release
          intArray
        }
      }
    }
  }

  def getStats = {
    (numbers.misses, numbers.hits, linkedMap.getCurrentSize, numbers.currRealCapacity)
  }
}
