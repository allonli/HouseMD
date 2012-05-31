/*
 * Copyright 2012 zhongl
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.zhongl.house

import instrument.Instrumentation
import java.net.Socket
import jline.console.ConsoleReader
import com.github.zhongl.command.{Shell, PrintOut, Command}

/**
 * @author <a href="mailto:zhong.lunfu@gmail.com">zhongl<a>
 */
class Duck(inst: Instrumentation, port: Int, classes:Array[Class[Command]]) extends Runnable {

  def run() {
    new Cellphone() dail(port, classes)
  }

  class Cellphone {
    def dail(port: Int, classes: Array[Class[Command]]) {
      val socket = new Socket("localhost", port)
      val reader = new ConsoleReader(socket.getInputStream, socket.getOutputStream)

      new Shell(
        name = "house",
        description = "a runtime diagnosis tool of jvm",
        reader = reader
      ) {
        override protected lazy val commands = classes map {toCommand(_, PrintOut(reader))}
      } main (Array.empty[String])

      socket.shutdownOutput()
      socket.shutdownInput()
      socket.close()
    }

    private def toCommand(c: Class[Command], out: PrintOut) = {
      val I = classOf[Instrumentation]
      val O = classOf[PrintOut]
      val constructors = c.getConstructors
      val args = constructors(0).getParameterTypes map {
        case I => inst
        case O => out
        case x => throw new IllegalStateException("Unsupport parameter type: " + x)
      }
      constructors(0).newInstance(args).asInstanceOf[Command]
    }

  }

}

