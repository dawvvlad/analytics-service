package com.btlab.wings.module.recruitment.rest.cloudadmin.analytics;

import com.btlab.wings.module.recruitment.rest.cloudadmin.analytics.data.AnalyticsRequest;
import com.btlab.wings.module.recruitment.rest.cloudadmin.analytics.data.AnalyticsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ws/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping
    public ResponseEntity<AnalyticsResponse> post(@RequestBody AnalyticsRequest analyticsRequest) throws Exception {
        return ResponseEntity.ok().body(analyticsService.getAnalyticsData(analyticsRequest));
    }
}
