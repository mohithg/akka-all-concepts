package com.example

import akka.actor.{Actor, ActorSystem, Props}
import com.example.MusicController._
import com.example.MusicPlayer.{PlayMusic, StopMusic}

object MusicController {
  sealed trait ControllerMsg

  case object Play extends ControllerMsg

  case object Stop extends ControllerMsg

  def props = Props[MusicController]
}

class MusicController extends Actor {
  override def receive: Receive = {
    case Play =>
      println("Started Music Play....")
    case Stop =>
      println("Music Stopped.....")
    case _ =>
      println("Uknown Message")
  }
}

object MusicPlayer {
  sealed trait PlayMsg
  case object StopMusic extends PlayMsg
  case object PlayMusic extends PlayMsg
  def props = Props[MusicPlayer]
}

class MusicPlayer extends Actor {
  override def receive: Receive = {
    case StopMusic =>
      println("I cannot stop music")
    case PlayMusic =>
      val musicController = context.actorOf(MusicController.props, "music-controller")
      musicController ! Play
    case _ =>
      println("Uknown Message")
  }
}

object Creation extends App {
  val system = ActorSystem("creation")

  val player = system.actorOf(MusicPlayer.props, "music-player")

  player ! PlayMusic

  system.terminate()
}