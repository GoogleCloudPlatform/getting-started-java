package com.example.std.gettingstarted.exceptions;/**
 * Author: wge
 * Date: 30/10/2016
 * Time: 17:33
 */

public class NoTopicFoundException extends Exception {

    public NoTopicFoundException(String topicName) {
        super(topicName);
    }
}
