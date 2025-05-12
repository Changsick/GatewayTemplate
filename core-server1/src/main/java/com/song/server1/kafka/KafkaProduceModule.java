package com.song.server1.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProduceModule {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProduceModule(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        // 전송 실패 시 처리

                        // 재시도 로직 또는 실패 메시지 기록
                    } else {
                        // 전송 성공 시 처리

                    }
                });
    }

}
