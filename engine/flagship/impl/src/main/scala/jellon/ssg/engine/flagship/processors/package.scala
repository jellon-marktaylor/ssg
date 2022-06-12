package jellon.ssg.engine.flagship

import jellon.ssg.node.api.INodeMap

package object processors {

  implicit class INodeMapProcessorExtensions(self: INodeMap) {
    def string(key: Any): String =
      self.attributeAs[String](key)

    def optString(key: Any): Option[String] =
      self.optAttributeAs[String](key)
  }

}
