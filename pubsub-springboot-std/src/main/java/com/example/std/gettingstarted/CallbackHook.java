package com.example.std.gettingstarted;

import com.google.api.services.pubsub.model.PubsubMessage;


public interface CallbackHook {
    void receiveMessage(PubsubMessage message);
}
