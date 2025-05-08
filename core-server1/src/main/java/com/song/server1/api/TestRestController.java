package com.song.server1.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.song.server1.dto.TestDTO;
import com.song.server1.kafka.KafkaProduceModule;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server1")
public class TestRestController {

    private final KafkaProduceModule kafkaProduceModule;

    public TestRestController(KafkaProduceModule kafkaProduceModule) {
        this.kafkaProduceModule = kafkaProduceModule;
    }

    @GetMapping
    public String test() throws JsonProcessingException {
        kafkaProduceModule.send("test-topic");
        return "server1 - SUCCESS";
    }
}
