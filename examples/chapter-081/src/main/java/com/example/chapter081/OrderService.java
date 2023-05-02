package com.example.chapter081;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Objects;

public class OrderService {

    private Flux<PurchaseOrder> flux;

    // 파이프라인을 새로 만드는 것을 방지하기 위한 싱글톤 패턴
    public Flux<PurchaseOrder> orderStream() {
        if (Objects.isNull(flux)) {
            flux = getOrderStream();
        }
        return flux;
    }

    private Flux<PurchaseOrder> getOrderStream() {
        return Flux.interval(Duration.ofMillis(250))
                .map(number -> new PurchaseOrder())
                .publish()
                .refCount(2);
    }
}
