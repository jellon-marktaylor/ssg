package jellon.ssg.engine.flagship.hints

import jellon.ssg.io.api.IHintHandler
import jellon.ssg.io.spi.IOutputStreamResources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.io.OutputStream
import javax.inject.Inject

@Component
@Autowired
@Inject
class JavaHintHandler(resources: IOutputStreamResources) extends IHintHandler {
  override def optOutputStream(resource: String, hint: String): Option[OutputStream] =
    if (hint.contains("java")) Some(new JavaOutputStream(resources, resource))
    else Option.empty
}
