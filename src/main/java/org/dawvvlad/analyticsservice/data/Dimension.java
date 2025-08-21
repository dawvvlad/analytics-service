package com.btlab.wings.module.recruitment.rest.cloudadmin.analytics.data;

/**
 * Размеры - группировка и форматирование
 * @param field наименование поля
 * @param alias псевдоним поля (опционально)
 * @param type time или category. Если time -> используется TO_CHAR(DATE_TRUNC)

 * @param interval для типа time. Временной интервал (разрез)
 * <ul>
 *  <p>- Возможные варианты: </p>
 *  <li>1 month / month</li>
 *  <li>1 day / day</li>
 *  <li>1 year / year</li>
 *  <li>1 week / week</li>
 *  <li>1 hour / hour</li>
 * </ul>
 * @param format форматирование дат.
 *             <ul>
 *               <p>- Возможные варианты: </p>
 *               <li>YYYY-MM-DD</li>
 *               <li>DD/MM/YYYY</li>
 *               <li>Month DD, YYYY</li>
 *               <li>Month YYYY</li>
 *               <li>Mon YYYY</li>
 *               <li>YYYY-"Q"Q</li>
 *             </ul>
 */
public record Dimension(
        String field,
        String alias,
        String type, // "time", "category"
        String interval, // для time: "1 hour", "1 day", "1 month"
        String format // опциональное форматирование
) {}
