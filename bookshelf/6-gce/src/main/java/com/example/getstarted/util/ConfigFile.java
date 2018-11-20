package com.example.getstarted.util;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Path;
import java.nio.file.Paths;

// Singleton class that reads from config.json
// (Unlike pom.xml, this file can be encrypted using Cloud KMS)
public abstract class ConfigFile {
  private static Map<String, String> config = new HashMap<>();
  private static Gson gson = null;

  public static Map<String, String> getConfig() throws IOException {
    if (gson == null) {
      gson = new Gson();
      Path path = Paths.get("config.json");
      BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset());
      config = gson.fromJson(reader, config.getClass());
    }

    return config;
  }
}
