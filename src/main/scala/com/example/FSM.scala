package com.example

import akka.actor.{Actor, ActorSystem, FSM, Props, Stash}
import com.example.UserStorage.{Connect, Disconnect, Operation}
import com.example.UserStorage.DBOperation.Create
import com.example.UserStorageFSM._

case class UserWithEmailFSM(name: String, email: String)

object UserStorageFSM {

  sealed trait State
  case object Connected extends State
  case object Disconnected extends State

  sealed trait Data
  case object EmptyData extends Data

  trait DBOperation

  object DBOperation {
    case object Create extends DBOperation
    case object Update extends DBOperation
  }
  case object Connect
  case object Disconnect
  case class Operation(DBOperation: DBOperation, user: UserWithEmailFSM)
}

class UserStorageFSM extends FSM[UserStorageFSM.State, UserStorageFSM.Data] with Stash {

  // define startwith
  startWith(Disconnected, EmptyData)

  // define states
  when(Disconnected) {
    case Event(Connect, _) =>
      println("User storage connected...")
      unstashAll()
      goto(Connected) using(EmptyData)
    case Event(_, _) =>
      stash()
      stay using EmptyData
  }

  when(Connected) {
    case Event(Disconnect, _) =>
      println("UserStorage Disconnected ....")
      goto(Disconnected) using EmptyData
    case Event(Operation(operation, user), _) =>
      operation match {
        case Create =>
          println(s"Created ${user} in user storage")
          stay using EmptyData
      }
  }

  initialize()

}

object FsmTest extends App {
  val system = ActorSystem("become-test")

  val userStorageActor = system.actorOf(Props[UserStorageFSM], "user-storage")

  userStorageActor ! Operation(Create, UserWithEmailFSM("mohith", "test@test.com"))
  userStorageActor ! Connect
  userStorageActor ! Disconnect

  Thread.sleep(100)
  system.terminate()
}