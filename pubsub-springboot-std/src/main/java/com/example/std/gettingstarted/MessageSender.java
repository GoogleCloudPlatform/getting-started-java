package com.example.std.gettingstarted;

import com.google.api.services.pubsub.model.PubsubMessage;

public interface MessageSender {

    void sendPublishMessage(String endpoint, PubsubMessage message) throws Exception;
}
