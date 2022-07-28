package jellon.ssg.node.api

import scala.reflect.ClassTag

trait IValueNode[Self <: IValueNode[Self]] {
  def hasValue: Boolean = this
    .optValue
    .isDefined

  def optValue: Option[_ <: AnyRef]

  def optValueAs[B](clz: Class[B]): Option[B] = this
    .optValue
    .filter(clz.isInstance)
    .map(clz.cast)

  def optValueAs[B: ClassTag]: Option[B] = this
    .optValueAs(implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

  @throws[NoSuchElementException]
  def value: AnyRef = this
    .optValue match {
    case Some(value) => value
    case None => throw new NoSuchElementException(this + " is empty")
  }

  @throws[NoSuchElementException]
  def valueAs[B <: AnyRef](clz: Class[B]): B = this
    .optValueAs[B](clz) match {
    case Some(value) => value
    case None => throw new NoSuchElementException(this + " does not contain a value of type " + clz.getName)
  }

  @throws[NoSuchElementException]
  def valueAs[B <: AnyRef : ClassTag]: B = this
    .valueAs(implicitly[ClassTag[B]].runtimeClass.asInstanceOf[Class[B]])

  def setValue(optValue: Option[_ <: AnyRef]): Self

  def setValue(value: AnyRef): Self = this
    .setValue(Option(value))
}
