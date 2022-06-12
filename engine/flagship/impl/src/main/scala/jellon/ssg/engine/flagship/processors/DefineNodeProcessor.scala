package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.{AbstractNodeProcessor, INodeProcessor}
import jellon.ssg.node.api.{INode, INodeMap}
import jellon.ssg.node.spi.{MapNode, Node}

/** for each key in the "define" node, copy it's attributes to the same key in the state
 * so for `"define": { "name": { "key": "value" } }` ; define "name" as a node in state, if it doesn't already exist
 * and copy "key" -> "value" into it. Other processors may then access `state.apply("name").attribute("key")`.
 */
object DefineNodeProcessor extends AbstractNodeProcessor("define") {
  override def propagateOutput: Boolean =
    true

  override def output(state: INodeMap, key: Any, defineNode: INode, engine: IFlagshipEngine): INode =
    defineNode.keySet
      .foldLeft(INode.empty)((r, nameOfNodeToAddAttributesTo) => {
        val curAttrs: INode = state.attribute(nameOfNodeToAddAttributesTo)
        val newAttrs: INode = defineNode.attribute(nameOfNodeToAddAttributesTo)
        val mergedNode: INode = mergeNodes(Node(state) ++ r, engine, curAttrs, newAttrs)
        r.setAttribute(nameOfNodeToAddAttributesTo, mergedNode)
      })

  private def mergeNodes(state: INode, engine: IFlagshipEngine, curAttrs: INode, newAttrs: INode): INode = {
    val resolved: INode =
      newAttrs match {
        case mapNode: MapNode =>
          resolveStringValues(state, engine, mapNode)
        case node =>
          node
      }

    curAttrs ++ resolved
  }

  private def resolveStringValues(state: INode, engine: IFlagshipEngine, value: INode): INode =
    value.keySet.foldLeft(INode.empty)((accumulator, key) =>
      if ("*" == key) {
        val nameOfNode = value.attributeAs[String](key)
        val rawNode = engine.resolveNode(state, nameOfNode)
        mergeNodes(state, engine, accumulator, rawNode)
      } else {
        val rawNode = value.attribute(key)
        if (accumulator.optAttribute(key).isDefined) {
          val resolvedNode = resolveStringIfStringAttributeNode(state, engine, rawNode)
          accumulator.replaceAttribute(key, _ => resolvedNode)
        } else {
          accumulator.setAttribute(key, rawNode)
        }
      }
    )

  private def resolveStringIfStringAttributeNode(state: INode, engine: IFlagshipEngine, value: INode): INode =
    value.optValueAs[String]
      .map(stringValue => {
        val resolvedNode = engine.resolveNode(state, stringValue)
        if (resolvedNode == INode.empty) {
          val newValue = engine.resolve(state, stringValue)
          value.setValue(newValue)
        } else {
          resolvedNode
            .optValue
            .map {
              case str: String =>
                val newValue = engine.resolve(state, str)
                value.setValue(newValue)
              case _ =>
                resolvedNode
            }
            .getOrElse(resolvedNode)
        }
      })
      .getOrElse(
        value
      )
}
