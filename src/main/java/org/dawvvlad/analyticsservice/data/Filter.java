package org.dawvvlad.analyticsservice.data;

public record Filter(FilterType filterType, String field, FilterValue filterValue) {}
record FilterValue(String value, String type) {}
