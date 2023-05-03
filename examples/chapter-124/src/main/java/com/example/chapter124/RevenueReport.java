package com.example.chapter124;

import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@ToString
public class RevenueReport {

    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final Map<String, BigDecimal> revenue;

    public RevenueReport(Map<String, BigDecimal> revenue) {
        this.revenue = revenue;
    }
}
