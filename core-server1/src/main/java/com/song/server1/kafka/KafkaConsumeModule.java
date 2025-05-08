package com.song.server1.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.song.server1.dto.TestDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumeModule {

    private final ObjectMapper objectMapper;

    public KafkaConsumeModule(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "#{'${app.kafka.topics.basic}'}",
            groupId = "#{'${app.kafka.groupId}'}",
            containerFactory = "#{'${app.kafka.containerFactory}'}"
    )
    // @Transactional("kafkaTransactionManager") 만약 topic to topic(컨슘 이후 프로듀스) 할 경우
    // producer transaction manager를 명시해야하고, producer send 이후 ack을 수동커밋해야한다.
    public void listen(ConsumerRecord<String, String> record, String message, Acknowledgment ack) throws Exception {
        System.out.println("record : " + record);
        System.out.println("message : " + message); // 편의상 message 내용만 뽑아 볼 수 있다.
        System.out.println("ack : " + ack);
        // record안엔 origin message랑 부가적인 데이터 싹다 들어있다.
        TestDTO dto = objectMapper.readValue(record.value(), TestDTO.class);
        System.out.println("dto : " + dto);
        if(true) throw new Exception("test Exception");
        ack.acknowledge();
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

            ack.acknowledge();
        } catch (Exception ex) {
            ex.printStackTrace();
            // dlq도 실패하면 우째?
        }
    }
}
