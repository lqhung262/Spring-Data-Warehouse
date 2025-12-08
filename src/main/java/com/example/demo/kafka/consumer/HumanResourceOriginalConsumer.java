package com.example.demo.kafka.consumer;

import com.example.demo.kafka.constants.KafkaHeaders;
import com.example. demo.kafka.enums.MessageSpec;
import com.example. demo.kafka.exception.RetryableException;
import com.example.demo.kafka. model.KafkaMessage;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.processor.MessageProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders as SpringKafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org. springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HumanResourceOriginalConsumer {

    private final MessageProcessor messageProcessor;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topic. original}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOriginalMessage(
            @Payload Map<String, Object> messageMap,
            @Header(SpringKafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.MESSAGE_SPEC, required = false) byte[] messageSpecBytes,
            @Header(value = KafkaHeaders.MESSAGE_ID, required = false) byte[] messageIdBytes
    ) {
        String messageSpec = messageSpecBytes != null ?  
                new String(messageSpecBytes, StandardCharsets.UTF_8) : "UNKNOWN";
        String messageId = messageIdBytes != null ? 
                new String(messageIdBytes, StandardCharsets.UTF_8) : "UNKNOWN";

        log.info("Consumed message from original topic: {}, messageId: {}, messageSpec: {}", 
                topic, messageId, messageSpec);

        try {
            // Convert Map to KafkaMessage
            KafkaMessage<? > kafkaMessage = objectMapper. convertValue(messageMap, KafkaMessage.class);
            
            // Process message based on MESSAGE_SPEC
            messageProcessor.processMessage(kafkaMessage, MessageSpec.valueOf(messageSpec));
            
            log.info("Successfully processed message: {}", messageId);
            
        } catch (RetryableException e) {
            log.error("Retryable error processing message: {}. Sending to DLQ.", messageId, e);
            
            // Send to DLQ for retry
            KafkaMessage<? > kafkaMessage = objectMapper. convertValue(messageMap, KafkaMessage.class);
            kafkaProducerService.sendToDlqTopic(kafkaMessage, topic);
            
        } catch (Exception e) {
            log.error("Non-retryable error processing message: {}. Skipping.", messageId, e);
            // Log error but don't retry - could save to error table for manual review
        }
    }
}