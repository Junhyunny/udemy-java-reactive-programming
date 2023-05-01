package com.example.chapter064;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {

    private static final Map<Integer, List<PurchaseOrder>> db = new HashMap<>();

    static {
        db.put(1, List.of(
                new PurchaseOrder(1),
                new PurchaseOrder(1),
                new PurchaseOrder(1)
        ));
        db.put(2, List.of(
                new PurchaseOrder(2),
                new PurchaseOrder(2)
        ));
    }

    public static Flux<PurchaseOrder> getOrders(int userId) {
        return Flux.create(
                        (FluxSink<PurchaseOrder> fluxSink) -> {
                            db.get(userId).forEach(fluxSink::next);
                            fluxSink.complete();
                        }
                )
                // 딜레이가 발생하면 먼저 emmit 된 데이터가 먼저 전달되지 않는다.
                .delayElements(Duration.ofSeconds(1));
    }
}
