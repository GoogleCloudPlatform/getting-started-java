package com.example.appengine.pubsub;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;

@WebServlet(name = "PubSubPush", value = "/_ah/push-handlers/receive_messages")
public class PubSubPush extends HttpServlet {
  List<String> tokens = new ArrayList<>();
  List<String> claims = new ArrayList<>();
  List<GoogleIdToken.Payload> messages = new ArrayList<>();

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    if (null == req.getParameter("token")
        || null == req.getHeader("Authorization")
        || !System.getenv("PUBSUB_VERIFICATION_TOKEN").equals(req.getParameter("token"))) {
      resp.sendError(HttpStatus.SC_BAD_REQUEST, "Invalid request");
    }

    String headers[] = req.getHeader("Authorization").split(" ");
    String bearerToken = headers[1];
    tokens.add(bearerToken);

    JacksonFactory jacksonFactory = new JacksonFactory();
    GoogleIdTokenVerifier verifier =
        new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jacksonFactory).build();
    GoogleIdToken token = GoogleIdToken.parse(jacksonFactory, bearerToken);
    try {
      verifier.verify(token);
    } catch (GeneralSecurityException e) {
      resp.sendError(HttpStatus.SC_BAD_REQUEST, "Invalid token");
    }
    GoogleIdToken.Payload payload = token.getPayload();
    messages.add(payload);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    try {
      req.setAttribute("messages", messages);
      RequestDispatcher requestDispatcher = req.getRequestDispatcher("index.jsp");
      requestDispatcher.forward(req, resp);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
