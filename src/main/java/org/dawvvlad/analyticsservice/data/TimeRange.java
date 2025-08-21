package org.dawvvlad.analyticsservice.data;

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
