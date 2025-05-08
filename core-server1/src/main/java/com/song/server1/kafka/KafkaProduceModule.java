package com.song.server1.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class KafkaProduceModule {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProduceModule(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional("kafkaTransactionManager")
    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
