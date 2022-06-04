package jellon.ssg.io.api

import org.scalatest.funspec.AnyFunSpec

object IHintHandlerTests {
  private val subject = new IHintHandler {}
}

class IHintHandlerTests extends AnyFunSpec {

  import IHintHandlerTests.subject

  describe("IHintHandler.optURL(String, String)") {
    it("should return None by default") {
      assertResult(subject.optURL("", ""))(Option.empty)
    }
  }

  describe("IHintHandler.optInputStream(String, String)") {
    it("should return None by default") {
      assertResult(subject.optInputStream("", ""))(Option.empty)
    }
  }

  describe("IHintHandler.optOutputStream(String, String)") {
    it("should return None by default") {
      assertResult(subject.optOutputStream("", ""))(Option.empty)
    }
  }
}
