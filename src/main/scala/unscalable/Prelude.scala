package unscalable

import java.nio.charset.Charset

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import unscalable.NativeRunner.NativeCommandResult

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable}

object Prelude {
  implicit lazy val actorSystem = ActorSystem()
  implicit lazy val materializer = ActorMaterializer()
  implicit lazy val executionContext = actorSystem.dispatcher

  lazy val native = new NativeRunner
  lazy val n = new NativeDynamic(native)

  def run[T](cr: NativeCommandResult, charset: Charset = NativeRunner.defaultCharset): Int = {
    val stdoutEof = cr.stdout.lines(charset).runForeach(println)
    val stderrEof = cr.stderr.lines(charset).runForeach(println)
    await(stdoutEof)
    await(stderrEof)
    await(cr.returnCode)
  }

  def await[T](f: Awaitable[T]): T = {
    Await.result(f, Duration.Inf)
  }
}
