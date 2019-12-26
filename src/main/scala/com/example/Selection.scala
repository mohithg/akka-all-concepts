package com.example

import akka.actor.{Actor, ActorIdentity, ActorRef, ActorSystem, Identify, PoisonPill, Props}

class Counter extends Actor {
  import Counter._

  private var count = 0

  override def receive: Receive = {
    case Inc(x) =>
      count += x
    case Dec(x) =>
      count -= x
  }

}

object Counter {

  final case class Inc(num: Int)
  final case class Dec(num: Int)
}

class Watcher extends Actor {

  var counterRef: ActorRef = _

  val selection = context.actorSelection("/user/counter")

  selection ! Identify(None)

  override def receive: Receive = {
    case ActorIdentity(_, Some(ref)) =>
      println(s"Actor Reference for counter is ${ref}")
    case ActorIdentity(_, None) =>
      println("Actor selection for actor doesn't live :( ")

  }
}

object Watch extends App {

  val system = ActorSystem("Watsh-actor-selection")

  val counter = system.actorOf(Props[Counter], "counter")

  val watcher = system.actorOf(Props[Watcher], "watcher")

  counter ! PoisonPill

  println(s"Counter ref is ${counter}")

  val counterByPath = system.actorSelection("counter")
  println(s"Counter by path is ${counterByPath}")

  Thread.sleep(1000)

  system.terminate()
}