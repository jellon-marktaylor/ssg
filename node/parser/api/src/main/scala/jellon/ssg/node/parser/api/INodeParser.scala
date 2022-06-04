package jellon.ssg.node.parser.api

import jellon.ssg.node.api.INode

import java.io.IOException

trait INodeParser {
  /**
   * @param resourceName to be parsed
   * @return provide a guess as to if the resource can be parsed, if a guess is available
   */
  def isDefinedAt(resourceName: String): Option[Boolean] = Option.empty

  /**
   * @param resourceName to be parsed
   * @throws IOException if something could not be read
   * @return either the parsed [[INode]] or [[INode.empty]] if the resource was in the wrong format
   */
  @throws[IOException]
  def parse(resourceName: String): INode
}
