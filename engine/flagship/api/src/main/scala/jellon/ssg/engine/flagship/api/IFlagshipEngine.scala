package jellon.ssg.engine.flagship.api

import jellon.ssg.engine.flagship.spi.IResolverFactory
import jellon.ssg.io.spi.IResources
import jellon.ssg.node.api.{INode, INodeMap}

import java.io.{InputStream, OutputStream}

object IFlagshipEngine {

  implicit class IFlagshipEngineExtensions(self: IFlagshipEngine) {
    def resolveNode(name: String, dictionary: INodeMap, rawText: String): INode =
      self.resolver.asNode(name, dictionary, rawText)

    def resolveNode(name: String, dictionary: INode, rawText: String): INode =
      resolveNode(name, dictionary.attributes, rawText)

    def resolve(name: String, dictionary: INodeMap, rawText: String): String =
      self.resolver.asText(name, dictionary, rawText)

    def resolve(name: String, dictionary: INode, rawText: String): String =
      resolve(name, dictionary.attributes, rawText)

    def resolveNodeString(name: String, dictionary: INodeMap, unresolvedText: String): String =
      resolve(name, dictionary, resolveNode(dictionary, unresolvedText).valueAs[String])

    def resolveNodeString(name: String, dictionary: INode, unresolvedText: String): String =
      resolveNodeString(name, dictionary.attributes, unresolvedText)

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
 * Usually used by [[jellon.ssg.engine.flagship.api.IFlagshipApplication]] to kick off an SSG application instance
 */
trait IFlagshipEngine {
  def resources: IResources

  def resolver: IResolverFactory

  /**
   * This method has 2 primary use-cases:
   * <br/>1) [[jellon.ssg.engine.flagship.api.IFlagshipApplication]] to kick off an SSG application instance
   * <br/>2) an implementation of [[jellon.ssg.engine.flagship.spi.INodeProcessor]] to call sub-nodes
   *
   * @see [[jellon.ssg.engine.flagship.spi.INodeProcessor.INodeProcessorExt#process(jellon.ssg.node.api.INodeMap, java.lang.Object, jellon.ssg.node.api.INode, jellon.ssg.engine.flagship.api.IFlagshipEngine)]]
   * @see [[jellon.ssg.engine.flagship.api.IFlagshipApplication.IFlagshipApplicationExt]]
   */
  def process(state: INodeMap, key: Any, node: INode): INodeMap
}
