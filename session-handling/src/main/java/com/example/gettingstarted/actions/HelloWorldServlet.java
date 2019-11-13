/* Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.gettingstarted.actions;

// [START session_handling_servlet]

import java.io.IOException;
import java.util.Random;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
    name = "helloworld",
    urlPatterns = {"/"})
public class HelloWorldServlet extends HttpServlet {
  private static String[] greetings = {
      "Hello World",
      "Hallo Welt",
      "Ciao Mondo",
      "Salut le Monde",
      "Hola Mundo",
  };

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (!req.getServletPath().equals("/")) {
      return;
    }
    // Get current values for the session.
    // If any attribute doesn't exist, add it to the session.
    Object viewsValue = req.getSession().getAttribute("views");
    if (viewsValue == null) {
      viewsValue = 0;
    }
    int viewsInt = (int) viewsValue;
    viewsInt++;
    req.getSession().setAttribute("views", viewsInt);

    Object greetingValue = req.getSession().getAttribute("greeting");
    if (greetingValue == null) {
      greetingValue = greetings[new Random().nextInt(greetings.length)];
      req.getSession().setAttribute("greeting", greetingValue);
    }
    String greeting = greetingValue.toString();

    System.out.println("Writing response " + req.toString());
    resp.getWriter().write(String.format("%d views for %s", viewsInt, greeting));
  }
}
// [END session_handling_servlet]
