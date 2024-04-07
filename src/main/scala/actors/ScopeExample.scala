package actors

import zio.*

object ScopeExample extends ZIOAppDefault:
  
  def resource(name: String): ZIO[Scope, Nothing, Unit] = 
    ZIO.acquireRelease(ZIO.debug(s"acquiring $name"))(_ => ZIO.debug(s"releasing $name")).unit
    

  val scopeExample = 
    for 
      _ <- resource("A").forkScoped.onInterrupt(ZIO.debug("shutting down the scope"))      
      _ <- resource("B").forkScoped.onInterrupt(ZIO.debug("shutting down the scope"))
      _ <- ZIO.scoped(resource("C").forkScoped.onInterrupt(ZIO.debug("shutting down the scope"))) 
      _ <- ZIO.debug("first doing something else before releasing resources B (first) and A (C has been released via the scope already)").delay(1.second).repeatN(5)
    yield ()

  def run = scopeExample