package com.example

import akka.actor.{Actor, ActorSystem, Props}

case class WhotoGreet(
  message: String
)

class Greeter extends Actor {
  override def receive: Receive = {
    case WhotoGreet(who) =>
      println(s"Hello $who")
  }
}

object AkkaQuickstart extends App {
  val system = ActorSystem("Hello-Akka")

  val greeter = system.actorOf(Props[Greeter], "greeter")

  greeter ! WhotoGreet("Mohith")

  system.terminate()
}
