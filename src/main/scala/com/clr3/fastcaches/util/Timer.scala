package com.clr3.fastcaches.util

object Timer {

  def t[T](f: => T):(Long, T) = {
    val start = System.nanoTime()
    val result = f
    val end = System.nanoTime()
    (end-start, result)
  }

  /**
   * Time a function
   */
  def time[T](name: String)(f: => T): T = {
    val (time, result) = t(f)
    println("%s took %sms".format(name, time/1000000))
    result
  }

  /**
   * Repeat this function and average the total amount of time taken per iteration
   */
  def manyTimes[T](name: String)(iterations: Int)(f: => T): Iterable[T] = {
    val trs = (0 until iterations).map { i => t(f) }
    val times = trs.map { i => i._1 }
//    println("%s (x%s) took an average of %sms".format(name, iterations, (times.sum / times.size)/1000000))
    println("%s\t%s\t%s".format(name, iterations, (times.sum / times.size)/1000000))
    trs.map { i => i._2 }
  }

  /**
   * Repeat this function and sum the total amount of time taken
   */
  def repeat[T](name: String)(iterations: Int)(f: => T): Iterable[T] = {
    val trs = (0 until iterations).map { i => t(f) }
    val times = trs.map { i => i._1 }
    println("%s (x%s) took a total of %sms".format(name, iterations, times.sum/1000000))
    trs.map { i => i._2 }
  }

}
