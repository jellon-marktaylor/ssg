package jellon.ssg.engine.flagship.hints

import jellon.ssg.io.impl.HintHandlers
import jellon.ssg.io.spi.IOutputStreamResources
import org.springframework.stereotype.Component

@Component
class FlagshipHintHandlers(resources: IOutputStreamResources) extends HintHandlers(Seq(
  new JavaHintHandler(resources)
)) {

}
