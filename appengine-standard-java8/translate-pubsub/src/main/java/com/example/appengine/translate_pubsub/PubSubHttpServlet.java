package com.example.appengine.translate_pubsub;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

public class PubSubHttpServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final JsonParser jsonParser = new JsonParser();

    protected Message getMessage(HttpServletRequest request) throws IOException {
        String requestBody = request.getReader().lines().collect(Collectors.joining("\n"));
        JsonElement jsonRoot = jsonParser.parse(requestBody);
        String messageStr = jsonRoot.getAsJsonObject().get("message").toString();
        return gson.fromJson(messageStr, Message.class);
    }
}
