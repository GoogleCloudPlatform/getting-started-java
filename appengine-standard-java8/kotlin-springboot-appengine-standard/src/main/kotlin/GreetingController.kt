// See https://github.com/JetBrains/kotlin-examples/blob/master/LICENSE
package org.jetbrains.kotlin.demo

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@RestController
class GreetingController {

    val counter = AtomicLong()

    @RequestMapping("/greeting")
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String): Greeting {
        return Greeting(counter.incrementAndGet(), "Hello, $name, from a SpringBoot Application written in Kotlin, running on Google App Engine Java8 Standard...")
    }
}
