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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HumanResourceDlqConsumer {

    private final MessageProcessor messageProcessor;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.dlq.max.retries}")
    private int maxRetries;

    @KafkaListener(
            topics = "${kafka.topic.dlq}",
            groupId = "${spring.kafka.consumer.group-id}-dlq",
            containerFactory = "${kafka.consumer.dlq.containerFactory}"
    )
    public void consumeDlqMessage(
            @Payload Map<String, Object> messageMap,
            @Header(RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.MESSAGE_SPEC, required = false) String messageSpec,
            @Header(value = KafkaHeaders.MESSAGE_ID, required = false) String messageId,
            @Header(value = KafkaHeaders.SOURCE_TOPIC, required = false) String sourceTopic,
            @Header(value = KafkaHeaders.RETRY_COUNT, required = false) String retryCountStr
    ) {
        String resolvedMessageSpec = messageSpec != null ? messageSpec : "UNKNOWN";
        String resolvedMessageId = messageId != null ? messageId : "UNKNOWN";
        String resolvedSourceTopic = sourceTopic != null ? sourceTopic : "UNKNOWN";

        int retryCount = 0;
        if (retryCountStr != null) {
            try {
                retryCount = Integer.parseInt(retryCountStr);
            } catch (NumberFormatException ignored) {
            }
        }

        log.info("Consumed message from DLQ: {}, messageId: {}, messageSpec: {}, retryCount: {}",
                topic, resolvedMessageId, resolvedMessageSpec, retryCount);

        try {
            KafkaMessage<?> kafkaMessage = objectMapper.convertValue(messageMap, KafkaMessage.class);
            messageProcessor.processMessage(kafkaMessage, MessageSpec.valueOf(resolvedMessageSpec));
            log.info("Successfully processed DLQ message: {} after {} retries", resolvedMessageId, retryCount);

        } catch (RetryableException e) {
            retryCount++;

            if (retryCount < maxRetries) {
                log.warn("Retry {}/{} failed for message: {}. Re-sending to DLQ.",
                        retryCount, maxRetries, resolvedMessageId, e);

                KafkaMessage<?> kafkaMessage = objectMapper.convertValue(messageMap, KafkaMessage.class);
                kafkaMessage.getMetadata().setRetryCount(retryCount);
                kafkaProducerService.sendToDlqTopic(kafkaMessage, resolvedSourceTopic);

            } else {
                log.error("Max retries ({}) exceeded for message: {}. Manual review needed.",
                        maxRetries, resolvedMessageId, e);
            }

        } catch (Exception e) {
            log.error("Non-retryable error processing DLQ message: {}. Skipping.", resolvedMessageId, e);
        }
    }
}