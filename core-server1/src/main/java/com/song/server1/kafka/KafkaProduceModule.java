package com.song.server1.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.song.server1.entity.UserEntity;
import com.song.server1.service.UserService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

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
//            CompletableFuture<SendResult<String, String>> futures = kafkaTemplate.send("","");
//
//            // send 이후 무겁거나 스레드에 영향을 주는 작업이면 whenCompleteAsync
//            futures.whenComplete((result, ex) -> {
//                if (ex != null) {
//                    System.err.println("Failed to send message: " + ex.getMessage());
//                } else {
//                    System.out.println("Message sent to: " + result.getRecordMetadata());
//                }
//            });

            try {
                UserEntity user = new UserEntity();
                user.setUsername("test");
                user.setPassword("123456");
                userService.saveUser(user);
                transactionManager.commit(status);

                CompletableFuture<SendResult<String, String>> future = tx.send(topic, objectMapper.writeValueAsString(user));
//                tx.wh
            } catch (Exception e) {
                transactionManager.rollback(status);
                throw new RuntimeException(e);
            }
            return true;
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMessage(String topic, String message) {
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

    public void test(){
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaTemplate.send("topic", "value"); // 커밋 후 전송
            }
        });
    }
}
