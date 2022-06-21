package jellon.ssg.io.impl

import jellon.ssg.io.Contents
import jellon.ssg.io.api.IHintHandler
import jellon.ssg.io.impl.HintHandlersTests.{handledHint, unhandledHint}
import jellon.ssg.io.impl.OutputStreamResourcesTests.outputFilePath
import jellon.ssg.io.spi._
import org.scalatest.funspec.AnyFunSpec

import java.io.{IOException, InputStream, OutputStream}
import java.net.URL
import java.util.Objects

object ResourcesTests {
  val handlers: IHintHandlers = new HintHandlers(Seq(
    new IHintHandler() {
      override def optURL(resource: String, hint: String): Option[URL] =
        if (hint == handledHint) Some(null)
        else Option.empty

      override def optInputStream(resource: String, hint: String): Option[InputStream] =
        if (hint == handledHint) Some(null)
        else Option.empty

      override def optOutputStream(resource: String, hint: String): Option[OutputStream] =
        if (hint == handledHint) Some(null)
        else Option.empty
    }
  ))

  def subject(): ImplTestResources =
    new ImplTestResources(UrlResourcesTests.subject, InputStreamResourcesTests.subject, new ByteArrayOutputStreamResources(), handlers)
}

class ResourcesTests extends AnyFunSpec {

  import ResourcesTests.subject

  describe("openURL(resource: String)") {
    it("should return expected result when called for valid input") {
      val actual = Contents.ofURL {
        subject().openURL(validResourcePath)
      }
      assertResult(expectedContents)(actual)
    }

    it("should throw an IOException when called for invalid input") {
      assertThrows[IOException] {
        subject().openURL(invalidResourcePath)
      }
    }
  }

  describe("optURL(resource: String)") {
    it("should return Some(URL) when called for valid input") {
      val actual = Contents.ofURL{
        subject().optURL(validResourcePath)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input") {
      val actual = subject().optURL(invalidResourcePath)
      assert(actual.isEmpty)
    }
  }

  describe("optURL(resource: String, hint: String)") {
    it("should return Some(InputStream) when called for valid input and handled hint") {
      val actual = subject().optURL(validResourcePath, handledHint)
      assert(actual.isDefined)
      assert(actual.forall(Objects.isNull))
    }

    it("should return None when called for invalid input and handled hint") {
      val actual = subject().optURL(invalidResourcePath, handledHint)
      assert(actual.isDefined)
      assert(actual.forall(Objects.isNull))
    }

    it("should return None when called for valid input and unhandled hint") {
      val actual = Contents.ofURL {
        subject().optURL(validResourcePath, unhandledHint)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input and unhandled hint") {
      val actual = subject().optURL(invalidResourcePath, unhandledHint)
      assert(actual.isEmpty)
    }
  }

  describe("openInputStream(resource: String)") {
    it("should return expected result when called for valid input") {
      val actual = Contents.ofInputStream {
        subject().openInputStream(validResourcePath)
      }
      assertResult(expectedContents)(actual)
    }

    it("should throw an IOException when called for invalid input") {
      assertThrows[IOException] {
        subject().openInputStream(invalidResourcePath)
      }
    }
  }

  describe("optInputStream(resource: String)") {
    it("should return Some(InputStream) when called for valid input") {
      val actual = Contents.ofInputStream {
        subject().optInputStream(validResourcePath)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input") {
      val actual = subject().optInputStream(invalidResourcePath)
      assert(actual.isEmpty)
    }
  }

  describe("optInputStream(resource: String, hint: String)") {
    it("should return Some(InputStream) when called for valid input and handled hint") {
      val actual = subject().optInputStream(validResourcePath, handledHint)
      assert(actual.isDefined)
      assert(actual.forall(Objects.isNull))
    }

    it("should return None when called for invalid input and handled hint") {
      val actual = subject().optInputStream(invalidResourcePath, handledHint)
      assert(actual.isDefined)
      assert(actual.forall(Objects.isNull))
    }

    it("should return None when called for valid input and unhandled hint") {
      val actual = Contents.ofInputStream {
        subject().optInputStream(validResourcePath, unhandledHint)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input and unhandled hint") {
      val actual = subject().optInputStream(invalidResourcePath, unhandledHint)
      assert(actual.isEmpty)
    }
  }

  describe("optOutputStream(resource: String)") {
    it("should return expected result when called") {
      val actual = subject().optOutputStream(outputFilePath)
      assert(actual.isDefined)
      assert(actual.exists(Objects.nonNull))
    }
  }

  describe("optOutputStream(resource: String, hint: String)") {
    it("should return None when called for handled hint") {
      val actual = subject().optOutputStream(outputFilePath, handledHint)
      assert(actual.isDefined)
      assert(actual.exists(Objects.isNull))
    }

    it("should return None when called for unhandled hint") {
      val actual = subject().optOutputStream(outputFilePath, unhandledHint)
      assert(actual.isDefined)
      assert(actual.exists(Objects.nonNull))
    }
  }
}
