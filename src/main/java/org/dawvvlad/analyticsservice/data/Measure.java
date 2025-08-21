package org.dawvvlad.analyticsservice.data;

/**
 * Меры
 * @param field название поля таблицы
 * @param agg название аггрегирующей функции
 * <ul>
 *  <p>- Возможные варианты: </p>
 *  <li>sum</li>
 *  <li>count</li>
 *  <li>avg</li>
 *  <li>min</li>
 *  <li>max</li>
 * </ul>
 * @param alias опционально - псевдоним результирующего поля
 */
public record Measure(
        String field,
        String agg, // "count", "sum", "avg", "min", "max"
        String alias
) {}
