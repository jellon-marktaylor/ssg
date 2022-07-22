package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.{IFlagshipApplication, INodeProcessors}
import jellon.ssg.engine.flagship.spi.{INodeProcessor, IResolverFactory}
import jellon.ssg.io.IResetable
import jellon.ssg.io.spi.IResources
import jellon.ssg.node.api.{INode, INodeList, INodeMap}
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Assertions, Checkpoints}

import java.io.ByteArrayOutputStream

object FlagshipTestApplication extends Checkpoints with Matchers with Assertions {
  def assertNodeResult(expected: INode)(actual: INode): Unit = {
    val checkPoint = new Checkpoint
    assertNodeResult(checkPoint, Option.empty, expected, actual)
    checkPoint.reportAll()
  }

  def assertNodeResult(expected: INodeList)(actual: INodeList): Unit = {
    val checkPoint = new Checkpoint
    assertNodeListResult(checkPoint, Option.empty, expected, actual)
    checkPoint.reportAll()
  }

  def assertNodeResult(expected: INodeMap)(actual: INodeMap): Unit = {
    val checkPoint = new Checkpoint
    assertNodeMapResult(checkPoint, Option.empty, expected, actual)
    checkPoint.reportAll()
  }

  private def assertNodeResult(checkPoint: Checkpoint, path: Option[String], lhs: INode, rhs: INode): Unit = {
    checkPoint {
      assertResult(lhs.optValue, path.getOrElse("<root>"))(rhs.optValue)
    }
    assertNodeListResult(checkPoint, path, lhs.children, rhs.children)
    assertNodeMapResult(checkPoint, path, lhs.attributes, rhs.attributes)
  }

  private def assertNodeListResult(checkPoint: Checkpoint, path: Option[String], lhs: INodeList, rhs: INodeList): Unit = {
    if (lhs.size != rhs.size) {
      assertResult(lhs, path.getOrElse("<root>"))(rhs)
    } else if (lhs.nonEmpty) {
      lhs
        .toSeq
        .zip(rhs.toSeq)
        .zipWithIndex
        .foreach(data => {
          val (nodes, index) = data
          val (expected, actual) = nodes
          val clue = path
            .map(a => s"$a[$index]")
            .getOrElse(index)

          checkPoint {
            assertResult(expected, clue)(actual)
          }
        })
    }
  }

  private def assertNodeMapResult(checkPoint: Checkpoint, path: Option[String], lhs: INodeMap, rhs: INodeMap): Unit = {
    if (!lhs.isEmpty || !rhs.isEmpty) {
      val keys = lhs.keySet ++ rhs.keySet
      keys.foreach(key => {
        val leftChild = lhs.attribute(key)
        val rightChild = rhs.attribute(key)
        val newPath = path
          .map(a => s"$a.$key")
          .orElse(Some(String.valueOf(key)))

        assertNodeResult(checkPoint, newPath, leftChild, rightChild)
      })
    }
  }
}

class FlagshipTestApplication(engine: FlagshipTestEngine) extends IFlagshipApplication with IResetable {
  def this(resources: IResources, resolver: IResolverFactory, processors: Seq[INodeProcessor]) =
    this(new FlagshipTestEngine(resources, resolver, processors))

  def this(resources: IResources, resolver: IResolverFactory, processors: INodeProcessors) =
    this(new FlagshipTestEngine(resources, resolver, processors))

  def this(processors: Seq[INodeProcessor]) =
    this(new FlagshipTestEngine(processors))

  def this(processors: INodeProcessors) =
    this(new FlagshipTestEngine(processors))

  /**
   * Uses default values. The IResources will implement IResetable and delegate the call to an instance of ByteArrayOutputStreamResources
   */
  def this() =
    this(new FlagshipTestEngine())

  override def createEngine: FlagshipTestEngine =
    engine

  override def reset(): Unit = {
    engine.reset()
  }

  def optTestOutputAsStream(resource: String): Option[ByteArrayOutputStream] =
    engine.optTestOutputAsStream(resource)

  def optTestOutput(resource: String): Option[String] =
    engine.optTestOutput(resource)

  def lookupTestOutputOrEmpty(resource: String): String =
    engine.lookupTestOutputOrEmpty(resource)

  def assertNodeResult(expected: INode)(actual: INode): Unit =
    FlagshipTestApplication.assertNodeResult(expected)(actual)

  def assertNodeResult(expected: INodeList)(actual: INodeList): Unit =
    FlagshipTestApplication.assertNodeResult(expected)(actual)

  def assertNodeResult(expected: INodeMap)(actual: INodeMap): Unit =
    FlagshipTestApplication.assertNodeResult(expected)(actual)
}
