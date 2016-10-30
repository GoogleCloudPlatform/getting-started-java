package com.example.std.gettingstarted.responses;/**
 * Author: wge
 * Date: 30/10/2016
 * Time: 17:52
 */

import com.travellazy.google.pubsub.util.TopicValue;

public class TopicDisplay  {

    public String status;
    public String topicName;

    public TopicDisplay(){}

    public TopicDisplay(final TopicValue topicValue) {
       this.status = topicValue.getStatus();
        this.topicName = topicValue.getFullTopicName();
    }
}
