package com.example.std.gettingstarted.exceptions;

public class NoTopicFoundException extends Exception {

    public NoTopicFoundException(String topicName) {
        super(topicName);
    }
}
