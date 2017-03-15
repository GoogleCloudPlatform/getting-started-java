package com.example.std.gettingstarted.config;

import com.google.api.client.util.Base64;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.travellazy.google.pubsub.service.CallbackHook;
import org.slf4j.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ReceiveMessageCallback implements CallbackHook {
    private static final Logger log = LoggerFactory.getLogger(ReceiveMessageCallback.class);

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage) {
        log.info("rawMessage = " + pubsubMessage.toString());

        Map<String, Object> map = (Map) pubsubMessage.get("message");

        String encodedData = (String) map.get("data");
        log.info("dencodedData = " + encodedData);
        byte[] rawData = Base64.decodeBase64(encodedData);
        String decodedData = null;

        try {
            decodedData = new String(rawData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        log.info("byte[] decoded from pubsubMessage = " + pubsubMessage.decodeData());
        log.info("received message as " + decodedData);
        
    }
}
