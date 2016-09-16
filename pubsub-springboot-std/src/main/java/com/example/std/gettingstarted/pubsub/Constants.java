package com.example.std.gettingstarted.pubsub;

public final class Constants
{
    public static final int MAX_COUNT = 20;

    /**
     * A memcache key for storing query result for recent messages.
     */
    public static final String MESSAGE_CACHE_KEY = "messageCache";
    public static final String BASE_PACKAGE ="com.google.cloud.pubsub.client.demos.appengine";

    private Constants() {
    }
}
