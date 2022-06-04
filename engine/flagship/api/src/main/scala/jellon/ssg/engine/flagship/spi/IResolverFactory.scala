package jellon.ssg.engine.flagship.spi

import jellon.ssg.node.api.{INode, INodeMap}

object IResolverFactory {

  implicit class IResolverFactoryExt(self: IResolverFactory) {
    def asNode(dictionary: INodeMap, rawText: String): INode =
      self.resolver(dictionary).asNode(rawText)

    def asNode(dictionary: INodeMap, rawNode: INode): INode =
      self.resolver(dictionary).asNode(rawNode)

    def asText(dictionary: INodeMap, rawText: String): String =
      self.resolver(dictionary).asText(rawText)

    /** if rawNode is a ValueNode(string) then we return Node(asText(string)) else we return rawNode */
    def resolveIfStringNode(dictionary: INodeMap, node: INode): INode =
      self.resolver(dictionary).resolveIfStringNode(node)

    /** call [[resolveIfStringNode(INode)]] on each node in the NodeMap. As a result, each ValueNode(string) will be resolved if it contained a template */
    def resolveStringAttributes(dictionary: INodeMap, node: INodeMap): INodeMap =
      self.resolver(dictionary).resolveStringAttributes(node)
  }

}

trait IResolverFactory {
  def resolver(dictionary: INodeMap): IResolver
}
