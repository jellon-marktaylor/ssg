package jellon.ssg.io.impl

import grizzled.slf4j.Logging
import jellon.ssg.io.Contents
import org.scalatest.funspec.AnyFunSpec

import java.io.IOException

object UrlResourcesTests {
  // this is an immutable class, so it's fine to share for all tests
  val subject: UrlResources = new UrlResources(baseDir)
}

class UrlResourcesTests extends AnyFunSpec with Logging {

  import UrlResourcesTests._

  describe("UrlResources.optURL(baseDir: File, resource: String)") {
    it("should return Some(URL) when called for valid input") {
      val actual = Contents.ofURL {
        UrlResources.optURL(baseDir, validResourcePath)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input") {
      val actual = UrlResources.optURL(baseDir, invalidResourcePath)
      assert(actual.isEmpty)
    }
  }

  describe("UrlResources.optFileURL(file: File)") {
    it("should return Some(URL) when called for valid input") {
      val actual = Contents.ofURL {
        UrlResources.optFileURL(validFile)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input") {
      val actual = UrlResources.optFileURL(invalidFile)
      assert(actual.isEmpty)
    }
  }

  describe("UrlResources.optFileURL(baseDir: File, resource: String)") {
    it("should return Some(URL) when called for valid input") {
      val actual = Contents.ofURL {
        UrlResources.optFileURL(baseDir, validResourcePath)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input") {
      val actual = UrlResources.optFileURL(baseDir, invalidResourcePath)
      assert(actual.isEmpty)
    }
  }

  describe("UrlResources.optSystemClassLoaderResource(resource: String)") {
    it("should return Some(URL) when called for valid input") {
      val actual = UrlResources.optSystemClassLoaderResource(validResourcePath)
      if (ClassLoader.getSystemClassLoader.getResources(validResourcePath).hasMoreElements) {
        val actualContents = Contents.ofURL {
          actual
        }
        assert(actualContents.contains(expectedContents))
      } else {
        logger.warn(s"missing $validResourcePath")
        assert(actual.isEmpty)
      }
    }

    it("should return None when called for invalid input") {
      val actual = UrlResources.optSystemClassLoaderResource(invalidResourcePath)
      assert(actual.isEmpty)
    }
  }

  describe("UrlResources.optSystemResource(resource: String)") {
    it("should return Some(URL) when called for valid input") {
      val actual = UrlResources.optSystemResource(validResourcePath)
      if (ClassLoader.getSystemResources(validResourcePath).hasMoreElements) {
        val actualContents = Contents.ofURL {
          actual
        }
        assert(actualContents.contains(expectedContents))
      } else {
        logger.warn(s"missing $validResourcePath")
        assert(actual.isEmpty)
      }
    }

    it("should return None when called for invalid input") {
      val actual = UrlResources.optSystemResource(invalidResourcePath)
      assert(actual.isEmpty)
    }
  }

  describe("openURL(resource: String)") {
    it("should return expected result when called for valid input") {
      val actual = Contents.ofURL {
        subject.openURL(validResourcePath)
      }
      assertResult(expectedContents)(actual)
    }

    it("should throw an IOException when called for invalid input") {
      assertThrows[IOException] {
        subject.openURL(invalidResourcePath)
      }
    }
  }

  describe("optURL(resource: String)") {
    it("should return Some(URL) when called for valid input") {
      val actual = Contents.ofURL {
        subject.optURL(validResourcePath)
      }
      assert(actual.contains(expectedContents))
    }

    it("should return None when called for invalid input") {
      val actual = subject.optURL(invalidResourcePath)
      assert(actual.isEmpty)
    }
  }
}
