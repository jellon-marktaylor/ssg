package jellon.ssg.io.impl

import grizzled.slf4j.Logging
import jellon.ssg.io.Contents
import org.scalatest.funspec.AnyFunSpec

import java.io.IOException

object InputStreamResourcesTests {
  // this is an immutable class, so it's fine to share for all tests
  val subject: InputStreamResources = new InputStreamResources(baseDir)
}

class InputStreamResourcesTests extends AnyFunSpec with Logging {

  import InputStreamResourcesTests._

  describe("InputStreamResources.optInputStream(baseDir: File, resource: String)") {
    it("should return Some(InputStream) when called for valid input") {
      val actual = Contents.ofInputStream {
        InputStreamResources.optInputStream(baseDir, validResourcePath)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input") {
      val actual = InputStreamResources.optInputStream(baseDir, invalidResourcePath)
      assert(actual.isEmpty)
    }
  }

  describe("InputStreamResources.optFileInputStream(file: File)") {
    it("should return Some(InputStream) when called for valid input") {
      val actual = Contents.ofInputStream {
        InputStreamResources.optFileInputStream(validFile)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input") {
      val actual = InputStreamResources.optFileInputStream(invalidFile)
      assert(actual.isEmpty)
    }
  }

  describe("InputStreamResources.optFileInputStream(baseDir: File, resource: String)") {
    it("should return Some(InputStream) when called for valid input") {
      val actual = Contents.ofInputStream {
        InputStreamResources.optFileInputStream(baseDir, validResourcePath)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input") {
      val actual = InputStreamResources.optFileInputStream(baseDir, invalidResourcePath)
      assert(actual.isEmpty)
    }
  }

  describe("openInputStream(resource: String)") {
    it("should return expected result when called for valid input") {
      val actual = Contents.ofInputStream {
        subject.openInputStream(validResourcePath)
      }
      assertResult(expectedContents)(actual)
    }

    it("should throw an IOException when called for invalid input") {
      assertThrows[IOException] {
        subject.openInputStream(invalidResourcePath)
      }
    }
  }

  describe("optInputStream(resource: String)") {
    it("should return Some(InputStream) when called for valid input") {
      val actual = Contents.ofInputStream {
        subject.optInputStream(validResourcePath)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input") {
      val actual = subject.optInputStream(invalidResourcePath)
      assert(actual.isEmpty)
    }
  }
}
