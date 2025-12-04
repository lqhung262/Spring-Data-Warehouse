package com.example.demo.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;

@Slf4j
public final class BulkOperationUtils {

    private BulkOperationUtils() {
        // utility class
    }

    /**
     * Loại bỏ phần tử trùng và phát hiện duplicate trong danh sách.
     *
     * @param values    danh sách giá trị đầu vào
     * @param valueName tên giá trị để đưa vào message exception (vd: "ID", "source_id")
     * @return tập giá trị duy nhất, giữ nguyên thứ tự xuất hiện đầu tiên
     * @throws IllegalArgumentException nếu phát hiện phần tử trùng lặp
     */
    public static <T> Set<T> validateAndExtractUniqueValues(List<T> values, String valueName) {
        Set<T> uniqueValues = new LinkedHashSet<>();
        Set<T> duplicates = new LinkedHashSet<>();

        for (T value : values) {
            if (!uniqueValues.add(value)) {
                duplicates.add(value);
            }
        }

        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("Duplicate " + valueName + " in request: " + duplicates);
        }

        return uniqueValues;
    }

    /**
     * Chuyển kết quả query dạng List<Object[]> (id, count) sang Map<ID, Count>.
     */
    public static Map<Long, Long> toIdCountMap(List<Object[]> queryResults) {
        Map<Long, Long> map = new HashMap<>();
        for (Object[] row : queryResults) {
            if (row == null || row.length < 2) {
                continue;
            }
            Long id = (Long) row[0];
            Long count = (Long) row[1];
            map.put(id, count);
        }
        return map;
    }

    /**
     * Helper generic: build map từ collection sang map theo key extractor.
     */
    public static <K, V> Map<K, V> toMap(Iterable<V> values, Function<V, K> keyExtractor) {
        Map<K, V> map = new HashMap<>();
        for (V value : values) {
            map.put(keyExtractor.apply(value), value);
        }
        return map;
    }

    // ================== BULK UPSERT CLASSIFICATION =======================

    /**
     * Phân loại requests thành Safe Batch và Final Batch dựa trên unique fields.
     * <p>
     * Safe Batch: Requests không có unique value nào tồn tại trong DB hoặc duplicate trong request
     * Final Batch: Requests có ít nhất 1 unique value đã tồn tại trong DB hoặc duplicate trong request
     *
     * @param requests              Danh sách requests
     * @param uniqueFieldExtractors Map<fieldName, extractor function>
     * @param existingValuesMaps    Map<fieldName, Set<existing values from DB>>
     * @return BatchClassification chứa safeBatch và finalBatch
     */
    public static <T> BatchClassification<T> classifyBatchByUniqueFields(
            List<T> requests,
            Map<String, Function<T, String>> uniqueFieldExtractors,
            Map<String, Set<String>> existingValuesMaps) {

        List<T> safeBatch = new ArrayList<>();
        List<T> finalBatch = new ArrayList<>();

        // Initialize tracker for seen values
        UniqueValueTracker tracker = new UniqueValueTracker(uniqueFieldExtractors.keySet());

        for (int i = 0; i < requests.size(); i++) {
            T request = requests.get(i);
            boolean isFinal = shouldGoToFinalBatch(request, uniqueFieldExtractors, existingValuesMaps, tracker);

            if (isFinal) {
                finalBatch.add(request);
                log.debug("Request[{}] → FINAL BATCH", i);
            } else {
                safeBatch.add(request);
                log.debug("Request[{}] → SAFE BATCH", i);
            }
        }

        log.info("Batch classification completed: {} safe, {} final (potential conflicts)",
                safeBatch.size(), finalBatch.size());
        return new BatchClassification<>(safeBatch, finalBatch);
    }

    /**
     * EXTRACTED: Check if request should go to final batch
     */
    private static <T> boolean shouldGoToFinalBatch(
            T request,
            Map<String, Function<T, String>> uniqueFieldExtractors,
            Map<String, Set<String>> existingValuesMaps,
            UniqueValueTracker tracker) {

        // duyệt mỗi field unique
        for (Map.Entry<String, Function<T, String>> entry : uniqueFieldExtractors.entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue().apply(request);

            // Skip null/empty values
            if (isNullOrEmpty(value)) {
                log.trace("Field {} is null/empty, skipping", fieldName);
                continue;
            }

            // Check 1: Duplicate trong request
            boolean isDup = tracker.isDuplicate(fieldName, value);
            if (isDup) {
                log.warn("→ FINAL: Duplicate {} '{}' found in request", fieldName, value);
                return true;
            }

            // Check 2: Exists in DB
            boolean existsInDb = existsInDatabase(fieldName, value, existingValuesMaps);
            log.trace("Field {} = '{}': isDuplicate={}, existsInDB={}", fieldName, value, isDup, existsInDb);

            if (existsInDb) {
                log.warn("→ FINAL: {} '{}' already exists in DB", fieldName, value);
                return true;
            }
        }

        log.debug("→ SAFE: All unique fields are clean");
        return false;
    }

    /**
     * EXTRACTED: Check if value is null or empty
     */
    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * EXTRACTED: Check if value exists in database
     */
    private static boolean existsInDatabase(
            String fieldName,
            String value,
            Map<String, Set<String>> existingValuesMaps) {
        Set<String> existingValues = existingValuesMaps.get(fieldName);
        return existingValues != null && existingValues.contains(value);
    }

    /**
     * Extract tất cả unique values từ list requests cho 1 field cụ thể.
     * Chỉ lấy non-null, non-empty values.
     */
    public static <T> Set<String> extractUniqueValues(
            List<T> requests,
            Function<T, String> extractor) {
        Set<String> values = new LinkedHashSet<>();
        for (T request : requests) {
            String value = extractor.apply(request);
            if (!isNullOrEmpty(value)) {
                values.add(value);
            }
        }
        return values;
    }

    /**
     * INNER CLASS: Track seen values để detect duplicates
     */
    private static class UniqueValueTracker {
        private final Map<String, Set<String>> seenValuesInRequest;

        UniqueValueTracker(Set<String> fieldNames) {
            this.seenValuesInRequest = new HashMap<>();
            fieldNames.forEach(field -> seenValuesInRequest.put(field, new HashSet<>()));
        }

        /**
         * Check và track value.  Return true nếu duplicate.
         */
        boolean isDuplicate(String fieldName, String value) {
            return !seenValuesInRequest.get(fieldName).add(value);
        }
    }

    /**
     * Class chứa kết quả phân loại batch
     */
    @Getter
    public static class BatchClassification<T> {
        private final List<T> safeBatch;
        private final List<T> finalBatch;

        public BatchClassification(List<T> safeBatch, List<T> finalBatch) {
            this.safeBatch = safeBatch;
            this.finalBatch = finalBatch;
        }

        public boolean hasSafeBatch() {
            return !safeBatch.isEmpty();
        }

        public boolean hasFinalBatch() {
            return !finalBatch.isEmpty();
        }
    }
}