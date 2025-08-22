package org.dawvvlad.analyticsservice.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.dawvvlad.analyticsservice.data.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Построитель SQL-запроса из java-объектов
 * @see AnalyticsRequest
 * @see Dimension
 * @see Measure
 *
 * @author Golikov Vladislav
 */
@Log4j2
public class QueryBuilder {

    /**
     * Главный метод для построения запросов
     * @param request данные запроса
     * @return готовый SQL запрос
     */
    public String buildQuery(AnalyticsRequest request) {
        StringBuilder sql = new StringBuilder();

        // SELECT
        buildSelectClause(sql, request);

        // FROM
        sql.append(" FROM ").append(request.tableName());

        // WHERE
        buildWhereClause(sql, request);

        // GROUP BY
        buildGroupByClause(sql, request);

        // ORDER BY
        buildOrderByClause(sql, request);

        // LIMIT & OFFSET
        buildLimitOffsetClause(sql, request);

        return sql.toString();
    }

    public List<Object> buildParameters(AnalyticsRequest request) {
        List<Object> params = new ArrayList<>();

        if (request.timeRange() != null) {
            params.add(convertToTimestamp(request.timeRange().from()));
            params.add(convertToTimestamp(request.timeRange().to()));
        }

        if (request.filters() != null) {
            for (Filter filter : request.filters()) {
                addFilterParameters(params, filter);
            }
        }

        return params;
    }

    private Timestamp convertToTimestamp(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }

    /**
     * Строит блок SELECT
     * @param sql пустой StringBuilder для итогового запроса
     * @param request данные запроса
     */
    private void buildSelectClause(StringBuilder sql, AnalyticsRequest request) {
        sql.append("SELECT ");

        // Dimensions
        if (request.dimensions() != null) {
            for (Dimension dimension : request.dimensions()) {
                buildDimensionField(sql, dimension);
                sql.append(", ");
            }
        }

        // Measures
        for (Measure measure : request.measures()) {
            buildMeasureField(sql, measure);
            sql.append(", ");
        }

        // Убираем последнюю запятую
        if (sql.charAt(sql.length() - 2) == ',') {
            sql.setLength(sql.length() - 2);
        }
    }

    /**
     * Строит функции и форматирование для дат и добавляет GROUP BY
     * @param sql StringBuilder SQL-запроса
     * @param dimension блок Dimension из данных запроса
     */
    private void buildDimensionField(StringBuilder sql, Dimension dimension) {
        if ("time".equals(dimension.type()) && dimension.interval() != null) {
            // Для временных dimensions
            sql.append("TO_CHAR(DATE_TRUNC('")
                    .append(convertInterval(dimension.interval()))
                    .append("', ")
                    .append(dimension.field())
                    .append("), '");

            // Форматирование
            if (dimension.format() != null) {
                sql.append(dimension.format());
            } else {
                sql.append("YYYY-MM-DD"); // default format
            }

            sql.append("')");
        } else {
            // Обычные поля
            sql.append(dimension.field());
        }

        // добавляем alias
        if(StringUtils.isNotBlank(dimension.alias())) {
            sql.append(" AS ").append(dimension.alias());
        }
    }

    /**
     * Построение метрик
     * @param sql
     * @param measure
     */
    private void buildMeasureField(StringBuilder sql, Measure measure) {
        String aggregation = getAggregationFunction(measure.agg());
        sql.append(aggregation)
                .append("(")
                .append(measure.field())
                .append(")");
        if(StringUtils.isNotBlank(measure.alias())) {
            sql.append(" AS ").append(measure.alias());
        }
    }

    /**
     * Добавляет агрегирование в SQL-запрос
     * @param agg название
     */
    private String getAggregationFunction(String agg) {
        return switch (agg.toLowerCase()) {
            case "count" -> "COUNT";
            case "sum" -> "SUM";
            case "avg" -> "AVG";
            case "min" -> "MIN";
            case "max" -> "MAX";
            default -> "COUNT";
        };
    }

    /**
     * Строит блок WHERE
     * @param sql StringBuilder SQL-запроса
     * @param request данные запроса
     */
    private void buildWhereClause(StringBuilder sql, AnalyticsRequest request) {
        List<String> conditions = new ArrayList<>();

        // Time range - используем правильные имена полей
        if (request.timeRange() != null) {
            // Определяем какое поле использовать для времени
            String timeField = determineTimeField(request);
            conditions.add(timeField + " BETWEEN ? AND ?");
        }

        // Filters
        if (request.filters() != null) {
            for (Filter filter : request.filters()) {
                conditions.add(buildFilterCondition(filter));
            }
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
    }

    /**
     * Построение форматирования для типа Dimension 'time'
     * @param request данные запроса
     * @return
     */
    private String determineTimeField(AnalyticsRequest request) {
        // Если есть time dimension - используем её поле
        if (request.dimensions() != null) {
            for (Dimension dimension : request.dimensions()) {
                if ("time".equals(dimension.type())) {
                    return dimension.field();
                }
            }
        }
        // Иначе используем дефолтное
        return "creation_date";
    }

    /**
     * Построение фильтрации (добавление условий в блок WHERE)
     * @param filter объект блока filters из запроса
     * @see Filter
     * @return
     */
    private String buildFilterCondition(Filter filter) {
        StringBuilder condition = new StringBuilder(filter.field());
        switch (filter.filterType()) {
            case FilterType.EqualsFilter -> condition.append(" = ?");
            case FilterType.NotEqualsFilter -> condition.append(" != ?");
            case FilterType.GreaterThanFilter -> condition.append(" > ?");
            case FilterType.GreaterThanOrEqualsFilter -> condition.append(" >= ?");
            case FilterType.LessThanFilter -> condition.append(" < ?");
            case FilterType.LessThanOrEqualsFilter -> condition.append(" <= ?");
            case FilterType.ContainsFilter -> condition.append(" LIKE %?%");
            case FilterType.BetweenFilter -> condition.append(" BETWEEN ? AND ?");
            case FilterType.InFilter -> condition.append(" IN (?)");
            case FilterType.NotInFilter -> condition.append(" NOT IN (?)");
            case FilterType.EndsWithFilter -> condition.append(" LIKE ?%");
            case FilterType.StartsWithFilter -> condition.append(" LIKE %?");
            default -> condition.append(" IS NULL");
        }
        return condition.toString();
    }

    /**
     * Добавление параметров в фильтр
     * @param params
     * @see Filter
     * @param filter
     */
    private void addFilterParameters(List<Object> params, Filter filter) {

    }

    /**
     * Конвертирует EntityField в соответствующий Java тип для SQL
     */
    private Object convertValue(Object entityField) {
        return entityField;
    }

    /**
     * Парсит строку в Timestamp с учетом разных форматов
     */
    private Timestamp parseTimestamp(String value) {
        try {
            // Пробуем разные форматы дат
            try {
                return Timestamp.valueOf(value);
            } catch (Exception e1) {
                try {
                    // Для ISO формата: 2024-01-01T00:00:00Z
                    Instant instant = Instant.parse(value);
                    return Timestamp.from(instant);
                } catch (Exception e2) {
                    // Для простых дат: 2024-01-01
                    java.sql.Date date = java.sql.Date.valueOf(value);
                    return new Timestamp(date.getTime());
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + value);
        }
    }

    private void buildGroupByClause(StringBuilder sql, AnalyticsRequest request) {
        if (request.dimensions() != null && !request.dimensions().isEmpty()) {
            sql.append(" GROUP BY ");

            for (int i = 0; i < request.dimensions().size(); i++) {
                Dimension dimension = request.dimensions().get(i);

                if ("time".equals(dimension.type()) && dimension.interval() != null) {
                    // Для временных dimensions группируем по оригинальному выражению
                    sql.append("DATE_TRUNC('")
                            .append(convertInterval(dimension.interval()))
                            .append("', ")
                            .append(dimension.field())
                            .append(")");
                } else {
                    // Для обычных полей
                    sql.append(dimension.field());
                }

                if (i < request.dimensions().size() - 1) {
                    sql.append(", ");
                }
            }
        }
    }

    private void buildOrderByClause(StringBuilder sql, AnalyticsRequest request) {
        if (request.orders() != null && !request.orders().isEmpty()) {
            sql.append(" ORDER BY ");

            for (int i = 0; i < request.orders().size(); i++) {
                Order order = request.orders().get(i);

                // Используем поле из SELECT (а не оригинальное имя)
                if (i < request.orders().size() - 1) {
                    sql.append(", ");
                }
            }
        }
    }

    /**
     * Добавление блоков LIMIT и OFFSET
     * @param sql
     * @param request
     */
    private void buildLimitOffsetClause(StringBuilder sql, AnalyticsRequest request) {
        if (request.limit() != null) {
            sql.append(" LIMIT ").append(request.limit());
        }
        if (request.offset() != null) {
            sql.append(" OFFSET ").append(request.offset());
        }
    }

    /**
     * Конвертирование дат
     * @param interval интервал дат из запроса
     * @return наименование строки форматирования
     */
    private String convertInterval(String interval) {
        return switch (interval.toLowerCase()) {
            case "1 hour", "hour" -> "hour";
            case "1 day", "day" -> "day";
            case "1 week", "week" -> "week";
            case "1 month", "month" -> "month";
            case "1 year", "year" -> "year";
            default -> "day";
        };
    }
}
