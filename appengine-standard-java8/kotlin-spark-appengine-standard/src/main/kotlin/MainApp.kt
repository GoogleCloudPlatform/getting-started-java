/**
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import spark.kotlin.Http
import spark.kotlin.ignite
import spark.servlet.SparkApplication

/**
 * Example usage of spark-kotlin.
 * See https://github.com/perwendel/spark-kotlin
 */
class MainApp : SparkApplication {
    override fun init() {
        
        val http: Http = ignite()

        http.get("/") {
            """Hello Spark Kotlin running on Java8 App Engine Standard.
            <p>You can try /hello<p> or /saymy/:name<p> or redirect
            <p>or /nothing"""
        }
        http.get("/hello") {
            "Hello Spark Kotlin running on Java8 App Engine Standard."
        }

        http.get("/nothing") {
            status(404)
            "Oops, we couldn't find what you're looking for."
        }

        http.get("/saymy/:name") {
            params(":name")
        }

        http.get("/redirect") {
            redirect("/hello");
        }
    }
}