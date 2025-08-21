package org.dawvvlad.analyticsservice.data;

import java.time.Instant;
import java.util.Map;

/**
 * Возвращается вместе с данными статистики
 * @param totalRecords количество полученных строк
 * @param generatedAt дата генерации
 * @param queryId id запроса (UUID)
 * @param stats (итоговый SQL-запрос, параметры)
 */
public record Metadata(
        Long totalRecords,
        Instant generatedAt,
        String queryId,
        Map<String, String> stats
) {}
