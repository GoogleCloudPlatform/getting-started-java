/*
 * Copyright (c) 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.google.appengine.demos.hello.server;

import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;

// [START example]
/**
 * Generate some simple information about the app version and hostname.
 */
public class HelloInfo {

  public static String getInfo() {
    String version = SystemProperty.applicationVersion.get();
    String majorVersion = version.substring(0, version.indexOf('.'));
  	Environment env = ApiProxy.getCurrentEnvironment();
    String hostname = "" +
        env.getAttributes().get("com.google.appengine.runtime.default_version_hostname");
  	String infostring = "version: " + majorVersion + " and hostname: " + hostname;
  	return infostring;
  }
}
// [END example]
