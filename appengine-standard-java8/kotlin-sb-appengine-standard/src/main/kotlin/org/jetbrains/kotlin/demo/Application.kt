// See https://github.com/JetBrains/kotlin-examples/blob/master/LICENSE
package org.jetbrains.kotlin.demo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class Application : SpringBootServletInitializer() {

}

fun main(args: Array<String>) {
	SpringApplication.run(Application::class.java, *args)
}
