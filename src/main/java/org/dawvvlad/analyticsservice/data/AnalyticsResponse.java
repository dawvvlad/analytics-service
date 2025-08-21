package com.btlab.wings.module.recruitment.rest.cloudadmin.analytics.data;

import java.util.List;
import java.util.Map;

public record AnalyticsResponse(
        List<Map<String, Object>> data,
        Metadata metadata
) {}
