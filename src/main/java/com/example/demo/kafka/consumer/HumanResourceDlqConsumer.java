package com.example.demo.kafka. consumer;

import com.example. demo.kafka.constants.KafkaHeaders;
import com.example.demo.kafka.enums. MessageSpec;
import com.example.demo.kafka.exception.RetryableException;
import com. example.demo.kafka.model. KafkaMessage;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.processor.MessageProcessor;
import com.fasterxml. jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework. beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support. KafkaHeaders as SpringKafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype. Component;

import java.nio. charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HumanResourceDlqConsumer {

    private final MessageProcessor messageProcessor;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @Value("${kafka. dlq.max. retries:3}")
    private int maxRetries;

    @KafkaListener(
            topics = "${kafka.topic.dlq}",
            groupId = "${spring.kafka.consumer.group-id}-dlq",
            containerFactory = "dlqKafkaListenerContainerFactory"
    )
    public void consumeDlqMessage(
            @Payload Map<String, Object> messageMap,
            @Header(SpringKafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.MESSAGE_SPEC, required = false) byte[] messageSpecBytes,
            @Header(value = KafkaHeaders.MESSAGE_ID, required = false) byte[] messageIdBytes,
            @Header(value = KafkaHeaders.SOURCE_TOPIC, required = false) byte[] sourceTopicBytes,
            @Header(value = KafkaHeaders. RETRY_COUNT, required = false) byte[] retryCountBytes
    ) {
        String messageSpec = messageSpecBytes != null ? 
                new String(messageSpecBytes, StandardCharsets.UTF_8) : "UNKNOWN";
        String messageId = messageIdBytes != null ? 
                new String(messageIdBytes, StandardCharsets.UTF_8) : "UNKNOWN";
        String sourceTopic = sourceTopicBytes != null ? 
                new String(sourceTopicBytes, StandardCharsets.UTF_8) : "UNKNOWN";
        
        int retryCount = 0;
        if (retryCountBytes != null) {
            retryCount = Integer.parseInt(new String(retryCountBytes, StandardCharsets.UTF_8));
        }

        log.info("Consumed message from DLQ topic: {}, messageId: {}, messageSpec: {}, retryCount: {}", 
                topic, messageId, messageSpec, retryCount);

        try {
            // Convert Map to KafkaMessage
            KafkaMessage<? > kafkaMessage = objectMapper. convertValue(messageMap, KafkaMessage.class);
            
            // Process message
            messageProcessor.processMessage(kafkaMessage, MessageSpec.valueOf(messageSpec));
            
            log.info("Successfully processed DLQ message: {} after {} retries", messageId, retryCount);
            
        } catch (RetryableException e) {
            retryCount++;
            
            if (retryCount < maxRetries) {
                log. warn("Retry attempt {}/{} failed for message: {}. Re-sending to DLQ.", 
                        retryCount, maxRetries, messageId, e);
                
                // Update retry count and send back to DLQ
                KafkaMessage<?> kafkaMessage = objectMapper.convertValue(messageMap, KafkaMessage.class);
                kafkaMessage.getMetadata().setRetryCount(retryCount);
                kafkaProducerService.sendToDlqTopic(kafkaMessage, sourceTopic);
                
            } else {
                log.error("Max retries ({}) exceeded for message: {}. Moving to dead letter storage.", 
                        maxRetries, messageId, e);
                // TODO: Save to database table for manual review
            }
            
        } catch (Exception e) {
            log.error("Non-retryable error processing DLQ message: {}. Skipping.", messageId, e);
            // TODO: Save to database table for manual review
        }
    }
}