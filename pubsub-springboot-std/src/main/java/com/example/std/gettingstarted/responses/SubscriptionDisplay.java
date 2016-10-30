package com.example.std.gettingstarted.responses;

import com.travellazy.google.pubsub.util.SubscriptionValue;

public class SubscriptionDisplay {

    public String topicKey;
    public String callbackUrl;
    public String subscriberKey;

    public SubscriptionDisplay(final SubscriptionValue subscriptionValue){
        subscriberKey = subscriptionValue.getSubscriptionKey();
        topicKey = subscriptionValue.getTopicSubscribedTo().getTopicKey();
        callbackUrl = subscriptionValue.getEndpointCallback();
    }
}
