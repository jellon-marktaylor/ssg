package jellon.ssg.io

object IResetable {
  def reset(any: Any): Unit = any match {
    case resetable: IResetable =>
      resetable.reset()
  }
}

trait IResetable {
  def reset(): Unit
}
