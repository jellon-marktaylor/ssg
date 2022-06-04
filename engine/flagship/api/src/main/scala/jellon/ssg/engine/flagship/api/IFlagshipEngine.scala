package jellon.ssg.engine.flagship.api

import jellon.ssg.engine.flagship.spi.IResolverFactory
import jellon.ssg.io.spi.IResources
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.{Node, NodeMap}

import java.io.{InputStream, OutputStream}

object IFlagshipEngine {
  val BASE_PATH: String = ""

  val INSTRUCTIONS: String = "instructions"

  val INPUT: String = "input"

  val OUTPUT: String = "output"

  def instructionsNodeMap(value: INode): INodeMap =
    new NodeMap(Map[Any, INode](INSTRUCTIONS -> value))

  def inputNodeMap(value: INode): INodeMap =
    new NodeMap(Map[Any, INode](INPUT -> value))

  def outputNodeMap(value: INode): INodeMap =
    new NodeMap(Map[Any, INode](OUTPUT -> value))

  implicit class IFlagshipNodeMapExtensions(self: INodeMap) {
    def instructions: INode = self(INSTRUCTIONS)

    def input: INode = self(INPUT)

    def output: INode = self(OUTPUT)
  }

  implicit class IFlagshipEngineExtensions(self: IFlagshipEngine) {
    def process(name: String, state: INode): INodeMap = self
      .process(name, state.attributes)

    def processInstructions(name: String, state: INodeMap, instructions: INode): INodeMap = self
      .process(name, state.setAttribute(INSTRUCTIONS, instructions))

    def processInstructions(name: String, state: INodeMap, instructions: INodeMap): INodeMap =
      processInstructions(name, state, Node(instructions))

    def resolveNode(dictionary: INodeMap, rawText: String): INode =
      self.resolver.asNode(dictionary, rawText)

    def resolveNode(dictionary: INode, rawText: String): INode =
      resolveNode(dictionary.attributes, rawText)

    def resolve(dictionary: INodeMap, rawText: String): String =
      self.resolver.asText(dictionary, rawText)

    def resolve(dictionary: INode, rawText: String): String =
      resolve(dictionary.attributes, rawText)

    def resolveNodeString(dictionary: INodeMap, unresolvedText: String): String =
      resolve(dictionary, resolveNode(dictionary, unresolvedText).valueAs[String])

    def resolveNodeString(dictionary: INode, unresolvedText: String): String =
      resolveNodeString(dictionary.attributes, unresolvedText)

    def readFrom(resource: String): InputStream = self
      .resources.openInputStream(resource)

    def readFrom(resource: String, hint: String): InputStream = self
      .resources.openInputStream(resource, hint)

    def writeTo(resource: String): OutputStream = self
      .resources.openOutputStream(resource)

    def writeTo(resource: String, hint: String): OutputStream = self
      .resources.openOutputStream(resource, hint)
  }

}

/**
 * [[NodeProcessorHandler]]
 *
 * @see jellon.ssg.engine.flagship.NodeProcessorHandler
 */
trait IFlagshipEngine {
  def process(name: String, state: INodeMap): INodeMap

  def resolver: IResolverFactory

  def resources: IResources
}
