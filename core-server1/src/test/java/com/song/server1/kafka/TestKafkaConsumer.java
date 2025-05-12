package com.song.server1.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.CountDownLatch;

@TestConfiguration
public class TestKafkaConsumer {

    private final CountDownLatch latch = new CountDownLatch(1);
    private String receivedMessage;

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(ConsumerRecord<String, String> record) {
        this.receivedMessage = record.value();
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }
}
