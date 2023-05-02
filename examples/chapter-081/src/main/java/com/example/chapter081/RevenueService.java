package com.example.chapter081;

import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RevenueService {

    private final Map<String, BigDecimal> db = new HashMap<>();

    public RevenueService() {
        db.put("Kids", BigDecimal.valueOf(0));
        db.put("Automotive", BigDecimal.valueOf(0));
    }

    public Consumer<PurchaseOrder> subscribeOrderStream() {
        return p -> db.computeIfPresent(p.getCategory(), (key, value) -> value.add(p.getPrice()));
    }

    public Flux<String> revenueStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(number -> db.toString());
    }
}
