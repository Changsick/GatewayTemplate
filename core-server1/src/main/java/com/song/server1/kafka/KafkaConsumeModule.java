package com.song.server1.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumeModule {

//    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

//    private final UserService userService;

    public KafkaConsumeModule(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
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

        try {
            ack.acknowledge(); // 메시지 커밋
        } catch (Exception e) {
            throw new RuntimeException(e); // Kafka 트랜잭션도 abort
        }
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

        try {
            // 위의 컨슈머와 동일 내용
            ack.acknowledge();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
            // dlq도 실패하면 우째?
        }
    }

}
