package com.example.std.gettingstarted;

import com.google.api.services.pubsub.model.PubsubMessage;

/**
 * Author: wge
 * Date: 04/11/2016
 * Time: 07:33
 */
public interface CallbackHook {
    void recieveMessage(PubsubMessage message);
}
