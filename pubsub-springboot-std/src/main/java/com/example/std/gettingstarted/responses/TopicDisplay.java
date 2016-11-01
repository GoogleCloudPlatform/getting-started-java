package com.example.std.gettingstarted.responses;

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
