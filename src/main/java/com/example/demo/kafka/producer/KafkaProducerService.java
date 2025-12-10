package com.example.demo.kafka.producer;

import static org.springframework.kafka.support.KafkaHeaders.*;

import com.example.demo.kafka.constants.KafkaHeaders;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.model.KafkaMessage;
import com.example.demo.kafka.model.KafkaMessageMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.original}")
    private String originalTopic;

    @Value("${kafka.topic.dlq}")
    private String dlqTopic;

    @Value("${kafka.payload.maximum.records}")
    private int maxRecordsPerMessage;

    /**
     * Send messages to original topic with batch partitioning
     */
    public <T> void sendToOriginalTopic(String jobId, List<T> records, MessageSpec messageSpec, String dataDomain) {
        sendMessages(jobId, records, messageSpec, originalTopic, dataDomain);
    }

    /**
     * Send message to DLQ topic (for retry mechanism)
     */
    public <T> void sendToDlqTopic(KafkaMessage<T> kafkaMessage, String sourceTopic) {
        String messageId = kafkaMessage.getMetadata().getMessageId();

        Message<KafkaMessage<T>> message = MessageBuilder
                .withPayload(kafkaMessage)
                // Use Spring TOPIC header for topic routing
                .setHeader(TOPIC, dlqTopic)
                .setHeader(KafkaHeaders.SOURCE_TOPIC, sourceTopic)
                .setHeader(KafkaHeaders.DATA_DOMAIN_NAME,
                        DataDomain.HUMAN_RESOURCE.getValue())
                .setHeader(KafkaHeaders.MESSAGE_SPEC,
                        kafkaMessage.getMetadata().getMessageSpec())
                .setHeader(KafkaHeaders.MESSAGE_SPEC_VERSION,
                        kafkaMessage.getMetadata().getMessageSpecVersion())
                .setHeader(KafkaHeaders.MESSAGE_ID,
                        messageId)
                .setHeader(KafkaHeaders.MESSAGE_TIMESTAMP,
                        String.valueOf(System.currentTimeMillis()))
                .setHeader(KafkaHeaders.RETRY_COUNT,
                        String.valueOf(kafkaMessage.getMetadata().getRetryCount()))
                .build();

        kafkaTemplate.send(message);
        log.info("Sent message to DLQ topic: {} with messageId: {}", dlqTopic, messageId);
    }

    /**
     * Private helper to partition and send messages
     */
    private <T> void sendMessages(String jobId, List<T> records, MessageSpec messageSpec,
                                  String topic, String dataDomain) {
        List<List<T>> batches = partitionRecords(records);

        log.info("Partitioning {} records into {} message(s) for topic: {}, jobId: {}",
                records.size(), batches.size(), topic, jobId);

        for (List<T> batch : batches) {
            String messageId = UUID.randomUUID().toString();
            long timestamp = System.currentTimeMillis();

            KafkaMessageMetadata metadata = KafkaMessageMetadata.builder()
                    .messageId(messageId)
                    .dataDomainName(dataDomain)
                    .messageSpec(messageSpec.getValue())
                    .messageSpecVersion("1.0")
                    .messageTimestamp(timestamp)
                    .retryCount(0)
                    .build();

            KafkaMessage<T> kafkaMessage = KafkaMessage.<T>builder()
                    .jobId(jobId)  // ← SET jobId
                    .payload(batch)
                    .metadata(metadata)
                    .build();

            Message<KafkaMessage<T>> message = MessageBuilder
                    .withPayload(kafkaMessage)
                    // Use Spring TOPIC header for topic routing
                    .setHeader(TOPIC, topic)
                    .setHeader(KafkaHeaders.SOURCE_TOPIC,
                            topic)
                    .setHeader(KafkaHeaders.DATA_DOMAIN_NAME,
                            dataDomain)
                    .setHeader(KafkaHeaders.MESSAGE_SPEC,
                            messageSpec.getValue())
                    .setHeader(KafkaHeaders.MESSAGE_SPEC_VERSION,
                            "1.0")
                    .setHeader(KafkaHeaders.MESSAGE_ID,
                            messageId)
                    .setHeader(KafkaHeaders.MESSAGE_TIMESTAMP,
                            String.valueOf(timestamp))
                    .build();

            kafkaTemplate.send(message);
            log.info("Sent message to topic: {} with messageId: {}, jobId: {}, records: {}",
                    topic, messageId, jobId, batch.size());
        }
    }

    /**
     * Partition records based on maxRecordsPerMessage configuration
     */
    private <T> List<List<T>> partitionRecords(List<T> records) {
        List<List<T>> batches = new ArrayList<>();

        for (int i = 0; i < records.size(); i += maxRecordsPerMessage) {
            int end = Math.min(i + maxRecordsPerMessage, records.size());
            batches.add(new ArrayList<>(records.subList(i, end)));
        }

        return batches;
    }
}