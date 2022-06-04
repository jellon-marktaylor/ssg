package jellon.ssg.engine.flagship

import com.google.inject.{AbstractModule, Provider}

import scala.reflect.ClassTag

abstract class AbstractGuiceModule extends AbstractModule {
  def bindTo[A: ClassTag, B <: A : ClassTag]: Unit =
    bind(implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])
      .to(implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

  def bindToProvider[A: ClassTag](provider: Provider[_ <: A]): Unit =
    bind(implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])
      .toProvider(provider)

  def bindTo[A: ClassTag](impl: () => A): Unit =
    bindToProvider[A](() => impl())
}
