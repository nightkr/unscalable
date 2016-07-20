package unscalable

import scala.concurrent.ExecutionContext
import scala.language.dynamics

class NativeDynamic(runner: NativeRunner) extends Dynamic {
  def applyDynamic(cmd: String)(args: String*)(implicit ec: ExecutionContext) = runner.runCommand(cmd, args)
}
