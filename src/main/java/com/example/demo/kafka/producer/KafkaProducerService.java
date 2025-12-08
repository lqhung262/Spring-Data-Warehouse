package com.example. demo.kafka.producer;

import com.example.demo.kafka. constants.KafkaHeaders;
import com.example.demo.kafka. enums.DataDomain;
import com.example.demo.kafka. enums.MessageSpec;
import com.example.demo.kafka.model.KafkaMessage;
import com.example.demo.kafka. model.KafkaMessageMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders as SpringKafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype. Service;

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

    @Value("${kafka.payload.maximum. records:10}")
    private int maxRecordsPerMessage;

    /**
     * Send messages to original topic with batch partitioning
     */
    public <T> void sendToOriginalTopic(List<T> records, MessageSpec messageSpec) {
        sendMessages(records, messageSpec, originalTopic, null);
    }

    /**
     * Send message to DLQ topic (for retry mechanism)
     */
    public <T> void sendToDlqTopic(KafkaMessage<T> kafkaMessage, String sourceTopic) {
        String messageId = kafkaMessage.getMetadata().getMessageId();
        
        Message<KafkaMessage<T>> message = MessageBuilder
                .withPayload(kafkaMessage)
                .setHeader(SpringKafkaHeaders.TOPIC, dlqTopic)
                .setHeader(KafkaHeaders. TOPIC, dlqTopic. getBytes(StandardCharsets.UTF_8))
                .setHeader(KafkaHeaders.SOURCE_TOPIC, sourceTopic. getBytes(StandardCharsets.UTF_8))
                .setHeader(KafkaHeaders.DATA_DOMAIN_NAME, 
                        DataDomain.HUMAN_RESOURCE.getValue(). getBytes(StandardCharsets.UTF_8))
                .setHeader(KafkaHeaders.MESSAGE_SPEC, 
                        kafkaMessage.getMetadata().getMessageSpec().getBytes(StandardCharsets.UTF_8))
                .setHeader(KafkaHeaders.MESSAGE_SPEC_VERSION, 
                        kafkaMessage.getMetadata().getMessageSpecVersion().getBytes(StandardCharsets.UTF_8))
                .setHeader(KafkaHeaders.MESSAGE_ID, 
                        messageId.getBytes(StandardCharsets.UTF_8))
                . setHeader(KafkaHeaders. MESSAGE_TIMESTAMP, 
                        String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8))
                .setHeader(KafkaHeaders.RETRY_COUNT,
                        String.valueOf(kafkaMessage.getMetadata().getRetryCount()).getBytes(StandardCharsets.UTF_8))
                .build();

        kafkaTemplate.send(message);
        log.info("Sent message to DLQ topic: {} with messageId: {}", dlqTopic, messageId);
    }

    /**
     * Private helper to partition and send messages
     */
    private <T> void sendMessages(List<T> records, MessageSpec messageSpec, 
                                   String topic, String sourceTopic) {
        // Partition records into batches
        List<List<T>> batches = partitionRecords(records);
        
        log.info("Partitioning {} records into {} message(s) for topic: {}", 
                records.size(), batches.size(), topic);

        for (List<T> batch : batches) {
            String messageId = UUID.randomUUID().toString();
            long timestamp = System.currentTimeMillis();

            KafkaMessageMetadata metadata = KafkaMessageMetadata.builder()
                    .messageId(messageId)
                    .dataDomainName(DataDomain.HUMAN_RESOURCE.getValue())
                    .messageSpec(messageSpec.getValue())
                    .messageSpecVersion("1.0")
                    . messageTimestamp(timestamp)
                    .retryCount(0)
                    .build();

            KafkaMessage<T> kafkaMessage = KafkaMessage. <T>builder()
                    . payload(batch)
                    . metadata(metadata)
                    . build();

            Message<KafkaMessage<T>> message = MessageBuilder
                    . withPayload(kafkaMessage)
                    .setHeader(SpringKafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.TOPIC, topic.getBytes(StandardCharsets.UTF_8))
                    .setHeader(KafkaHeaders.SOURCE_TOPIC, 
                            (sourceTopic != null ? sourceTopic : topic).getBytes(StandardCharsets. UTF_8))
                    . setHeader(KafkaHeaders. DATA_DOMAIN_NAME, 
                            DataDomain. HUMAN_RESOURCE.getValue().getBytes(StandardCharsets.UTF_8))
                    .setHeader(KafkaHeaders.MESSAGE_SPEC, 
                            messageSpec.getValue().getBytes(StandardCharsets.UTF_8))
                    .setHeader(KafkaHeaders.MESSAGE_SPEC_VERSION, 
                            "1.0". getBytes(StandardCharsets.UTF_8))
                    .setHeader(KafkaHeaders.MESSAGE_ID, 
                            messageId.getBytes(StandardCharsets.UTF_8))
                    .setHeader(KafkaHeaders.MESSAGE_TIMESTAMP, 
                            String.valueOf(timestamp). getBytes(StandardCharsets.UTF_8))
                    .build();

            kafkaTemplate.send(message);
            log.info("Sent message to topic: {} with messageId: {}, records: {}", 
                    topic, messageId, batch.size());
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