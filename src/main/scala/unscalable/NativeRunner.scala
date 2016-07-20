package unscalable

import java.io.InputStream
import java.nio.charset.Charset
import java.nio.{ByteBuffer, CharBuffer}

import akka.NotUsed
import akka.stream.scaladsl.{Sink, Source}
import unscalable.NativeRunner.{NativeCommandResult, SinkEndpoint, SourceEndpoint}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class NativeRunner {
  def runCommand(cmd: String, args: Seq[String])(implicit ec: ExecutionContext): NativeCommandResult = {
    val builder = new ProcessBuilder(cmd +: args: _*)
    builder.redirectInput(ProcessBuilder.Redirect.PIPE)
    builder.redirectError(ProcessBuilder.Redirect.PIPE)
    builder.redirectOutput(ProcessBuilder.Redirect.PIPE)
    val process = builder.start()

    NativeCommandResult(
      returnCode = Future(process.waitFor()),
      stdin = SinkEndpoint(Sink.cancelled[Byte]),
      stdout = SourceEndpoint.fromInputStream(process.getInputStream),
      stderr = SourceEndpoint.fromInputStream(process.getErrorStream)
    )
  }
}

object NativeRunner {

  case class NativeCommandResult(returnCode: Future[Int], stdin: SinkEndpoint, stdout: SourceEndpoint, stderr: SourceEndpoint)

  case class SinkEndpoint(bytes: Sink[Byte, NotUsed])

  case class SourceEndpoint(bytes: Source[Byte, NotUsed]) {
    lazy val defaultCharset = Charset.forName("UTF-8")

    def chars(charset: Charset = defaultCharset): Source[Char, NotUsed] = bytes.statefulMapConcat[Char] { () =>
      val decoder = charset.newDecoder()
      val inBuf = ByteBuffer.allocate(1)
      val outBuf = CharBuffer.allocate(Math.ceil(decoder.maxCharsPerByte()).toInt)

    { (byte: Byte) =>
      inBuf.clear()
      inBuf.put(0, byte)
      outBuf.clear()

      decoder.decode(inBuf, outBuf, false) match {
        case x if x.isUnderflow =>
          0.until(outBuf.position()).map(outBuf.get).toList
        case x =>
          x.throwException().asInstanceOf[Nothing] // Should be unreachable, since an exception *should* be thrown
      }
    }
    }

    def lines(charset: Charset = defaultCharset): Source[String, NotUsed] = chars(charset).statefulMapConcat[String] { () =>
      val buf = mutable.Queue[Char]()

    { char: Char =>
      char match {
        case '\r' =>
          List.empty
        case '\n' =>
          val line = buf.toList
          buf.clear()
          List(line.mkString)
        case x =>
          buf += x
          List.empty
      }
    }
    }
  }

  object SourceEndpoint {
    def fromInputStream(is: InputStream): SourceEndpoint = {
      val source = Source.unfold[InputStream, Byte](is) { is =>
        is.read() match {
          case -1 => None
          case x => Some((is, x.toByte))
        }
      }
      SourceEndpoint(source)
    }
  }

}
