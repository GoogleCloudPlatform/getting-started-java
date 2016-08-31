/**
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package com.example.appengine.helloworld;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;

// [START example]
@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet {

  public static String getInfo() {
    String version = SystemProperty.applicationVersion.get();
    String majorVersion = version.substring(0, version.indexOf('.'));
    Environment env = ApiProxy.getCurrentEnvironment();
    String hostname =
        "" + env.getAttributes().get("com.google.appengine.runtime.default_version_hostname");
    String infostring = "version: " + majorVersion + " and hostname: " + hostname;
    return infostring;
  }


  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter out = resp.getWriter();
    out.println("Hello, world - Flex Compat " + getInfo());
  }
}
// [END example]
