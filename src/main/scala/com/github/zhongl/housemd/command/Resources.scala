package com.github.zhongl.housemd.command

import instrument.Instrumentation
import com.github.zhongl.yascli.{Command, PrintOut}
import collection.JavaConversions._

/**
 * @author <a href="mailto:zhong.lunfu@gmail.com">zhongl<a>
 */
class Resources(inst: Instrumentation, out: PrintOut)
  extends Command("resources", "list all source paths can loaded from every classloader by resource full name.", out) {

  private val fullname = parameter[String]("full-name", "resource full name, eg: com/example/xxx.xxx .")

  private object Loader {
    def unapply(c: Class[_]) = Option(c.getClassLoader)
  }

  def run() {
    val name = fullname()
    val loaders = inst.getAllLoadedClasses.collect { case Loader(cl) => cl }.distinct
    val urls = loaders.flatMap(_.getResources(name).toList)
    urls.distinct.foreach {println}
  }
}
