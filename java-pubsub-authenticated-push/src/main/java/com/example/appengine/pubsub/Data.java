package com.example.appengine.pubsub;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.List;

public class Data {
  List<String> tokens = new ArrayList<>();
  List<Claims> claims = new ArrayList<>();
  List<GoogleIdToken.Payload> messages = new ArrayList<>();

  public List<String> getTokens() {
    return tokens;
  }

  public List<Claims> getClaims() {
    return claims;
  }

  public List<GoogleIdToken.Payload> getMessages() {
    return messages;
  }
}
