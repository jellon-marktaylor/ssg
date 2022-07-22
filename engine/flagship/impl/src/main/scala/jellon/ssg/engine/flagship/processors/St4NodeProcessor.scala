package jellon.ssg.engine.flagship.processors

import jellon.ssg.engine.flagship.api.IFlagshipEngine
import jellon.ssg.engine.flagship.spi.AbstractNodeProcessor
import jellon.ssg.engine.flagship.st4.helpers.{Groups, Templates}
import jellon.ssg.io.api.emptyString
import jellon.ssg.io.spi.IUrlResources
import jellon.ssg.node.api.INodeMap.NodeMapExt
import jellon.ssg.node.api.{INode, INodeMap}
import org.stringtemplate.v4._
import org.stringtemplate.v4.misc.STMessage
import sun.net.www.protocol.file.FileURLConnection

import java.io.{IOException, OutputStreamWriter}
import java.nio.file.NoSuchFileException
import scala.io.Source

/**
 * st4 <= name
 * @note stgroup (optional) the resource path and name of the stg file to use including the extension, or a directory of
 *       stg files
 * @note template (required) either the name of a template in stgroup or an ST4 template string if no stgroup
 * @note output (required) the file or directory (if hint is used) to write to
 * @note hint (optional) to dynamically pick the output file name (eg. java, csharp)
 * @note using (optional defaults to output) name of the defined state to pass to velocity
 * @note resolver (optional defaults to velocity) to resolve references in the instruction file
 */
object St4NodeProcessor extends AbstractNodeProcessor("st4") {
  @throws[IOException]
  override def execute(engine: IFlagshipEngine, state: INodeMap, key: Any, st4Node: INode): Unit = {
    val st4: INodeMap = state(name).attributes ++ st4Node.attributes
    val resolverName = st4.optAttributeAs[String]("resolver").getOrElse(name)
    val output = engine.resolve(resolverName, state, st4.string("output"))
    val hint = st4.optString("hint")
      .getOrElse(emptyString)

    val outputStream = engine.writeTo(output, hint)

    try {
      val stGroup = st4
        .optString("stgroup")
      val templateName = st4
        .string("template")
      val using = st4
        .optString("using")

      val attributes: INodeMap = using
        .map(usingString =>
          engine.resolve(resolverName, state, usingString)
        )
        .map(usingString =>
          state(usingString).attributes
        )
        .getOrElse(state)

      val resolvedAttributes = engine.resolver.resolveStringAttributes(resolverName, state, attributes)

      val errorHandler: STErrorListener = new STErrorListener() {
        override def compileTimeError(stMessage: STMessage): Unit =
          stMessage.cause.printStackTrace()

        override def runTimeError(stMessage: STMessage): Unit =
          stMessage.cause.printStackTrace()

        override def IOError(stMessage: STMessage): Unit =
          stMessage.cause.printStackTrace()

        override def internalError(stMessage: STMessage): Unit =
          stMessage.cause.printStackTrace()
      }

      val writer = new OutputStreamWriter(outputStream)
      try {
        bindTemplate(stGroup, templateName, resolvedAttributes, engine.resources)
          .write(new AutoIndentWriter(writer), errorHandler)
        writer.flush()
      } finally {
        writer.close()
      }
    } finally {
      if (outputStream != null) {
        outputStream.close()
      }
    }
  }

  @throws[IOException]
  private def bindTemplate(stgroup: Option[String], template: String, attributes: INodeMap, resources: IUrlResources): ST =
    Templates.bind(attributes, this.template(stgroup, template, resources))

  // TODO: this method is WAAAAAAAY too long and complicated. Chunk it
  @throws[IOException]
  private def template(stgroup: Option[String], template: String, resources: IUrlResources): ST =
    stgroup
      .map(fileName => {
        val url = resources.openURL(fileName)

        if (fileName.endsWith(".stg")) {
          Groups.configureSTGroup(new STGroupFile(url.getPath))
            .getInstanceOf(template)
        } else {
          url.openConnection() match {
            case _: FileURLConnection =>
              val file = new java.io.File(System.getProperty("user.dir"), url.getPath)
              if (!file.exists()) {
                // well this is odd. Do we even need this code?
                throw new NoSuchFileException(fileName)
              } else if (file.isDirectory) {
                Groups.configureSTGroup(new STGroupDir(fileName))
                  .getInstanceOf(template)
              } else {
                val src = Source.fromFile(file)
                try {
                  val contents = src.mkString
                  Templates.stringTemplate(contents)
                } finally if (src != null) src.close()
              }
            case _ =>
              val src = Source.fromURL(url)
              try {
                val contents = src.mkString
                Templates.stringTemplate(contents)
              } finally if (src != null) src.close()
          }
        }
      })
      .getOrElse(Templates.stringTemplate(template))
}
