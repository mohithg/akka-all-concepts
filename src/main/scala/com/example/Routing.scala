package com.example

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.routing.{FromConfig, RoundRobinPool}
import com.example.Worker.Work

class Worker extends Actor {
  override def receive: Receive = {
    case msg: Work =>
      println(s"Got message work ${msg} with ${self}")
  }
}

object Worker {
  case class Work()
}

class Router extends Actor {
  private var routees: List[ActorRef] = _

  override def preStart(): Unit = {
    routees = List.fill(5)(
      context.actorOf(Props[Worker])
    )
  }

  override def receive: Receive = {
    case msg: Work =>
      println(s"I'm from a router received ${msg}")
      routees(util.Random.nextInt(routees.size)) forward msg
  }
}

object Routing extends App {
  val system = ActorSystem("routing")

  system.actorOf(Props[Worker], "w1")
  system.actorOf(Props[Worker], "w2")
  system.actorOf(Props[Worker], "w3")

  val router = system.actorOf(FromConfig.props(), "round-robin-pool")

  router ! Work()
  router ! Work()
  router ! Work()

  Thread.sleep(5000)

  system.terminate()

}