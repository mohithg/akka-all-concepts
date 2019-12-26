package com.example

import akka.actor.{Actor, ActorSystem, Props, Stash}
import com.example.UserStorage.{Connect, Disconnect, Operation}
import com.example.UserStorage.DBOperation.Create

case class UserWithEmail(name: String, email: String)

object UserStorage {
  trait DBOperation

  object DBOperation {
    case object Create extends DBOperation
    case object Update extends DBOperation
  }
  case object Connect
  case object Disconnect
  case class Operation(DBOperation: DBOperation, user: UserWithEmail)
}

class UserStorage extends Actor with Stash {

  private def connected: Receive = {
    case Disconnect =>
      println("UserStorage Disconnected ....")
      context.unbecome()
    case Operation(operation, user) =>
      operation match {
        case Create =>
          println(s"Created ${user} in user storage")
      }
  }

  private def disconnected: Receive = {
    case Connect =>
      println("UserStorage connected.....")
      context.become(connected)
      unstashAll()
    case _ =>
      stash()
  }

  override def receive: Receive = disconnected
}

object BecomeTest extends App {
  val system = ActorSystem("become-test")

  val userStorageActor = system.actorOf(Props[UserStorage], "user-storage")

  userStorageActor ! Operation(Create, UserWithEmail("mohith", "test@test.com"))
  userStorageActor ! Connect
  userStorageActor ! Disconnect

  Thread.sleep(100)
  system.terminate()
}