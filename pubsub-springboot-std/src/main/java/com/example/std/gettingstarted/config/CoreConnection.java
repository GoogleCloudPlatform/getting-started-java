package com.example.std.gettingstarted.config;

import org.apache.log4j.Logger;


public class CoreConnection
{
    private static final Logger log = Logger.getLogger(CoreConnection.class);

    public static String appUrl = "127.0.0.1";
    public static String maven_version = "externally set to pom";
    public static String port =":8080";
    private CoreConnection() {}

    public static String buildUrl(String relativeUrl)
    {
        String url;
        if(appUrl.startsWith("http"))
             url = appUrl + port + relativeUrl;
        else
             url = "http://" +appUrl + port + relativeUrl;

        log.debug("url = " + url);

        return url;
    }
}
