package jellon.ssg.io.impl

import jellon.ssg.io.Contents
import jellon.ssg.io.api.IHintHandler
import jellon.ssg.io.impl.OutputStreamResourcesTests.outputFilePath
import jellon.ssg.io.spi.IHintHandlers
import org.scalatest.funspec.AnyFunSpec

import java.io.{ByteArrayOutputStream, InputStream, OutputStream}
import java.net.URL

object HintHandlersTests {
  val handledHint: String = "hinthint"

  val unhandledHint: String = s"-$handledHint-"

  val handler: IHintHandler = new IHintHandler {
    override def optURL(resource: String, hint: String): Option[URL] = {
      if (hint == handledHint) {
        UrlResources.optURL(baseDir, resource)
      } else {
        Option.empty
      }
    }

    override def optInputStream(resource: String, hint: String): Option[InputStream] = {
      if (hint == handledHint) {
        InputStreamResources.optInputStream(baseDir, resource)
      } else {
        Option.empty
      }
    }

    // Note that we only open an in-memory OutputStream (not to the FS as in the other 2)
    override def optOutputStream(resource: String, hint: String): Option[OutputStream] = {
      if (hint == handledHint) {
        Some(new ByteArrayOutputStream())
      } else {
        Option.empty
      }
    }
  }

  val handlers: Seq[IHintHandler] = Seq(handler)

  val subject: IHintHandlers = new HintHandlers(handlers)
}

class HintHandlersTests extends AnyFunSpec {

  import HintHandlersTests._

  describe("HintHandlers.optURL(handlers: Seq[IHintHandler], resource: String, hint: String)") {
    it("should return Some(InputStream) when called for valid input and handled hint") {
      val actual = Contents.ofURL(HintHandlers.optURL(handlers, validResourcePath, handledHint))
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input and handled hint") {
      val actual = HintHandlers.optURL(handlers, invalidResourcePath, handledHint)
      assert(actual.isEmpty)
    }

    it("should return None when called for valid input and unhandled hint") {
      val actual = HintHandlers.optURL(handlers, validResourcePath, unhandledHint)
      assert(actual.isEmpty)
    }

    it("should return None when called for invalid input and unhandled hint") {
      val actual = HintHandlers.optURL(handlers, invalidResourcePath, unhandledHint)
      assert(actual.isEmpty)
    }
  }

  describe("HintHandlers.optInputStream(handlers: Seq[IHintHandler], resource: String, hint: String)") {
    it("should return Some(InputStream) when called for valid input and handled hint") {
      val actual = Contents.ofInputStream(HintHandlers.optInputStream(handlers, validResourcePath, handledHint))
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input and handled hint") {
      val actual = HintHandlers.optInputStream(handlers, invalidResourcePath, handledHint)
      assert(actual.isEmpty)
    }

    it("should return None when called for valid input and unhandled hint") {
      val actual = HintHandlers.optInputStream(handlers, validResourcePath, unhandledHint)
      assert(actual.isEmpty)
    }

    it("should return None when called for invalid input and unhandled hint") {
      val actual = HintHandlers.optInputStream(handlers, invalidResourcePath, unhandledHint)
      assert(actual.isEmpty)
    }
  }

  describe("HintHandlers.optOutputStream(handlers: Seq[IHintHandler], resource: String, hint: String)") {
    it("should return None when called for handled hint") {
      val actual = HintHandlers.optOutputStream(handlers, outputFilePath, handledHint)
      assert(actual.isDefined)
    }

    it("should return None when called for unhandled hint") {
      val actual = HintHandlers.optOutputStream(handlers, outputFilePath, unhandledHint)
      assert(actual.isEmpty)
    }
  }

  describe("optURL(resource: String, hint: String)") {
    it("should return Some(InputStream) when called for valid input and handled hint") {
      val actual = Contents.ofURL(subject.optURL(validResourcePath, handledHint))
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input and handled hint") {
      val actual = subject.optURL(invalidResourcePath, handledHint)
      assert(actual.isEmpty)
    }

    it("should return None when called for valid input and unhandled hint") {
      val actual = subject.optURL(validResourcePath, unhandledHint)
      assert(actual.isEmpty)
    }

    it("should return None when called for invalid input and unhandled hint") {
      val actual = subject.optURL(invalidResourcePath, unhandledHint)
      assert(actual.isEmpty)
    }
  }

  describe("optInputStream(resource: String, hint: String)") {
    it("should return Some(InputStream) when called for valid input and handled hint") {
      val actual = Contents.ofInputStream(subject.optInputStream(validResourcePath, handledHint))
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input and handled hint") {
      val actual = subject.optInputStream(invalidResourcePath, handledHint)
      assert(actual.isEmpty)
    }

    it("should return None when called for valid input and unhandled hint") {
      val actual = subject.optInputStream(validResourcePath, unhandledHint)
      assert(actual.isEmpty)
    }

    it("should return None when called for invalid input and unhandled hint") {
      val actual = subject.optInputStream(invalidResourcePath, unhandledHint)
      assert(actual.isEmpty)
    }
  }

  describe("optOutputStream(resource: String, hint: String)") {
    it("should return None when called for handled hint") {
      val actual = subject.optOutputStream(outputFilePath, handledHint)
      assert(actual.isDefined)
    }

    it("should return None when called for unhandled hint") {
      val actual = subject.optOutputStream(outputFilePath, unhandledHint)
      assert(actual.isEmpty)
    }
  }
}
