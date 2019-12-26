package com.example

import scala.language.postfixOps
import akka.pattern.ask
import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import Checker.{BlackUser, CheckUser, WhiteUser}
import Recorder.NewUser
import Storage.AddUser
import akka.util.Timeout
// This demonstrates the Tell and Ask for an actor

case class User(username: String, email: String)

object Recorder {
  sealed trait RecorderMsg

  case class NewUser(user: UserWithEmailFSM) extends RecorderMsg

  def props(checker: ActorRef, storage: ActorRef) =
    Props(new Recorder(checker, storage))
}

class Recorder(checker: ActorRef, storage: ActorRef) extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(5 seconds)

  override def receive: Receive = {
    case NewUser(user) =>
      checker ? CheckUser(user) map {
        case WhiteUser(user) =>
          println("Sending to Storage for white user")
          storage ! AddUser(user)
        case BlackUser(user) =>
          println(s"Blackuser detected $user")
      }
  }
}


object Checker {
  sealed trait CheckerMsg
  case class CheckUser(user: UserWithEmailFSM) extends CheckerMsg

  sealed trait CheckerResponse
  case class BlackUser(user: UserWithEmailFSM) extends CheckerResponse
  case class WhiteUser(user: UserWithEmailFSM) extends CheckerResponse
}

class Checker extends Actor {
  val blackListed = Seq(UserWithEmailFSM("Test", "test@test.com"))
  override def receive: Receive = {
    case CheckUser(user) if blackListed.contains(user) =>
      println(s"Detected black listed user $user")
      sender() ! BlackUser(user)
    case CheckUser(user) =>
      println(s"User is good $user")
      sender() ! WhiteUser(user)
  }
}


object Storage {
  sealed trait StorageMsg

  case class AddUser(user: UserWithEmailFSM) extends StorageMsg
}

class Storage extends Actor {
  val users= Seq.empty[UserWithEmailFSM]
  override def receive: Receive = {
    case AddUser(user) =>
      println(s"User Added.... $user")
      users :+ user
  }
}

object UserCreator extends App {
  val system = ActorSystem("user")

  val storage = system.actorOf(Props[Storage], "storage")
  val checker = system.actorOf(Props[Checker], "checker")
  val recorder = system.actorOf(Recorder.props(checker, storage), "recorder")

  recorder ! Recorder.NewUser(UserWithEmailFSM("Tet", "test@test.com"))

  Thread.sleep(100)

  system.terminate()

}

