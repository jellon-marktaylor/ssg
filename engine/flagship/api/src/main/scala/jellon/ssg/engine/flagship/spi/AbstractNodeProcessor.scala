package jellon.ssg.engine.flagship.spi

import jellon.ssg.node.api.INode

class AbstractNodeProcessor(val name: String) extends INodeProcessor {
  override def handles(key: Any, node: INode): Boolean =
    key == name
}
