package com.example.concertticketing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@EmbeddedKafka(partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092"},
        ports = { 9092 }
)
public class KafkaTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private Queue<String> consumedMessages;

    private CountDownLatch latch;

    private int count = 100;

    @BeforeEach
    public void setup() {
        latch = new CountDownLatch(count);
        consumedMessages = new ArrayDeque<>();
    }

    @Test
    public void testKafkaProduceAndConsume() throws InterruptedException {
        IntStream.rangeClosed(1, count).forEach(productId ->
                kafkaTemplate.send("test-topic", String.valueOf(productId))
        );

        latch.await(10, TimeUnit.SECONDS);

        assertThat(consumedMessages.size()).isEqualTo(count);
    }

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(String message) {
        consumedMessages.add(message);
        latch.countDown();
    }
}