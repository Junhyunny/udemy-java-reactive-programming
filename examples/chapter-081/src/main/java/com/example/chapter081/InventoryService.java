package com.example.chapter081;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryService {

    private final Map<String, Integer> db = new HashMap<>();

    public InventoryService() {
        db.put("Kids", 100);
        db.put("Automotive", 100);
    }

    public Consumer<PurchaseOrder> subscribeOrderStream() {
        return p -> db.computeIfPresent(p.getCategory(), (key, value) -> value - p.getQuantity());
    }

    public Flux<String> inventoryStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(number -> db.toString());
    }
}
