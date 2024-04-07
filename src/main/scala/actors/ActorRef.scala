package actors

import zio.*

trait ActorRef[-Message[+Response]]:
  def send(message: Message[Any]): ZIO[Any, Nothing, Unit]
  def ask[Response](message: Message[Response]): ZIO[Any, Nothing, Response]
  def ! (message: Message[Any]): ZIO[Any, Nothing, Unit] = send(message)
  def ?[Response](message: Message[Response]): ZIO[Any, Nothing, Response] = ask(message)