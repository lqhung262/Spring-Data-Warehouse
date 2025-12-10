package com.example.demo.kafka.processor;

import com.example.demo.dto.BulkOperationResult;

import java.util.List;
import java.util.function.Function;

public record BulkOperationHandlerConfig<T>
        (Class<T> requestClass,
         Function<List<T>, BulkOperationResult<?>> upsertFunction,
         Function<List<Long>, BulkOperationResult<?>> deleteFunction) {
}

