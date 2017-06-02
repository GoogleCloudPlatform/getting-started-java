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
import javax.servlet.annotation.WebFilter
import javax.servlet.annotation.WebInitParam

import spark.servlet.SparkFilter


// Use Servlet annotation to define the Spark filter without web.xml:
@WebFilter(
        filterName = "SparkInitFilter",
        urlPatterns = arrayOf("/*"),
        initParams = arrayOf(
                WebInitParam(
                        name = "applicationClass",
                        value = "MainApp")

        ))
class SparkInitFilter : SparkFilter() {
}
