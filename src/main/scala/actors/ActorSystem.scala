package actors

import zio.*

trait ActorSystem(val name: String):
   def make[Environment, State, Message[+Response], Response](s: State)(
     f: (State, Message[Response]) => ZIO[Environment, Nothing, (State, Response)]
   ): ZIO[Environment, Nothing, ActorRef[Message]]

   

object ActorSystem:
  def make(name: String): ZIO[Scope, Nothing, ActorSystem] = 
    for 
      scope <- ZIO.scope
      counter <- Ref.make(0L)
      actors <- Ref.make[Map[Long, Actor[?]]](Map.empty)
    yield new ActorSystem(name) {
      def make[Environment, State, Message[+Response], Response](s: State)(
        f: (State, Message[Response]) => ZIO[Environment, Nothing, (State, Response)]): ZIO[Environment, Nothing, ActorRef[Message]] = 
          for 
            actor <- scope.extend(Actor.make(s)(f))
            id <- counter.getAndUpdate(_ + 1)
            _ <- actors.update(_ + (id -> actor))
          yield new ActorRef {
            def send(message: Message[Any]): ZIO[Any, Nothing, Unit] = 
              actor.send(message)
            def ask[Response](message: Message[Response]): ZIO[Any, Nothing, Response] = 
              actor.ask(message)
          }
    }