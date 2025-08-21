package org.dawvvlad.analyticsservice.data;

import java.util.List;
import java.util.Map;

public record AnalyticsResponse(
        List<Map<String, Object>> data,
        Metadata metadata
) {}
