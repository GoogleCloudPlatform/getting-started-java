package com.example.std.gettingstarted.requests;

/**
 * Author: wge
 * Date: 28/10/2016
 * Time: 22:34
 */

public class SubscriberRequest {
    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    private String subscriber;
    private String topic;
    private String callback;


    @Override
    public String toString() {
        return "SubscriberRequest{" +
                "subscriber='" + subscriber + '\'' +
                ", topic='" + topic + '\'' +
                ", callback='" + callback + '\'' +
                '}';
    }
}
