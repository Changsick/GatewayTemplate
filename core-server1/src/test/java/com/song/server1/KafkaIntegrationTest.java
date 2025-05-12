package com.song.server1;

import com.song.server1.kafka.TestKafkaConsumer;
import com.song.server1.kafka.KafkaProduceModule;
import com.song.server1.kafka.TestKafkaProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Import({TestKafkaProducer.class, TestKafkaConsumer.class})
@EmbeddedKafka(partitions = 1, topics = { "test-topic" }, brokerProperties = { "listeners=PLAINTEXT://localhost:9095", "port=9095" })
public class KafkaIntegrationTest {

    @Autowired
    private TestKafkaProducer kafkaProduceModule;

    @Autowired
    private TestKafkaConsumer kafkaConsumeModule;

    @Test
    void testKafkaSendReceive() throws Exception {
        String message = "Hello Kafka!";
        kafkaProduceModule.send("test-topic", message);

        // 실제 message가 소비되었는지 확인
        boolean messageConsumed = kafkaConsumeModule.getLatch().await(10, TimeUnit.SECONDS);

        // 비동기 전파 대기
        Thread.sleep(2000);

        assertThat(messageConsumed).isTrue();
        assertThat(kafkaConsumeModule.getReceivedMessage()).isEqualTo(message);
    }
}
