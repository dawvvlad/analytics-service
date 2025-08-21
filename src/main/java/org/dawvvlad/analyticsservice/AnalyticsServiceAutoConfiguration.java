package org.dawvvlad.analyticsservice;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfiguration
@ConditionalOnClass
public class AnalyticsServiceAutoConfiguration {
}
