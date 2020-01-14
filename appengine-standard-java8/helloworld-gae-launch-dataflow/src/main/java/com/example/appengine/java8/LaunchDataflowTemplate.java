/*
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

package com.example.appengine.java8;

// [START example]

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(name = "LaunchDataflowTemplate", value = "/launchdf")
public class LaunchDataflowTemplate extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String project = "YOUR_PROJECT_NAME";
    String bucket = "gs://YOUR_BUCKET_NAME";

    ArrayList<String> scopes = new ArrayList<String>();
    scopes.add("https://www.googleapis.com/auth/cloud-platform");
    final AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
    final AppIdentityService.GetAccessTokenResult accessToken = appIdentity.getAccessToken(scopes);

    JSONObject jsonObj = null;
    try {
      JSONObject parameters = new JSONObject()
          .put("datastoreReadGqlQuery", "SELECT * FROM Entries")
          .put("datastoreReadProjectId", project)
          .put("textWritePrefix", bucket + "/output/");
      JSONObject environment = new JSONObject()
          .put("tempLocation", bucket + "/tmp/")
          .put("bypassTempDirValidation", false);
      jsonObj = new JSONObject()
          .put("jobName", "template-" + UUID.randomUUID().toString())
          .put("parameters", parameters)
          .put("environment", environment);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    URL url = new URL(String.format("https://dataflow.googleapis.com/v1b3/projects/%s/templates"
          + ":launch?gcs_path=gs://dataflow-templates/latest/Datastore_to_GCS_Text", project));
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Authorization", "Bearer " + accessToken.getAccessToken());
    conn.setRequestProperty("Content-Type", "application/json");

    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
    jsonObj.write(writer);
    writer.close();

    int respCode = conn.getResponseCode();
    if (respCode == HttpURLConnection.HTTP_OK) {
      response.setContentType("application/json");
      String line;
      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      while ((line = reader.readLine()) != null) {
        response.getWriter().println(line);
      }
      reader.close();

    } else {
      StringWriter w = new StringWriter();
      IOUtils.copy(conn.getErrorStream(), w, "UTF-8");
      response.getWriter().println(w.toString());
    }
  }

}
// [END example]
