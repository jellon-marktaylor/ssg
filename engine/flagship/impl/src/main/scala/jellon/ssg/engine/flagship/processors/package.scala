package jellon.ssg.engine.flagship

import jellon.ssg.node.api.INode.NodeExt
import jellon.ssg.node.api.INodeMap

package object processors {

  implicit class INodeMapProcessorExtensions(self: INodeMap) {
    def string(key: Any): String = self.apply(key)
      .valueAs[String]

    def optString(key: Any): Option[String] = self.optAttribute(key)
      .flatMap(_.optValueAs[String])
  }

}
