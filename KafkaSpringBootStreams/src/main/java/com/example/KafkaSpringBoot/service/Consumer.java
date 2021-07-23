package com.example.KafkaSpringBoot.service;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@EnableKafka
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "fourth_topic-1", groupId = "my-first-app")
    public void consume(GenericRecord message) throws IOException {
        logger.info(String.format("Consumed message -> %s", message.toString()));
    }
}
