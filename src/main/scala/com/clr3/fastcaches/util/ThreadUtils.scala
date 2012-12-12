package com.clr3.fastcaches.util

import actors.Futures._
import java.util.concurrent.{Future, Executors, ExecutorService}

object ThreadUtils {

  def parallelExecute[B](inputs: Seq[() => B]): List[Option[B]] = {
    val tasks = inputs.map { i => future { i } }
    awaitAll(999999L, tasks: _*).asInstanceOf[List[Option[B]]]
  }

  def parallel[B](inputs: Seq[() => B]) = {
    val es: ExecutorService = Executors.newFixedThreadPool(4)
    val tasks = inputs.map { i => es.submit(new Runnable { def run() { i() }})}
    tasks.foreach { task => task.get() }
    es.shutdown()
  }

}
