package com.programmingcodez.inventoryservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @KafkaListener(topics = "my-topic", groupId = "my-supermarket-system-123456789")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }

}
