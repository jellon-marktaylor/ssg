package jellon.ssg.engine.flagship

import jellon.ssg.engine.flagship.api.{IFlagshipApplication, IFlagshipEngine, INodeProcessors}
import jellon.ssg.engine.flagship.spi.{INodeProcessor, IResolverFactory}
import jellon.ssg.io.spi.IResources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.inject.Inject
import scala.jdk.CollectionConverters._

/** There is an interactions among [[FlagshipApplication]], [[INodeProcessor]], and [[IFlagshipEngine]].
 * [[FlagshipApplication]] is annotated for DI via javax (eg Guice) or Spring. It's primary constructor accepts
 * a [[java.util.List]] of [[INodeProcessor]]. It then groups the processors by calling [[INodeProcessor.path]]
 * where root processor(s) path is expected to be "". This is the default in [[AbstractNodeProcessor]], so it
 * need not be memorized.
 *
 * A processor is given an instance of [[INodeProcessorState]] and [[IFlagshipEngine]]. The state is immutable,
 * thus it's thread-safe and reusable by multiple processors. The chain allows access to other defined
 * processors by path. It's likely there will only be one processor for each path, but this framework allows
 * for more flexibility for custom applications. If needed, your DI container of choice may be customized.
 *
 * The following example is implemented in the test source code (except without unimplemented "other" node)
 * {{{
 * {
 *   "foo": {
 *     "dictionary": {
 *       "a": 1,
 *       "b": 3
 *     },
 *     "list": [ "world", "ignored", "bar" ],
 *     "bar": {
 *       "bat": {
 *         "hello": "a"
 *         "foo": "b"
 *       },
 *       "other": {
 *       }
 *     }
 *   }
 * }
 * }}}
 *
 * @example {{{
 * @org.springframework.stereotype.Component
 * RootNodeProcessor extends ModelNodeProcessor
 *
 * @org.springframework.stereotype.Component
 * object FooNodeProcessor extends AbstractNodeProcessor("foo") {
 *   def process(state: INodeProcessorState, chain: INodeProcessorChain): INodeProcessorState = {
 *     val dictNode: INode = chain.chainAll("dictionary", state.updateModel(_.attribute("dictionary"))).bindings
 *     val listNode: INode = chain.chainAll("list", state.updateModel(_.attribute("list"))).bindings
 *
 *     val newState = state
 *       .updateBindings(_ ++ dictNode ++ listNode)
 *       .updateModel(_.attribute("bar"))
 *
 *     chain.chain("bar", newState)
 *     newState
 *   }
 * }
 *
 * @org.springframework.stereotype.Component
 * object DictionaryNodeProcessor extends AbstractNodeProcessor("foo/dictionary") {
 *   override def process(state: INodeProcessorState, chain: INodeProcessorChain): INodeProcessorState =
 *     state.updateBindings(_.withAttribute("dict", state.model))
 * }
 *
 * @org.springframework.stereotype.Component
 * object ListNodeProcessor extends AbstractNodeProcessor("foo/list") {
 *   override def process(state: INodeProcessorState, chain: INodeProcessorChain): INodeProcessorState =
 *     state.updateBindings(_.withAttribute("list", state.model))
 * }
 *
 * @org.springframework.stereotype.Component
 * object BarNodeProcessor extends ModelNodeProcessor("foo/bar")
 *
 * @org.springframework.stereotype.Component
 * object BatNodeProcessor extends AbstractNodeProcessor("foo/bar/bat") {
 *   def process(state: INodeProcessorState, chain: INodeProcessorChain): INodeProcessorState = {
 *     val dictNode = state.bindings.attribute("dict") // ("a" -> 1), ...
 *     val listNode = state.bindings.attribute("list") // "li1", ...
 *     state.model.attributeNames.foreach(barAttribute => {
 *       val barValue = state.model.attributeAs[String](barAttribute) // "a" | "b"
 *       val index: Int = dictNode.attributeAs[Integer](barValue) // 1 | 2 | 3
 *       val listValue = listNode.children.drop(index - 1).head.value // "world" | "bar"
 *       output = output :+ s"$barAttribute -> $listValue" // hello -> world | foo -> bar
 *     })
 *
 *     state
 *   }
 * }
 *
 * @org.springframework.stereotype.Component
 * object BatNodeProcessor extends AbstractNodeProcessor("foo/bar/other") {
 *   def process(state: INodeProcessorState, chain: INodeProcessorChain): INodeProcessorState = ???
 * }
 * }}}
 */
@Component
class FlagshipApplication(resources: IResources, resolver: IResolverFactory, processors: Seq[INodeProcessor])
    extends IFlagshipApplication {

  @Autowired
  def this(resources: IResources, resolver: IResolverFactory, delegates: java.util.List[INodeProcessor]) =
    this(resources, resolver, delegates.asScala.toSeq)

  @Inject
  def this(resources: IResources, resolver: IResolverFactory, delegates: INodeProcessors) =
    this(resources, resolver, delegates())

  override lazy val createEngine: IFlagshipEngine =
    new FlagshipEngine(resources, resolver, processors)
}
