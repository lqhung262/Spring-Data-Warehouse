package com.example.demo.kafka.enums;

public enum JobStatus {
    QUEUED,       // Đã gửi lên Kafka, chờ xử lý
    PROCESSING,   // Đang xử lý
    COMPLETED,    // Hoàn thành (có thể có một số lỗi)
    FAILED        // Thất bại hoàn toàn
}