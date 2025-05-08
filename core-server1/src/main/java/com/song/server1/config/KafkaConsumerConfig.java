package com.song.server1.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private final String kafkaBootstrapServers;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String groupId;

    public KafkaConsumerConfig(@Value("${spring.kafka.bootstrap-servers}") String kafkaBootstrapServers,
                               KafkaTemplate<String, String> kafkaTemplate,
                               @Value("${app.kafka.groupId}") String groupId) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.kafkaTemplate = kafkaTemplate;
        this.groupId = groupId;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // 수동 커밋 설정
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // 트랜잭션 미완료 메시지를 다른 컨슈머가 읽지 않도록 설정
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    // 기본적인 컨슘 listener는 해당 컨테이너 사용하도록 : dlq 설정 등
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // DLQ 핸들러 설정
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLQ", record.partition())
        );
        // DLQ 토픽으로 라우팅, 오류났던토픽 + .DLQ 이름의 토픽으로 쏜다. 동적으로 설정한건데 한번에 처리할거면 정적으로 여기서 직접 이름설정해도됨

        factory.setCommonErrorHandler(new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2))); // 2회 재시도 후 DLQ 전송

        return factory;
    }

    // dlq 컨슘 listener용 컨테이너, 위와 분리한 이유는 dlq에 위의 컨테이너를 명시하면 dlq에서 오류났을 시 계속 자기 dlq를 호출하는 무한루프에 빠질 수 있음
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> dlqKafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

}
