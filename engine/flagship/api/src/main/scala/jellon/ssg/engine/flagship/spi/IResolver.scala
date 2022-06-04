package jellon.ssg.engine.flagship.spi

import jellon.ssg.node.api.{INode, INodeMap}

object IResolver {

  implicit class IResolverExt(self: IResolver) {
    def resolve[A](f: IResolver => A): A = f(self)

    /** if rawNode is a ValueNode(string) then we return asNode(string) else we return rawNode */
    def asNode(rawNode: INode): INode =
      rawNode
        .optValueAs[String]
        .map(path =>
          self.asNode(path)
        )
        .getOrElse(rawNode)

    /** if rawNode is a ValueNode(string) then we return Node(asText(string)) else we return rawNode */
    def resolveIfStringNode(rawNode: INode): INode =
      rawNode.optValueAs[String]
        .map(unresolvedText => {
          val resolvedText = self.asText(unresolvedText)
          if (resolvedText == unresolvedText) rawNode
          else rawNode.setValue(resolvedText)
        })
        .getOrElse(rawNode)

    /** call [[resolveIfStringNode(INode)]] on each node in the NodeMap. As a result, each ValueNode(string) will be resolved if it contained a template */
    def resolveStringAttributes(node: INodeMap): INodeMap =
      node.keySet.foldLeft(node)((accumulator, key) =>
        accumulator.replaceAttribute(key, cur =>
          resolveIfStringNode(cur)
        )
      )
  }

}

trait IResolver {
  /**
   * @param rawText template string to be resolved
   * @return rendered text from template
   */
  def asText(rawText: String): String

  /**
   * @param path to a value held by this IResolver
   * @return the node for the path or INode.empty (should not return null)
   */
  def asNode(path: String): INode
}
