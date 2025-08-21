package org.dawvvlad.analyticsservice.service;


import org.dawvvlad.analyticsservice.data.AnalyticsRequest;
import org.dawvvlad.analyticsservice.data.AnalyticsResponse;

public interface AnalyticsService {
    AnalyticsResponse getAnalyticsData(AnalyticsRequest request) throws Exception;
}
