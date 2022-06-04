package jellon.ssg.io.impl

import jellon.ssg.io.spi.IOutputStreamResources
import org.scalatest.funspec.AnyFunSpec

import java.io.{File, IOException, OutputStream}
import java.util.Objects

object OutputStreamResourcesTests {
  val workingDir = new File(baseDir, "target")

  // this is an immutable class, so it's fine to share for all tests
  val subject: IOutputStreamResources = new OutputStreamResources(workingDir)

  val outputFilePath: String = "deleteme.txt"

  val invalidDir: File = new File("::\\/")

  def cleanup(): Unit = {
    val testFile = new File(workingDir, outputFilePath)
    testFile.delete()

    val targetDir = testFile.getParentFile
    if (Option(targetDir).map(_.listFiles()).filter(Objects.nonNull).forall(_.isEmpty)) {
      targetDir.delete()
    }
  }

  def test(input: => OutputStream): Unit = {
    cleanup()
    val actual = input
    try {
      assert(actual != null)
    } finally {
      if (actual != null) {
        actual.close()
      }

      cleanup()
    }
  }

  def testOpt(input: => Option[OutputStream]): Unit = {
    val actual = input
    assert(actual.isDefined)
    test {
      actual.get
    }
  }
}

class OutputStreamResourcesTests extends AnyFunSpec {

  import OutputStreamResourcesTests._

  describe("OutputStreamResources.optFileOutputStream(baseDir: File, resource: String)") {
    it("should return expected result when called for valid input") {
      testOpt {
        OutputStreamResources.optFileOutputStream(workingDir, outputFilePath)
      }
    }

    it("should throw an IOException when is called for invalid input") {
      assertThrows[IOException] {
        OutputStreamResources.optFileOutputStream(invalidDir, outputFilePath)
      }
    }
  }

  describe("OutputStreamResources.optFileOutputStream(file: File)") {
    it("should return expected result when called for valid input") {
      testOpt {
        OutputStreamResources.optFileOutputStream(new File(workingDir, outputFilePath))
      }
    }

    it("should throw an IOException when called for invalid input") {
      assertThrows[IOException] {
        OutputStreamResources.optFileOutputStream(invalidDir)
      }
    }
  }

  describe("new OutputStreamResources(baseDir: File)") {
    it("should throw an IOException when constructed with missing directory") {
      assertThrows[IOException] {
        new OutputStreamResources(invalidDir)
      }
    }
  }

  describe("openOutputStream(resource: String)") {
    it("should return expected result when is called") {
      test {
        subject.openOutputStream(outputFilePath)
      }
    }
  }

  describe("optOutputStream(resource: String)") {
    it("should return expected result when called") {
      testOpt {
        subject.optOutputStream(outputFilePath)
      }
    }
  }
}
