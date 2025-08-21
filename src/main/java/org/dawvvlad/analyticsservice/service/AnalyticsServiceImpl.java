package org.dawvvlad.analyticsservice.service;

import org.dawvvlad.analyticsservice.data.AnalyticsRequest;
import org.dawvvlad.analyticsservice.data.AnalyticsResponse;
import org.dawvvlad.analyticsservice.data.Metadata;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.*;

/**
 * Сервис аналитики
 */
public class AnalyticsServiceImpl implements AnalyticsService {
    private final JdbcTemplate jdbcTemplate;
    private final QueryBuilder queryBuilder;

    public AnalyticsServiceImpl(JdbcTemplate jdbcTemplate,
                                QueryBuilder queryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Получение аналитики по запросу AnalyticsRequest
     * @param request - запрос по форме AnalyticsRequest
     * @see AnalyticsRequest
     * @return данные аналитики с метаданными
     * @see AnalyticsResponse
     */
    public AnalyticsResponse getAnalyticsData(AnalyticsRequest request) throws Exception {
        try {
            // Валидация
            validateRequest(request);

            // Построение запроса
            String sql = queryBuilder.buildQuery(request);
            List<Object> params = queryBuilder.buildParameters(request);

            // Выполнение
            List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, params.toArray());

            // Метаданные
            Long totalCount = getTotalCount(request, sql, params);

            return new AnalyticsResponse(
                    data,
                    new Metadata(
                            totalCount,
                            Instant.now(),
                            UUID.randomUUID().toString(),
                            Map.of("sql", sql, "params", params.toString())
                    )
            );

        } catch (Exception e) {
            throw new Exception("Failed to execute analytics query: " + e.getMessage());
        }
    }

    /**
     * Валидация на корректное наименование таблицы и наличия measures в запросе
     * @param request запрос
     */
    private void validateRequest(AnalyticsRequest request) throws Exception {
        if (request.tableName() == null || request.tableName().isBlank()) {
            throw new Exception("Table name is required");
        }
        if (request.measures() == null || request.measures().isEmpty()) {
            throw new Exception("At least one measure is required");
        }
    }

    /**
     *
     * @param request данные запроса JSON
     * @param sql SQL-запрос
     * @param params параметры (фильтры, диапазон дат)
     * @return количество строк в запросе
     */
    private Long getTotalCount(AnalyticsRequest request, String sql, List<Object> params) {
        if (request.limit() != null) {
            String countSql = "SELECT COUNT(*) FROM (" + sql + ") as total";
            return jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());
        }
        return (long) jdbcTemplate.queryForList(sql, params.toArray()).size();
    }
}
