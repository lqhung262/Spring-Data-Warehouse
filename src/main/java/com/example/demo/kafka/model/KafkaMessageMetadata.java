package com.example.demo.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessageMetadata {
    private String messageId;
    private String dataDomainName;
    private String messageSpec;
    private String messageSpecVersion;
    private Long messageTimestamp;
    private Integer retryCount;
}