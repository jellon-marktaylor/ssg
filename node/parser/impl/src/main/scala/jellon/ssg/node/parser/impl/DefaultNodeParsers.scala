package jellon.ssg.node.parser.impl

import jellon.ssg.io.spi.IInputStreamResources
import jellon.ssg.node.parser.api.{INodeParser, INodeParsers}
import jellon.ssg.node.parser.impl.json.JsonParser
import jellon.ssg.node.parser.impl.xml.XmlParser
import jellon.ssg.node.parser.impl.yaml.YamlParser
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component
class DefaultNodeParsers extends INodeParsers {
  @Inject var resourceReader: IInputStreamResources = null

  @Inject
  def this(resourceReader: IInputStreamResources) {
    this()
    this.resourceReader = resourceReader
  }

  override def apply(): Seq[INodeParser] = Seq[INodeParser](
    json,
    xml,
    yaml
  )

  def json: INodeParser = new JsonParser(resourceReader)

  def xml: INodeParser = new XmlParser(resourceReader)

  def yaml: INodeParser = new YamlParser(resourceReader)
}
