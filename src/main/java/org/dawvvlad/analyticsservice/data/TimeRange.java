package com.btlab.wings.module.recruitment.rest.cloudadmin.analytics.data;

import java.time.Instant;

/**
 * Диапазон времени
 * @param from от
 * @param to до
 */
public record TimeRange(
        Instant from,
        Instant to
) {}
