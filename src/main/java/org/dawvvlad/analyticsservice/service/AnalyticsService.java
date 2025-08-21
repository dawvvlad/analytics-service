package com.btlab.wings.module.recruitment.rest.cloudadmin.analytics;

import com.btlab.wings.module.recruitment.rest.cloudadmin.analytics.data.AnalyticsRequest;
import com.btlab.wings.module.recruitment.rest.cloudadmin.analytics.data.AnalyticsResponse;

public interface AnalyticsService {
    AnalyticsResponse getAnalyticsData(AnalyticsRequest request) throws Exception;
}
