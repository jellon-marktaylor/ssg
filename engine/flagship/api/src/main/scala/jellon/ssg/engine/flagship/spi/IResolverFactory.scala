package jellon.ssg.engine.flagship.spi

import jellon.ssg.node.api.{INode, INodeMap}

object IResolverFactory {

  implicit class IResolverFactoryExt(self: IResolverFactory) {
    def resolver(dictionary: INodeMap): IResolver =
      self.defaultResolver(dictionary)

    def resolver(name: String, dictionary: INodeMap): IResolver =
      self.namedResolver(name, dictionary)

    def asNode(name: String, dictionary: INodeMap, rawText: String): INode =
      self.namedResolver(name, dictionary).asNode(rawText)

    def asNode(name: String, dictionary: INodeMap, rawNode: INode): INode =
      self.namedResolver(name, dictionary).asNode(rawNode)

    def asText(name: String, dictionary: INodeMap, rawText: String): String =
      self.namedResolver(name, dictionary).asText(rawText)

    /** if rawNode is a ValueNode(string) then we return Node(asText(string)) else we return rawNode */
    def resolveIfStringNode(name: String, dictionary: INodeMap, node: INode): INode =
      self.namedResolver(name, dictionary).resolveIfStringNode(node)

    /** call [[resolveIfStringNode(INode)]] on each node in the NodeMap. As a result, each ValueNode(string) will be resolved if it contained a template */
    def resolveStringAttributes(name: String, dictionary: INodeMap, node: INodeMap): INodeMap =
      self.namedResolver(name, dictionary).resolveStringAttributes(node)

    def asNode(dictionary: INodeMap, rawText: String): INode =
      self.defaultResolver(dictionary).asNode(rawText)

    def asNode(dictionary: INodeMap, rawNode: INode): INode =
      self.defaultResolver(dictionary).asNode(rawNode)

    def asText(dictionary: INodeMap, rawText: String): String =
      self.defaultResolver(dictionary).asText(rawText)

    /** if rawNode is a ValueNode(string) then we return Node(asText(string)) else we return rawNode */
    def resolveIfStringNode(dictionary: INodeMap, node: INode): INode =
      self.defaultResolver(dictionary).resolveIfStringNode(node)

    /** call [[resolveIfStringNode(INode)]] on each node in the NodeMap. As a result, each ValueNode(string) will be resolved if it contained a template */
    def resolveStringAttributes(dictionary: INodeMap, node: INodeMap): INodeMap =
      self.defaultResolver(dictionary).resolveStringAttributes(node)
  }

}

trait IResolverFactory {
  def defaultResolver(dictionary: INodeMap): IResolver

  def namedResolver(name: String, dictionary: INodeMap): IResolver
}
