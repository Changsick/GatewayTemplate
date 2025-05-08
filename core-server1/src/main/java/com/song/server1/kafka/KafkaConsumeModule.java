package com.song.server1.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.song.server1.entity.UserEntity;
import com.song.server1.service.UserService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class KafkaConsumeModule {

    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final UserService userService;

    private final PlatformTransactionManager transactionManager;

    private final DefaultTransactionDefinition defaultTransactionDefinition;

    public KafkaConsumeModule(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate, UserService userService, PlatformTransactionManager transactionManager, DefaultTransactionDefinition defaultTransactionDefinition) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.userService = userService;
        this.transactionManager = transactionManager;
        this.defaultTransactionDefinition = defaultTransactionDefinition;
    }

    @KafkaListener(
            topics = "#{'${app.kafka.topics.basic}'}",
            groupId = "#{'${app.kafka.groupId}'}",
            containerFactory = "#{'${app.kafka.containerFactory}'}"
    )
    public void listen(ConsumerRecord<String, String> record, // 처리 메시지 메타정보
                       Consumer<?, ?> consumer, // 컨슈머 메타정보
                       String message, // 편의상 message 내용만 뽑아 볼 수 있다.
                       Acknowledgment ack) throws Exception {
        System.out.println("record : " + record);
        System.out.println("message : " + message);
        System.out.println("ack : " + ack);

        kafkaTemplate.executeInTransaction(tx -> {

            // JPA 트랜잭션 명시적 시작
            TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);

            try {
                UserEntity dto = objectMapper.readValue(record.value(), UserEntity.class);
                System.out.println("dto : " + dto);

                // 컨슈머 내에선 db 트랜잭션이 안잡힌다(명시적으로 빼도)
                // 찾아보니 dlq 재시도 관련 중복될 수 있기때문에 upsert 쓴다하긴함.
                UserEntity user = new UserEntity();
                user.setUsername("test--");
                user.setPassword("123456");
                userService.saveUser(user);
                transactionManager.commit(status);
                // db작업 이후 topic to topic을 해야해서 컨슘 > db > 프로듀싱이 필요하다면
                // db 작업 이후 프로듀싱하고(tx.send("topic", "message")) 이후 컨슘 메시지 커밋 필요(offset 커밋)
                if(true) throw new Exception("aaaaaaaaaa");

//                tx.send("another topic", "msg");
//                Map<TopicPartition, OffsetAndMetadata> offsets = Map.of(
//                        new TopicPartition("input-topic", ((ConsumerRecord<?, ?>)consumer).partition()),
//                        new OffsetAndMetadata(((ConsumerRecord<?, ?>)consumer).offset() + 1)
//                );
//                tx.sendOffsetsToTransaction(offsets, consumer.groupMetadata());

                ack.acknowledge(); // 메시지 커밋
            } catch (Exception e) {
                transactionManager.rollback(status);
                throw new RuntimeException(e); // Kafka 트랜잭션도 abort
            }
            return true;
            // true는 단순히 executeInTransaction()의 리턴값으로서
            // 개발자가 그 안의 작업 결과를 받아보는 용도
        });
    }

    @KafkaListener(
            topics = "#{'${app.kafka.topics.dlq-basic}'}",
            groupId = "#{'${app.kafka.dlq-groupId}'}",
            containerFactory = "#{'${app.kafka.dlq-containerFactory}'}"
    )
    public void handleDlqMessages(ConsumerRecord<String, String> record, String message, Acknowledgment ack) {
        // 실패 메시지 로깅 또는 별도 저장 처리
        System.out.println("##dlq record : " + record);
        System.out.println("##dlq message : " + message);
        System.out.println("##dlq ack : " + ack);

        // kafka_dlt-exception-fqcn : 예외클래스 이름(거의 ListenerExecutionFailedException)
        Header exceptionFqcnHeader = record.headers().lastHeader("kafka_dlt-exception-fqcn");
        if (exceptionFqcnHeader != null) {
            System.out.println("Exception FQCN: " + new String(exceptionFqcnHeader.value()));
        }

        // kafka_dlt-exception-cause-fqcn : 실제 실패한 컨슈머에서 발생한 Exception 클래스이름
        Header causeFqcnHeader = record.headers().lastHeader("kafka_dlt-exception-cause-fqcn");
        if (causeFqcnHeader != null) {
            System.out.println("Cause FQCN: " + new String(causeFqcnHeader.value()));
        }

        // kafka_dlt-exception-message : 에러메시지, 좀 길게나오는데 마지막에 정보 나온다. 커스텀으로 쓴 메시지도 여기 마지막에 나옴.
        Header exceptionMessageHeader = record.headers().lastHeader("kafka_dlt-exception-message");
        if (exceptionMessageHeader != null) {
            System.out.println("Exception Message: " + new String(exceptionMessageHeader.value()));
        }

        // kafka_dlt-exception-stacktrace : printStackTrace
        Header stacktraceHeader = record.headers().lastHeader("kafka_dlt-exception-stacktrace");
        if (stacktraceHeader != null) {
            System.out.println("Exception Stacktrace: " + new String(stacktraceHeader.value()));
        }

        kafkaTemplate.executeInTransaction(tx -> {
            try {
                // 위의 컨슈머와 동일 내용
                ack.acknowledge();
            } catch (Exception ex) {

                // dlq도 실패하면 우째?
            }
            return true;
        });
    }

    @Transactional
    public void consumeMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            // 1. DB 작업 처리
//            someDatabaseService.processMessage(record.value());

            // 2. 트랜잭셔널 프로듀서로 메시지 전송
            kafkaTemplate.executeInTransaction(kafkaProducer -> {
                String messageId = record.value();  // 메시지의 고유 ID 생성
                kafkaProducer.send("some-topic", messageId, record.value())
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                // 전송 실패 시 예외 처리 (DB 트랜잭션과 롤백)
                                throw new RuntimeException("Kafka message send failed", ex);
                            }
                        });
                return null;
            });

            // 3. 메시지 전송 성공 후 DB 작업 완료 및 메시지 커밋
            acknowledgment.acknowledge();

        } catch (Exception e) {
            // 예외 처리: DB 롤백 및 메시지 전송 롤백
            e.printStackTrace();
            throw e;  // DB 트랜잭션 롤백, Kafka 메시지 전송도 롤백됨
        }
    }
}
