package jellon.ssg.engine.flagship

import jellon.ssg.node.api.INodeMap

package object processors {

  implicit class INodeMapProcessorExtensions(self: INodeMap) {
    def string(key: AnyRef): String =
      self.attributeAs[String](key)

    def optString(key: AnyRef): Option[String] =
      self.optAttributeAs[String](key)
  }

}
