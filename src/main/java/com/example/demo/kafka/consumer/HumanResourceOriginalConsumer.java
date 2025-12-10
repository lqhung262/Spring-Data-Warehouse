package com.example.demo.kafka.consumer;

import static org.springframework.kafka.support.KafkaHeaders.*;

import com.example.demo.kafka.constants.KafkaHeaders;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.exception.RetryableException;
import com.example.demo.kafka.model.KafkaMessage;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.processor.MessageProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HumanResourceOriginalConsumer {

    private final MessageProcessor messageProcessor;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topic.original}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "${kafka.consumer.original.containerFactory}"
    )
    public void consumeOriginalMessage(
            @Payload Map<String, Object> messageMap,
            @Header(RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.MESSAGE_SPEC, required = false) String messageSpec,
            @Header(value = KafkaHeaders.MESSAGE_ID, required = false) String messageId
    ) {
        String resolvedMessageSpec = messageSpec != null ? messageSpec : "UNKNOWN";
        String resolvedMessageId = messageId != null ? messageId : "UNKNOWN";

        log.info("Consumed message from original topic: {}, messageId: {}, messageSpec: {}",
                topic, resolvedMessageId, resolvedMessageSpec);

        try {
            KafkaMessage<?> kafkaMessage = objectMapper.convertValue(messageMap, KafkaMessage.class);
            messageProcessor.processMessage(kafkaMessage, MessageSpec.valueOf(resolvedMessageSpec));
            log.info("Successfully processed message: {}", resolvedMessageId);

        } catch (RetryableException e) {
            log.error("Retryable error processing message: {}. Sending to DLQ.", resolvedMessageId, e);
            KafkaMessage<?> kafkaMessage = objectMapper.convertValue(messageMap, KafkaMessage.class);
            kafkaProducerService.sendToDlqTopic(kafkaMessage, topic);

        } catch (Exception e) {
            log.error("Non-retryable error processing message: {}. Skipping.", resolvedMessageId, e);
        }
    }
}