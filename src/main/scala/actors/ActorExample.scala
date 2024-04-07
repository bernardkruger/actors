package actorexample

import zio.*
import actors.*
import actorexample.Example.TemperatureMessage.GetTemperature
import actorexample.Example.TemperatureMessage.SetTemperature
import actorexample.Example.makeTemperatureActor

object Example:
  sealed trait TemperatureMessage[+Response]
  
  object TemperatureMessage:
    final case class SetTemperature(value: Double) extends TemperatureMessage[Unit]
    case object GetTemperature extends TemperatureMessage[Double]
  end TemperatureMessage
  
  
  def makeTemperatureActor(actorSystem: ActorSystem): ZIO[Any, Nothing, ActorRef[TemperatureMessage]] =
    actorSystem.make(0.0) {
      case (state, SetTemperature(value)) => ZIO.succeed(value -> ())      
      case (state, GetTemperature) => ZIO.succeed(state -> state)
    }  


object ActorExample extends ZIOAppDefault:
  
  val myTempProgram = 
    for 
      actorSystem <- ActorSystem.make("system1")
      actor <- makeTemperatureActor(actorSystem)
      _ <- actor ! SetTemperature(42.0)
      _ <- actor ! SetTemperature(43.0)      
      _ <- actor ! SetTemperature(44.0)
      temperature <- (actor ? GetTemperature).debug("temperature is")      
    yield ()

  

  def run =    
    for 
      _ <- ZIO.scoped(myTempProgram)    // this to understand scopes better
      _ <- ZIO.debug("doing something else").delay(1.second).repeatN(2)
    yield ()