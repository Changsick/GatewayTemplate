package com.song.server1.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.song.server1.entity.UserEntity;
import com.song.server1.service.UserService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class KafkaProduceModule {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private final UserService userService;

    private final PlatformTransactionManager transactionManager;

    private final DefaultTransactionDefinition defaultTransactionDefinition;

    public KafkaProduceModule(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, UserService userService, PlatformTransactionManager transactionManager, DefaultTransactionDefinition defaultTransactionDefinition) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.transactionManager = transactionManager;
        this.defaultTransactionDefinition = defaultTransactionDefinition;
    }

    public void send(String topic) {

        kafkaTemplate.executeInTransaction(tx -> {

            // JPA 트랜잭션 명시적 시작
            TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);

            try {
                UserEntity user = new UserEntity();
                user.setUsername("test");
                user.setPassword("123456");
                userService.saveUser(user);
                transactionManager.commit(status);

                tx.send(topic, objectMapper.writeValueAsString(user));
            } catch (Exception e) {
                transactionManager.rollback(status);
                throw new RuntimeException(e);
            }
            return true;
        });
    }
}
