package com.song.server1.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.core.KafkaTemplate;

@TestConfiguration
public class TestKafkaProducer {

    private final static Logger logger = LoggerFactory.getLogger(TestKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message).whenComplete((r, e) -> {
            if (e != null) {
                logger.error(e.getMessage());
            }else{
                logger.info("Message sent successfully {}", r.getProducerRecord().value());
            }
        });
    }

}
