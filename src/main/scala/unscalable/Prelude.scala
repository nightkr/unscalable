package unscalable

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object Prelude {
  implicit lazy val actorSystem = ActorSystem()
  implicit lazy val materializer = ActorMaterializer()
  implicit lazy val executionContext = actorSystem.dispatcher

  lazy val native = new NativeRunner
  lazy val n = new NativeDynamic(native)
}
