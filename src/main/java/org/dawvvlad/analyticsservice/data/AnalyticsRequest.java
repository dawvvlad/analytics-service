package org.dawvvlad.analyticsservice.data;

import java.util.List;

/**
 * Запрос на построение аналитики
 * @param tableName наименование таблицы
 * @param measures меры (агрегирующие функции) (обязательное поле)
 * @param dimensions размеры (функции для форматирования и группировки)
 * @param filters фильтры (условия в блоке WHERE)
 * @param orders сортировка
 * @param timeRange диапазон дат для типа Dimension time
 * @param limit LIMIT
 * @param offset OFFSET
 * @param format запроса (необязательно)
 */
public record AnalyticsRequest(
        String tableName,
        List<Measure> measures,
        List<Dimension> dimensions,
        List<Filter> filters,
        List<Order> orders,
        TimeRange timeRange,
        Integer limit,
        Integer offset,
        String format
) {}

