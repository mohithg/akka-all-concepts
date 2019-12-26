package com.example

import scala.language.postfixOps
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import com.example.MonitoringTest.{RestartException, ResumeException, StopException}

import scala.concurrent.duration._


class MonitoringTest extends Actor {
  import MonitoringTest._

  override def preStart(): Unit = {
    println("PreStart Supervisor")
    super.preStart()
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("Pre-Re-start Supervisor")
    super.preRestart(reason, message)
  }

  override def postStop(): Unit = {
    println("Post Stop Supervisor")
    super.postStop()
  }

  override def postRestart(reason: Throwable): Unit = {
    println("Post Restart Supervisor")
    super.postRestart(reason)
  }

  override def receive: Receive = {
    case "Resume" =>
      throw ResumeException
    case "Stop" =>
      throw StopException
    case "Restart" =>
      throw RestartException
    case _ =>
      throw new Exception
  }
}

object MonitoringTest {
  case object ResumeException extends Exception
  case object StopException extends Exception
  case object RestartException extends Exception
}

class SupervisionTest extends Actor {

  private var childRef: ActorRef = _

  override def preStart(): Unit = {
    childRef = context.actorOf(Props[MonitoringTest], "monitoring")
    Thread.sleep(100)
  }

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 10 second) {
    case ResumeException => Resume
    case StopException => Stop
    case RestartException => Restart
    case _: Exception => Escalate
  }

  override def receive: Receive = {
    case msg =>
      println(s"Received message $msg")
      childRef ! msg
      Thread.sleep(1000)
  }
}


object Supervision extends App {

  // Create the 'supervision' actor system
  val system = ActorSystem("supervision")

  // Create Hera Actor
  val supervisionTest = system.actorOf(Props[SupervisionTest], "hera")

  supervisionTest ! "Resume"

  //   hera ! "Resume"
  //   Thread.sleep(1000)
  //   println()

  //  hera ! "Restart"
  //  Thread.sleep(1000)
  //  println()

  //  hera ! "Stop"
  Thread.sleep(1000)
  println()


  system.terminate()

}