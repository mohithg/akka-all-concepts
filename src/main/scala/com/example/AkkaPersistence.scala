package com.example
import akka.actor.{ActorSystem, Props}
import akka.persistence._
import com.example.CounterPersistence._

object CounterPersistence {
  sealed trait Operation {
    val count: Int
  }

  case class Increment(override val count: Int) extends Operation
  case class Decrement(override val count: Int) extends Operation

  case class Cmd(op: Operation) // Outer world
  case class Evnt(op: Operation) // Event from journals

  case class State(count: Int)
}

class CounterPersistence extends PersistentActor {

  println("Starting...")

  var state: State = State(count= 0)

  def updateState(evt: Evnt): Unit = evt match {
    case Evnt(Increment(count)) =>
      state = State(count = state.count + count)
//      takeSnapshot
    case Evnt(Decrement(count)) =>
      state = State(count = state.count - count)
//      takeSnapshot
  }

  // recover mode
  // starts with recovering mode
  override def receiveRecover: Receive = {
    case evt: Evnt =>
      println(s"Counter receive ${evt} on recovering mode")
      updateState(evt)
    case SnapshotOffer(_, snapshot: State) =>
      println(s"Counter receive snapshot with data ${snapshot} on recovering mode")
      state = snapshot
    case RecoveryCompleted =>
      println("Recovery complete and now I'll switch to receive mode!!!")
  }

  // normal mode
  override def receiveCommand: Receive = {
    case cmd @ Cmd(op) =>
      println(s"Counter receive ${cmd}")
      // persistes message and calls handler
      persist(Evnt(op)) { evt =>
        updateState(evt)
      }

    case "print" =>
      println(s"The Current state of counter is ${state}")

    case SaveSnapshotSuccess(metadata) =>
      println(s"save snapshot succeed.")
    case SaveSnapshotFailure(metadata, reason) =>
      println(s"save snapshot failed and failure is ${reason}")
  }

  override def persistenceId: String = "counter-example"

  def takeSnapshot = {
    if(state.count % 5 == 0){
      saveSnapshot(state)
    }
  }

//  override def recovery: Recovery = Recovery.none
}

object Persistent extends App {
  import CounterPersistence._

  val system = ActorSystem("persistent-actors")

  val counter = system.actorOf(Props[CounterPersistence])

  counter ! Cmd(Increment(3))

  counter ! Cmd(Increment(5))

  counter ! Cmd(Decrement(3))

  counter ! "print"

  Thread.sleep(1000)

  system.terminate()

}
