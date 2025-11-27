package com.example.demo.util;

import java.util.*;
import java.util.function.Function;

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
}

