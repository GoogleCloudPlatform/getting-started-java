package com.example.std.gettingstarted.requests;

/**
 * Author: wge
 * Date: 28/10/2016
 * Time: 22:34
 */

public class SubscriberRequest {
    public String getSubscriberKey() {
        return subscriberKey;
    }

    public void setSubscriberKey(String subscriberKey) {
        this.subscriberKey = subscriberKey;
    }

    public String getTopicKey() {
        return topicKey;
    }

    public void setTopicKey(String topicKey) {
        this.topicKey = topicKey;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    private String subscriberKey;
    private String topicKey;
    private String callback;


    @Override
    public String toString() {
        return "SubscriberRequest{" +
                "subscriber='" + subscriberKey + '\'' +
                ", topic='" + topicKey + '\'' +
                ", callback='" + callback + '\'' +
                '}';
    }
}
