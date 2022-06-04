package jellon.ssg.engine.flagship.hints

import jellon.ssg.engine.flagship.hints.JavaOutputStream.copy
import jellon.ssg.io.spi.IOutputStreamResources

import java.io._
import java.util.regex.Pattern
import scala.io.Source

object JavaOutputStream {
  def copy(fileSystem: IOutputStreamResources, path: String, contents: String): Unit = {
    parseJavaClassName(contents) match {
      case None => throw new IOException("No java class declaration found in: " + contents)
      case Some(className) => copy(fileSystem, path, className, contents)
    }
  }

  def copy(fileSystem: IOutputStreamResources, path: String, className: String, contents: String): Unit = {
    val fileName = new File(path, className + ".java").toString
    val outputStream = fileSystem.openOutputStream(fileName)
    val printStream = new PrintStream(outputStream)
    copy(printStream, contents)
  }

  def copy(output: PrintStream, contents: String): Unit = {
    try {
      output.print(contents)
      output.flush()
    } finally {
      output.close()
    }
  }

  /**
   * Uses a heuristic to find the
   *
   * @param contents text of the potential ".java" file
   * @return the class name declared in the java text, if possible
   */
  // TODO: use antlr to parse instead of a Pattern. Can always fall back on a pattern, such might be needed if/when Java syntax is no longer compatible w/ the g4 file
  def parseJavaClassName(contents: String): Option[String] = {
    val JAVA_ID = "[a-zA-Z_$][a-zA-Z\\d_$]*"
    val pattern = Pattern.compile(s"^.*(class|enum|interface|@interface) +($JAVA_ID)(< *${JAVA_ID}.*>)?( +extends ${JAVA_ID}.*)?( +implements ${JAVA_ID}.*)? *\\{ *$$")
    val reader = Source.fromString(contents)
    try {
      // I'm cringing just thinking about writing this in Java with a Scanner ;)
      reader.getLines()
        .to(LazyList)
        .map(line =>
          pattern.matcher(line)
        )
        .find(_.matches())
        .map(_.group(2))
    } finally {
      reader.close()
    }
  }
}

class JavaOutputStream(fileSystem: IOutputStreamResources, path: String) extends FilterOutputStream(new ByteArrayOutputStream()) {
  override def flush(): Unit =
    copy(fileSystem, path, out.toString)
}
